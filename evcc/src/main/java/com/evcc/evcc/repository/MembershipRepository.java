package com.evcc.evcc.repository;

import com.evcc.evcc.entity.GroupRole;
import com.evcc.evcc.entity.Membership;
import com.evcc.evcc.entity.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    /**
     * Find membership by group and user
     */
    Optional<Membership> findByGroupIdAndUserId(UUID groupId, UUID userId);

    /**
     * Find memberships by group ID
     */
    List<Membership> findByGroupId(UUID groupId);

    /**
     * Find memberships by user ID
     */
    List<Membership> findByUserId(UUID userId);

    /**
     * Find memberships by status
     */
    List<Membership> findByStatus(MembershipStatus status);

    /**
     * Find memberships by role
     */
    List<Membership> findByRole(GroupRole role);

    /**
     * Find active memberships by group ID
     */
    List<Membership> findByGroupIdAndStatus(UUID groupId, MembershipStatus status);

    /**
     * Find active memberships by user ID
     */
    List<Membership> findByUserIdAndStatus(UUID userId, MembershipStatus status);

    /**
     * Check if membership exists between user and group
     */
    boolean existsByGroupIdAndUserId(UUID groupId, UUID userId);

    /**
     * Check if active membership exists
     */
    boolean existsByGroupIdAndUserIdAndStatus(UUID groupId, UUID userId, MembershipStatus status);

    /**
     * Find group owners
     */
    List<Membership> findByGroupIdAndRole(UUID groupId, GroupRole role);

    /**
     * Count active members in group
     */
    long countByGroupIdAndStatus(UUID groupId, MembershipStatus status);

    /**
     * Count user's active memberships
     */
    long countByUserIdAndStatus(UUID userId, MembershipStatus status);

    /**
     * Custom query to find memberships with share percentage
     */
    @Query("SELECT m FROM Membership m WHERE m.sharePct IS NOT NULL AND m.sharePct > 0")
    List<Membership> findMembershipsWithShares();

    /**
     * Find memberships by group ID with users
     */
    @Query("SELECT m FROM Membership m JOIN FETCH m.user WHERE m.groupId = :groupId")
    List<Membership> findByGroupIdWithUsers(@Param("groupId") UUID groupId);

    /**
     * Find memberships by user ID with groups
     */
    @Query("SELECT m FROM Membership m JOIN FETCH m.group WHERE m.userId = :userId")
    List<Membership> findByUserIdWithGroups(@Param("userId") UUID userId);

    /**
     * Native SQL to find recent memberships
     */
    @Query(value = "SELECT * FROM memberships WHERE joined_at >= CURRENT_DATE - INTERVAL '7 days'", nativeQuery = true)
    List<Membership> findRecentMemberships();
}
