package com.evcc.voting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evcc.voting.entity.VoteOption;

/**
 * Repository cho VoteOption entity
 */
@Repository
public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {

    /**
     * Tìm tất cả option của một vote
     */
    List<VoteOption> findByVote_IdOrderByDisplayOrderAsc(Long voteId);

    /**
     * Tìm option có nhiều vote nhất
     */
    @Query("SELECT vo FROM VoteOption vo "
            + "WHERE vo.vote.id = :voteId "
            + "AND SIZE(vo.voteRecords) = ("
            + "    SELECT MAX(SIZE(vo2.voteRecords)) FROM VoteOption vo2 "
            + "    WHERE vo2.vote.id = :voteId"
            + ")")
    List<VoteOption> findWinningOptions(@Param("voteId") Long voteId);

    /**
     * Đếm tổng số vote của một vote
     */
    @Query("SELECT SUM(SIZE(vo.voteRecords)) FROM VoteOption vo WHERE vo.vote.id = :voteId")
    Long countTotalVotesByVoteId(@Param("voteId") Long voteId);
}
