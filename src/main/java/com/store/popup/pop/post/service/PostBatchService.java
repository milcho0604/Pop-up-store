package com.store.popup.pop.post.service;

import com.store.popup.pop.post.domain.Post;
import com.store.popup.pop.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostBatchService {

    private final PostRepository postRepository;
    @Qualifier("redisTemplateDb7")
    private final RedisTemplate<String, Object> redisTemplate;



//     12시간마다 실행: 12시간(43200000ms) 간격으로 스케줄 실행
    @Scheduled(fixedRate = 43200000)
    // 1분마다 실행
//    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updatePostViewsAndLikesToDB() {
        Set<String> keys = redisTemplate.keys("post:*");

        if (keys != null) {
            for (String key : keys) {
                if (key.startsWith("post:views:")) {
                    Long postId = extractPostId(key);
                    Long views = getPostViewsFromRedis(postId);
                    if (views != null && views > 0) {
                        Post post = postRepository.findById(postId)
                                .orElse(null);  // 게시글이 없을 때 null 반환
                        if (post != null) {
                            post.updateViewCount(views);
                            postRepository.save(post);
                        } else {
                            // 게시글이 없는 경우 Redis에서 해당 키 삭제
                            System.out.println("Post not found: " + postId + ". Deleting Redis key: " + key);
                            redisTemplate.delete(key);
                        }
                    }
                }

                if (key.startsWith("post:likes:")) {
                    Long postId = extractPostId(key);
                    Long likes = getPostLikesFromRedis(postId);
                    if (likes != null && likes > 0) {
                        Post post = postRepository.findById(postId)
                                .orElse(null);  // 게시글이 없을 때 null 반환
                        if (post != null) {
                            post.updateLikeCount(likes);
                            postRepository.save(post);
                        } else {
                            // 게시글이 없는 경우 Redis에서 해당 키 삭제
                            System.out.println("Post not found: " + postId + ". Deleting Redis key: " + key);
                            redisTemplate.delete(key);
                        }
                    }
                }
            }
        }
    }

    // Redis에서 조회수 가져오기
    private Long getPostViewsFromRedis(Long postId) {
        String key = "post:views:" + postId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        }
        // 기본값을 0으로 설정하여 null 방지
        return 0L;
    }

    // Redis에서 좋아요 수 가져오기
    private Long getPostLikesFromRedis(Long postId) {
        String key = "post:likes:" + postId;
        Object value = redisTemplate.opsForSet().size(key);
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        }
        // 기본값을 0으로 설정하여 null 방지
        return 0L;
    }

    // Redis 키에서 Post ID 추출
    public Long extractPostId(String redisKey) {
        try {
            // Redis key에서 숫자만 추출
            String[] parts = redisKey.split(":");
            for (String part : parts) {
                if (part.matches("\\d+")) { // 숫자인지 확인
                    return Long.parseLong(part); // 숫자만 Long으로 변환
                }
            }
            throw new NumberFormatException("유효한 Post ID를 찾을 수 없습니다.");
        } catch (NumberFormatException e) {
            throw new RuntimeException("Post ID를 추출하는데 실패했습니다. Redis Key: " + redisKey, e);
        }
    }
}
