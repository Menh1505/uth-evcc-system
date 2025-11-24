package com.evcc.voting.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evcc.voting.entity.Vote;
import com.evcc.voting.enums.VoteStatus;
import com.evcc.voting.enums.VoteType;

/**
 * Repository cho Vote entity
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    /**
     * Tìm tất cả vote của một nhóm
     */
    List<Vote> findByGroup_IdOrderByCreatedAtDesc(Long groupId);

    /**
     * Tìm vote của nhóm với phân trang
     */
    Page<Vote> findByGroup_IdOrderByCreatedAtDesc(Long groupId, Pageable pageable);

    /**
     * Tìm vote theo trạng thái
     */
    List<Vote> findByGroup_IdAndStatusOrderByCreatedAtDesc(Long groupId, VoteStatus status);

    /**
     * Tìm vote theo loại
     */
    List<Vote> findByGroup_IdAndVoteTypeOrderByCreatedAtDesc(Long groupId, VoteType voteType);

    /**
     * Tìm vote đang hoạt động của nhóm
     */
    @Query("SELECT v FROM Vote v WHERE v.group.id = :groupId "
            + "AND v.status = 'ACTIVE' "
            + "AND v.startTime <= :now "
            + "AND v.endTime > :now "
            + "ORDER BY v.endTime ASC")
    List<Vote> findActiveVotes(@Param("groupId") Long groupId, @Param("now") LocalDateTime now);

    /**
     * Tìm vote đã hết hạn nhưng chưa được xử lý
     */
    @Query("SELECT v FROM Vote v WHERE v.status = 'ACTIVE' "
            + "AND v.endTime < :now")
    List<Vote> findExpiredActiveVotes(@Param("now") LocalDateTime now);

    /**
     * Tìm vote mà user đã tham gia
     */
    @Query("SELECT DISTINCT v FROM Vote v "
            + "JOIN v.options o "
            + "JOIN o.voteRecords vr "
            + "WHERE v.group.id = :groupId "
            + "AND vr.user.id = :userId "
            + "ORDER BY v.createdAt DESC")
    List<Vote> findVotesUserParticipated(@Param("groupId") Long groupId,
            @Param("userId") UUID userId);

    /**
     * Tìm vote mà user chưa tham gia
     */
    @Query("SELECT v FROM Vote v WHERE v.group.id = :groupId "
            + "AND v.status = 'ACTIVE' "
            + "AND v.startTime <= :now "
            + "AND v.endTime > :now "
            + "AND v.id NOT IN ("
            + "    SELECT DISTINCT vr.vote.id FROM VoteRecord vr WHERE vr.user.id = :userId"
            + ") "
            + "ORDER BY v.endTime ASC")
    List<Vote> findPendingVotesForUser(@Param("groupId") Long groupId,
            @Param("userId") UUID userId,
            @Param("now") LocalDateTime now);

    /**
     * Đếm số vote theo trạng thái của nhóm
     */
    long countByGroup_IdAndStatus(Long groupId, VoteStatus status);

    /**
     * Tìm vote theo entity liên quan
     */
    List<Vote> findByRelatedEntityTypeAndRelatedEntityId(String entityType, Long entityId);

    /**
     * Tìm vote được tạo bởi user
     */
    List<Vote> findByCreatedBy_IdAndGroup_IdOrderByCreatedAtDesc(UUID createdById, Long groupId);

    /**
     * Kiểm tra xem có vote nào đang active cho entity này không
     */
    @Query("SELECT COUNT(v) > 0 FROM Vote v WHERE "
            + "v.relatedEntityType = :entityType "
            + "AND v.relatedEntityId = :entityId "
            + "AND v.status = 'ACTIVE' "
            + "AND v.startTime <= :now "
            + "AND v.endTime > :now")
    boolean existsActiveVoteForEntity(@Param("entityType") String entityType,
            @Param("entityId") Long entityId,
            @Param("now") LocalDateTime now);
}
