package com.store.popup.pop.service;

import com.store.popup.pop.domain.Post;
import com.store.popup.pop.dto.PostListDto;
import com.store.popup.pop.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PostMetricsService {

    @Qualifier("redisTemplateDb7")
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRepository postRepository;
    // Redis 조회수 증가 로직
    public void incrementPostViews(Long postId) {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        String key = "post:views:" + postId;
        String userKey = "post:views:users:" + postId;

        Boolean hasViewed = redisTemplate.opsForSet().isMember(userKey, memberEmail);
        if (!Boolean.TRUE.equals(hasViewed)) {
            redisTemplate.opsForValue().increment(key, 1);  // 조회수 1 증가
//            redisTemplate.opsForSet().add(userKey, memberEmail);  // 중복 방지용 유저 이메일 저장 추후 추가할 수 있음
        }
    }

    // Redis 조회수 조회
    public Long getPostViews(Long postId) {
        String key = "post:views:" + postId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                log.warn("Redis 값 파싱 실패 key={}, value={}", key, value);
            }
        }
        return 0L;
    }

    // 좋아요 추가
    public void likePost(Long postId) {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String key = "post:likes:" + postId;

        if (!redisTemplate.opsForSet().isMember(key, memberEmail)) {
            redisTemplate.opsForSet().add(key, memberEmail);
        }
    }

    // 좋아요 취소
    public void unlikePost(Long postId) {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String key = "post:likes:" + postId;

        if (redisTemplate.opsForSet().isMember(key, memberEmail)) {
            redisTemplate.opsForSet().remove(key, memberEmail);
        }
    }

    // Redis 좋아요 수 조회
    public Long getPostLikesCount(Long postId) {
        String key = "post:likes:" + postId;
        Object value = redisTemplate.opsForSet().size(key);
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                log.warn("Redis 값 파싱 실패 key={}, value={}", key, value);
            }
        }
        return 0L;
    }

    // 좋아요 많은 게시글
    public List<PostListDto> famousPostList() {
        List<Post> posts = postRepository.findByDeletedAtIsNull();
        // Post -> PostListDto로 변환
        List<PostListDto> postListDtoList = posts.stream().map(post -> {
            Long viewCount = getPostViews(post.getId());   // Redis에서 조회수 가져오기
            Long likeCount = getPostLikesCount(post.getId());   // Redis에서 좋아요 수 가져오기
            return post.listFromEntity(viewCount, likeCount);   // 조회수와 좋아요 수를 포함한 DTO로 변환
        }).collect(Collectors.toList());// 리스트로 변환

        postListDtoList.sort(Comparator.comparingDouble(PostListDto::getLikeCount).reversed());
        if (postListDtoList.size() > 3) {
            postListDtoList = postListDtoList.subList(0,3);
        }
        return postListDtoList;
    }
}
