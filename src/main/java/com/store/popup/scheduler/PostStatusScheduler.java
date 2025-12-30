package com.store.popup.scheduler;

import com.store.popup.pop.domain.Post;
import com.store.popup.pop.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Post 상태를 자동으로 업데이트하는 스케줄러
 * 매 시간마다 실행되어 Post의 startDate, endDate를 기준으로 상태를 갱신합니다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PostStatusScheduler {

    private final PostRepository postRepository;

    /**
     * 매 시간 정각에 Post 상태 업데이트
     * Cron 표현식: "초 분 시 일 월 요일"
     * "0 0 * * * *" = 매 시간 0분 0초
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updatePostStatus() {
        log.info("Post 상태 자동 업데이트 시작");

        List<Post> posts = postRepository.findByDeletedAtIsNull();
        int updatedCount = 0;

        for (Post post : posts) {
            if (post.getStartDate() != null && post.getEndDate() != null) {
                post.updateStatusByDate();
                updatedCount++;
            }
        }

        log.info("Post 상태 자동 업데이트 완료 - 총 {}개 Post 처리됨", updatedCount);
    }
}
