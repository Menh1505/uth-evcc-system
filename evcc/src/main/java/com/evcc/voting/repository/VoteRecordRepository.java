package com.evcc.voting.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evcc.voting.entity.VoteRecord;

/**
 * Repository cho VoteRecord entity
 */
@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    /**
     * Tìm vote record của user trong một vote
     */
    Optional<VoteRecord> findByVote_IdAndUser_Id(Long voteId, UUID userId);

    /**
     * Kiểm tra user đã vote chưa
     */
    boolean existsByVote_IdAndUser_Id(Long voteId, UUID userId);

    /**
     * Tìm tất cả vote records của một vote
     */
    List<VoteRecord> findByVote_IdOrderByVotedAtAsc(Long voteId);

    /**
     * Tìm tất cả vote records của user
     */
    List<VoteRecord> findByUser_IdOrderByVotedAtDesc(UUID userId);

    /**
     * Tìm vote records theo option
     */
    List<VoteRecord> findByVoteOption_IdOrderByVotedAtAsc(Long optionId);

    /**
     * Đếm số vote của một option
     */
    long countByVoteOption_Id(Long optionId);

    /**
     * Đếm số user đã vote trong một vote
     */
    long countDistinctByVote_Id(Long voteId);

    /**
     * Lấy danh sách user đã vote trong vote này
     */
    @Query("SELECT DISTINCT vr.user.id FROM VoteRecord vr WHERE vr.vote.id = :voteId")
    List<UUID> findUserIdsWhoVoted(@Param("voteId") Long voteId);

    /**
     * Xóa vote record của user (cho phép thay đổi vote)
     */
    void deleteByVote_IdAndUser_Id(Long voteId, UUID userId);
}
