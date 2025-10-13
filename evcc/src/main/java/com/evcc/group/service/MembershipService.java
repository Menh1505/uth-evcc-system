package com.evcc.group.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.group.entity.GroupRole;
import com.evcc.group.entity.Membership;
import com.evcc.group.entity.MembershipStatus;
import com.evcc.group.repository.MembershipRepository;




@Service
@Transactional
public class MembershipService {

    @Autowired
    private MembershipRepository membershipRepository;

    /**
     * Get all memberships
     */
    public List<Membership> getAllMemberships() {
        return membershipRepository.findAll();
    }

    /**
     * Find membership by ID
     */
    public Optional<Membership> findMembershipById(UUID id) {
        return membershipRepository.findById(id);
    }

    /**
     * Find membership by group and user
     */
    public Optional<Membership> findMembershipByGroupAndUser(UUID groupId, UUID userId) {
        return membershipRepository.findByGroupIdAndUserId(groupId, userId);
    }

    /**
     * Create a new membership
     */
    public Membership createMembership(UUID groupId, UUID userId, GroupRole role, BigDecimal sharePct) {
        // Check if membership already exists
        if (membershipRepository.existsByGroupIdAndUserIdAndStatus(groupId, userId, MembershipStatus.ACTIVE)) {
            throw new RuntimeException("Active membership already exists for user " + userId + " in group " + groupId);
        }

        Membership membership = new Membership(groupId, userId, role, sharePct);
        return membershipRepository.save(membership);
    }

    /**
     * Create a membership without share percentage
     */
    public Membership createMembership(UUID groupId, UUID userId, GroupRole role) {
        return createMembership(groupId, userId, role, null);
    }

    /**
     * Update membership
     */
    public Membership updateMembership(UUID id, GroupRole role, BigDecimal sharePct, MembershipStatus status) {
        Optional<Membership> membershipOpt = membershipRepository.findById(id);
        if (membershipOpt.isEmpty()) {
            throw new RuntimeException("Membership not found with id: " + id);
        }

        Membership membership = membershipOpt.get();

        if (role != null) {
            membership.setRole(role);
        }
        if (sharePct != null) {
            membership.setSharePct(sharePct);
        }
        if (status != null) {
            membership.setStatus(status);
            
            // Set leftAt when status changes to LEFT, KICKED, or BANNED
            if (status == MembershipStatus.LEFT || status == MembershipStatus.KICKED) {
                membership.setLeftAt(LocalDateTime.now());
            } else if (status == MembershipStatus.ACTIVE && membership.getLeftAt() != null) {
                // Clear leftAt when reactivating
                membership.setLeftAt(null);
            }
        }

        return membershipRepository.save(membership);
    }

    /**
     * Leave group (set status to LEFT)
     */
    public Membership leaveGroup(UUID groupId, UUID userId) {
        Optional<Membership> membershipOpt = membershipRepository.findByGroupIdAndUserId(groupId, userId);
        if (membershipOpt.isEmpty()) {
            throw new RuntimeException("Membership not found for user " + userId + " in group " + groupId);
        }

        Membership membership = membershipOpt.get();
        membership.setStatus(MembershipStatus.LEFT);
        membership.setLeftAt(LocalDateTime.now());

        return membershipRepository.save(membership);
    }

    /**
     * Kick member from group
     */
    public Membership kickMember(UUID groupId, UUID userId) {
        Optional<Membership> membershipOpt = membershipRepository.findByGroupIdAndUserId(groupId, userId);
        if (membershipOpt.isEmpty()) {
            throw new RuntimeException("Membership not found for user " + userId + " in group " + groupId);
        }

        Membership membership = membershipOpt.get();
        membership.setStatus(MembershipStatus.KICKED);
        membership.setLeftAt(LocalDateTime.now());

        return membershipRepository.save(membership);
    }

    /**
     * Delete membership
     */
    public boolean deleteMembership(UUID id) {
        if (membershipRepository.existsById(id)) {
            membershipRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get memberships by group ID
     */
    public List<Membership> getMembershipsByGroupId(UUID groupId) {
        return membershipRepository.findByGroupId(groupId);
    }

    /**
     * Get active memberships by group ID
     */
    public List<Membership> getActiveMembershipsByGroupId(UUID groupId) {
        return membershipRepository.findByGroupIdAndStatus(groupId, MembershipStatus.ACTIVE);
    }

    /**
     * Get memberships by user ID
     */
    public List<Membership> getMembershipsByUserId(UUID userId) {
        return membershipRepository.findByUserId(userId);
    }

    /**
     * Get active memberships by user ID
     */
    public List<Membership> getActiveMembershipsByUserId(UUID userId) {
        return membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.ACTIVE);
    }

    /**
     * Get memberships by status
     */
    public List<Membership> getMembershipsByStatus(MembershipStatus status) {
        return membershipRepository.findByStatus(status);
    }

    /**
     * Get memberships by role
     */
    public List<Membership> getMembershipsByRole(GroupRole role) {
        return membershipRepository.findByRole(role);
    }

    /**
     * Get group owners
     */
    public List<Membership> getGroupOwners(UUID groupId) {
        return membershipRepository.findByGroupIdAndRole(groupId, GroupRole.CO_OWNER);
    }

    /**
     * Check if user is member of group
     */
    public boolean isUserMemberOfGroup(UUID groupId, UUID userId) {
        return membershipRepository.existsByGroupIdAndUserId(groupId, userId);
    }

    /**
     * Check if user has active membership in group
     */
    public boolean isUserActiveMemberOfGroup(UUID groupId, UUID userId) {
        return membershipRepository.existsByGroupIdAndUserIdAndStatus(groupId, userId, MembershipStatus.ACTIVE);
    }

    /**
     * Get total membership count
     */
    public long getTotalMemberships() {
        return membershipRepository.count();
    }

    /**
     * Get active membership count for group
     */
    public long getActiveMembershipCount(UUID groupId) {
        return membershipRepository.countByGroupIdAndStatus(groupId, MembershipStatus.ACTIVE);
    }

    /**
     * Get user's active membership count
     */
    public long getUserActiveMembershipCount(UUID userId) {
        return membershipRepository.countByUserIdAndStatus(userId, MembershipStatus.ACTIVE);
    }

    /**
     * Get memberships with share percentages
     */
    public List<Membership> getMembershipsWithShares() {
        return membershipRepository.findMembershipsWithShares();
    }

    /**
     * Get recent memberships (last 7 days)
     */
    public List<Membership> getRecentMemberships() {
        return membershipRepository.findRecentMemberships();
    }
}
