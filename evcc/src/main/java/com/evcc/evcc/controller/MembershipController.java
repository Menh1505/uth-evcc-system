package com.evcc.evcc.controller;

import com.evcc.evcc.entity.GroupRole;
import com.evcc.evcc.entity.Membership;
import com.evcc.evcc.entity.MembershipStatus;
import com.evcc.evcc.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    /**
     * Get all memberships
     */
    @GetMapping
    public ResponseEntity<List<Membership>> getAllMemberships() {
        List<Membership> memberships = membershipService.getAllMemberships();
        return ResponseEntity.ok(memberships);
    }

    /**
     * Get membership by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Membership> getMembershipById(@PathVariable UUID id) {
        Optional<Membership> membership = membershipService.findMembershipById(id);
        return membership.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get membership by group and user
     */
    @GetMapping("/group/{groupId}/user/{userId}")
    public ResponseEntity<Membership> getMembershipByGroupAndUser(
            @PathVariable UUID groupId, 
            @PathVariable UUID userId) {
        Optional<Membership> membership = membershipService.findMembershipByGroupAndUser(groupId, userId);
        return membership.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new membership
     */
    @PostMapping
    public ResponseEntity<Object> createMembership(@RequestBody Map<String, String> membershipRequest) {
        try {
            String groupIdStr = membershipRequest.get("groupId");
            String userIdStr = membershipRequest.get("userId");
            String roleStr = membershipRequest.get("role");
            String sharePctStr = membershipRequest.get("sharePct");

            if (groupIdStr == null || userIdStr == null || roleStr == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "groupId, userId, and role are required"
                ));
            }

            UUID groupId, userId;
            GroupRole role;
            
            try {
                groupId = UUID.fromString(groupIdStr);
                userId = UUID.fromString(userIdStr);
                role = GroupRole.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Invalid UUID format or role"
                ));
            }

            BigDecimal sharePct = null;
            if (sharePctStr != null && !sharePctStr.trim().isEmpty()) {
                try {
                    sharePct = new BigDecimal(sharePctStr);
                    if (sharePct.compareTo(BigDecimal.ZERO) < 0 || sharePct.compareTo(BigDecimal.valueOf(100)) > 0) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "error", "Share percentage must be between 0 and 100"
                        ));
                    }
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Invalid share percentage format"
                    ));
                }
            }

            Membership membership = membershipService.createMembership(groupId, userId, role, sharePct);
            return ResponseEntity.ok(membership);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Update membership
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMembership(
            @PathVariable UUID id,
            @RequestBody Map<String, String> membershipRequest) {
        try {
            String roleStr = membershipRequest.get("role");
            String sharePctStr = membershipRequest.get("sharePct");
            String statusStr = membershipRequest.get("status");

            GroupRole role = null;
            if (roleStr != null && !roleStr.trim().isEmpty()) {
                try {
                    role = GroupRole.valueOf(roleStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Invalid role: " + roleStr
                    ));
                }
            }

            BigDecimal sharePct = null;
            if (sharePctStr != null && !sharePctStr.trim().isEmpty()) {
                try {
                    sharePct = new BigDecimal(sharePctStr);
                    if (sharePct.compareTo(BigDecimal.ZERO) < 0 || sharePct.compareTo(BigDecimal.valueOf(100)) > 0) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "error", "Share percentage must be between 0 and 100"
                        ));
                    }
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Invalid share percentage format"
                    ));
                }
            }

            MembershipStatus status = null;
            if (statusStr != null && !statusStr.trim().isEmpty()) {
                try {
                    status = MembershipStatus.valueOf(statusStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Invalid status: " + statusStr
                    ));
                }
            }

            Membership membership = membershipService.updateMembership(id, role, sharePct, status);
            return ResponseEntity.ok(membership);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Leave group
     */
    @PostMapping("/leave")
    public ResponseEntity<Object> leaveGroup(@RequestBody Map<String, String> request) {
        try {
            String groupIdStr = request.get("groupId");
            String userIdStr = request.get("userId");

            if (groupIdStr == null || userIdStr == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "groupId and userId are required"
                ));
            }

            UUID groupId = UUID.fromString(groupIdStr);
            UUID userId = UUID.fromString(userIdStr);

            Membership membership = membershipService.leaveGroup(groupId, userId);
            return ResponseEntity.ok(membership);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Kick member
     */
    @PostMapping("/kick")
    public ResponseEntity<Object> kickMember(@RequestBody Map<String, String> request) {
        try {
            String groupIdStr = request.get("groupId");
            String userIdStr = request.get("userId");

            if (groupIdStr == null || userIdStr == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "groupId and userId are required"
                ));
            }

            UUID groupId = UUID.fromString(groupIdStr);
            UUID userId = UUID.fromString(userIdStr);

            Membership membership = membershipService.kickMember(groupId, userId);
            return ResponseEntity.ok(membership);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Delete membership
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMembership(@PathVariable UUID id) {
        boolean deleted = membershipService.deleteMembership(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of(
                    "message", "Membership deleted successfully",
                    "id", id.toString()
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get memberships by group ID
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Membership>> getMembershipsByGroupId(@PathVariable UUID groupId) {
        List<Membership> memberships = membershipService.getMembershipsByGroupId(groupId);
        return ResponseEntity.ok(memberships);
    }

    /**
     * Get active memberships by group ID
     */
    @GetMapping("/group/{groupId}/active")
    public ResponseEntity<List<Membership>> getActiveMembershipsByGroupId(@PathVariable UUID groupId) {
        List<Membership> memberships = membershipService.getActiveMembershipsByGroupId(groupId);
        return ResponseEntity.ok(memberships);
    }

    /**
     * Get memberships by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Membership>> getMembershipsByUserId(@PathVariable UUID userId) {
        List<Membership> memberships = membershipService.getMembershipsByUserId(userId);
        return ResponseEntity.ok(memberships);
    }

    /**
     * Get active memberships by user ID
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Membership>> getActiveMembershipsByUserId(@PathVariable UUID userId) {
        List<Membership> memberships = membershipService.getActiveMembershipsByUserId(userId);
        return ResponseEntity.ok(memberships);
    }

    /**
     * Get memberships by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Membership>> getMembershipsByStatus(@PathVariable String status) {
        try {
            MembershipStatus membershipStatus = MembershipStatus.valueOf(status.toUpperCase());
            List<Membership> memberships = membershipService.getMembershipsByStatus(membershipStatus);
            return ResponseEntity.ok(memberships);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get memberships by role
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<Membership>> getMembershipsByRole(@PathVariable String role) {
        try {
            GroupRole groupRole = GroupRole.valueOf(role.toUpperCase());
            List<Membership> memberships = membershipService.getMembershipsByRole(groupRole);
            return ResponseEntity.ok(memberships);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get group owners
     */
    @GetMapping("/group/{groupId}/owners")
    public ResponseEntity<List<Membership>> getGroupOwners(@PathVariable UUID groupId) {
        List<Membership> owners = membershipService.getGroupOwners(groupId);
        return ResponseEntity.ok(owners);
    }

    /**
     * Check if user is member of group
     */
    @GetMapping("/check/group/{groupId}/user/{userId}")
    public ResponseEntity<Map<String, Boolean>> checkMembership(
            @PathVariable UUID groupId,
            @PathVariable UUID userId) {
        boolean isMember = membershipService.isUserMemberOfGroup(groupId, userId);
        boolean isActiveMember = membershipService.isUserActiveMemberOfGroup(groupId, userId);
        
        return ResponseEntity.ok(Map.of(
                "isMember", isMember,
                "isActiveMember", isActiveMember
        ));
    }

    /**
     * Get membership statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMembershipStats() {
        long totalMemberships = membershipService.getTotalMemberships();
        List<Membership> recentMemberships = membershipService.getRecentMemberships();
        List<Membership> membershipsWithShares = membershipService.getMembershipsWithShares();
        
        return ResponseEntity.ok(Map.of(
                "totalMemberships", totalMemberships,
                "recentMembershipsCount", recentMemberships.size(),
                "membershipsWithSharesCount", membershipsWithShares.size()
        ));
    }

    /**
     * Get group membership count
     */
    @GetMapping("/group/{groupId}/count")
    public ResponseEntity<Map<String, Long>> getGroupMembershipCount(@PathVariable UUID groupId) {
        long count = membershipService.getActiveMembershipCount(groupId);
        return ResponseEntity.ok(Map.of("activeMemberCount", count));
    }

    /**
     * Get user membership count
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> getUserMembershipCount(@PathVariable UUID userId) {
        long count = membershipService.getUserActiveMembershipCount(userId);
        return ResponseEntity.ok(Map.of("activeMembershipCount", count));
    }

    /**
     * Get memberships with shares
     */
    @GetMapping("/with-shares")
    public ResponseEntity<List<Membership>> getMembershipsWithShares() {
        List<Membership> memberships = membershipService.getMembershipsWithShares();
        return ResponseEntity.ok(memberships);
    }

    /**
     * Get recent memberships
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Membership>> getRecentMemberships() {
        List<Membership> memberships = membershipService.getRecentMemberships();
        return ResponseEntity.ok(memberships);
    }
}
