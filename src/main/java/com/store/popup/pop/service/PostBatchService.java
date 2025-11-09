package com.store.popup.pop.service;

import com.store.popup.pop.domain.Post;
import com.store.popup.pop.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostBatchService {

    private final PostRepository postRepository;

    /** DB7 전용 템플릿 주입 */
    private final @Qualifier("redisTemplateDb7") RedisTemplate<String, Object> redisTemplate;

    private static final String K_VIEWS       = "post:views:";
    private static final String K_VIEWS_USERS = "post:views:users:";
    private static final String K_LIKES       = "post:likes:";
    private static final String K_LIKES_USERS = "post:likes:users:";

    private static final Pattern P_ID = Pattern.compile("^post:(views|likes)(:users)?:([0-9]+)$");

    private Long asLong(Object v) { return (v instanceof Number) ? ((Number) v).longValue() : 0L; }

    /** KEYS → SCAN (비차단 순회) */
    private Set<String> scanKeys(String pattern, long countHint) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> results = new HashSet<>();
            ScanOptions options = ScanOptions.scanOptions()
                    .match(pattern)
                    .count(countHint)   // 힌트값(보장 아님)
                    .build();
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    results.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            }
            return results;
        });
    }

    private Long extractPostIdStrict(String key) {
        var m = P_ID.matcher(key);
        if (m.matches()) return Long.parseLong(m.group(3));
        throw new IllegalArgumentException("Unexpected Redis key: " + key);
    }

    private Long getViewsFromRedis(Long postId) {
        Object v = redisTemplate.opsForValue().get(K_VIEWS + postId);
        return asLong(v);
    }

    private Long getLikesFromRedis(Long postId) {
        Object v = redisTemplate.opsForSet().size(K_LIKES + postId);
        return asLong(v);
    }

    /**
     * 이전 실행이 끝난 뒤 12시간 대기 후 재실행 (겹침 방지)
     * 운영 다중 인스턴스라면 ShedLock 등 분산락 추가 권장
     */
    @Scheduled(fixedDelay = 43200000)
    @Transactional
    public void updatePostViewsAndLikesToDB() {
        // views/likes 각각 스캔 (users 키는 코드에서 필터링)
        Set<String> viewKeys = scanKeys("post:views:*", 1000);
        Set<String> likeKeys = scanKeys("post:likes:*", 1000);

        // 키 → id 매핑
        Map<Long, Long> viewMap = new HashMap<>();
        for (String key : viewKeys) {
            if (key.startsWith(K_VIEWS_USERS)) continue; // users 세트는 건너뜀
            Long id = extractPostIdStrict(key);
            Long views = getViewsFromRedis(id);
            if (views != null && views >= 0) viewMap.put(id, views);
        }

        Map<Long, Long> likeMap = new HashMap<>();
        for (String key : likeKeys) {
            if (key.startsWith(K_LIKES_USERS)) continue; // users 세트는 건너뜀
            Long id = extractPostIdStrict(key);
            Long likes = getLikesFromRedis(id);
            if (likes != null && likes >= 0) likeMap.put(id, likes);
        }

        if (viewMap.isEmpty() && likeMap.isEmpty()) return;

        // DB에서 대상 포스트 일괄 조회
        List<Long> ids = Stream.concat(viewMap.keySet().stream(), likeMap.keySet().stream())
                .distinct().collect(Collectors.toList());

        List<Post> posts = postRepository.findAllById(ids);
        if (posts.isEmpty()) return;

        for (Post p : posts) {
            Long id = p.getId();
            Long v = viewMap.get(id);
            Long l = likeMap.get(id);
            if (v != null && v >= 0) p.updateViewCount(v);   // Redis의 누적값을 DB와 동기화
            if (l != null && l >= 0) p.updateLikeCount(l);   // 동일
        }

        postRepository.saveAll(posts);

        // 정책상 Redis 값을 유지(=실시간 노출)하고, DB만 동기화한다.
        // 만약 “배치 후 Redis 초기화”가 필요하면 아래 주석을 해제하고 정책에 맞게 조정.
        // for (Long id : viewMap.keySet()) {
        //     redisTemplate.delete(K_VIEWS + id);
        //     // users 세트를 삭제하면 동일 사용자의 재조회가 다시 카운팅되므로, 보통 유지한다.
        // }
        // for (Long id : likeMap.keySet()) {
        //     redisTemplate.delete(K_LIKES + id);
        //     // users 세트 삭제 시 중복 좋아요 재발생 여지 → 유지 권장
        // }
    }
}
