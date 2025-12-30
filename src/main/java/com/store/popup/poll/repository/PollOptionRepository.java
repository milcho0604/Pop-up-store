package com.store.popup.poll.repository;

import com.store.popup.poll.domain.Poll;
import com.store.popup.poll.domain.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollOptionRepository extends JpaRepository<PollOption, Long> {

    // 특정 Poll의 모든 선택지 조회
    List<PollOption> findByPollAndDeletedAtIsNullOrderByIdAsc(Poll poll);
}
