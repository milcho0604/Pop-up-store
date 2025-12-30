package com.store.popup.poll.repository;

import com.store.popup.member.domain.Member;
import com.store.popup.poll.domain.Poll;
import com.store.popup.poll.domain.PollOption;
import com.store.popup.poll.domain.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollVoteRepository extends JpaRepository<PollVote, Long> {

    // 특정 Poll에 특정 Member가 투표했는지 확인
    boolean existsByPollAndMemberAndDeletedAtIsNull(Poll poll, Member member);

    // 특정 Poll에 특정 Member의 투표 기록 조회
    List<PollVote> findByPollAndMemberAndDeletedAtIsNull(Poll poll, Member member);

    // 특정 Member의 모든 투표 기록 조회
    List<PollVote> findByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(Member member);

    // 특정 PollOption에 대한 투표 찾기
    Optional<PollVote> findByPollOptionAndMemberAndDeletedAtIsNull(PollOption pollOption, Member member);

    // 특정 Poll의 총 투표수 집계
    @Query("SELECT COUNT(pv) FROM PollVote pv WHERE pv.poll.id = :pollId AND pv.deletedAt IS NULL")
    Long countByPollId(@Param("pollId") Long pollId);
}
