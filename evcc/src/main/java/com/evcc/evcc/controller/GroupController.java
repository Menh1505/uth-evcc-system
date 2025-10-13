package com.evcc.evcc.controller;

import com.evcc.evcc.entity.Group;
import com.evcc.evcc.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    /**
     * Get all groups
     */
    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        List<Group> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    /**
     * Get group by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroupById(@PathVariable UUID id) {
        Optional<Group> group = groupService.findGroupById(id);
        return group.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get group by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Group> getGroupByName(@PathVariable String name) {
        Optional<Group> group = groupService.findGroupByName(name);
        return group.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new group
     */
    @PostMapping
    public ResponseEntity<Object> createGroup(@RequestBody Map<String, String> groupRequest) {
        try {
            String name = groupRequest.get("name");
            String description = groupRequest.get("description");
            String contractIdStr = groupRequest.get("contractId");

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Group name is required"
                ));
            }

            UUID contractId = null;
            if (contractIdStr != null && !contractIdStr.trim().isEmpty()) {
                try {
                    contractId = UUID.fromString(contractIdStr);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Invalid contractId format"
                    ));
                }
            }

            Group group = groupService.createGroup(name.trim(), description, contractId);
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Create new group with owner
     */
    @PostMapping("/with-owner")
    public ResponseEntity<Object> createGroupWithOwner(@RequestBody Map<String, String> groupRequest) {
        try {
            String name = groupRequest.get("name");
            String description = groupRequest.get("description");
            String contractIdStr = groupRequest.get("contractId");
            String ownerIdStr = groupRequest.get("ownerId");

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Group name is required"
                ));
            }

            if (ownerIdStr == null || ownerIdStr.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Owner ID is required"
                ));
            }

            UUID contractId = null;
            if (contractIdStr != null && !contractIdStr.trim().isEmpty()) {
                try {
                    contractId = UUID.fromString(contractIdStr);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Invalid contractId format"
                    ));
                }
            }

            UUID ownerId;
            try {
                ownerId = UUID.fromString(ownerIdStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Invalid ownerId format"
                ));
            }

            Group group = groupService.createGroupWithOwner(name.trim(), description, contractId, ownerId);
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Update group
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateGroup(
            @PathVariable UUID id,
            @RequestBody Map<String, String> groupRequest) {
        try {
            String name = groupRequest.get("name");
            String description = groupRequest.get("description");
            String contractIdStr = groupRequest.get("contractId");

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Group name is required"
                ));
            }

            UUID contractId = null;
            if (contractIdStr != null && !contractIdStr.trim().isEmpty()) {
                try {
                    contractId = UUID.fromString(contractIdStr);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Invalid contractId format"
                    ));
                }
            }

            Group group = groupService.updateGroup(id, name.trim(), description, contractId);
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Delete group
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteGroup(@PathVariable UUID id) {
        boolean deleted = groupService.deleteGroup(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of(
                    "message", "Group deleted successfully",
                    "id", id.toString()
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search groups by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<Group>> searchGroups(@RequestParam String name) {
        List<Group> groups = groupService.searchGroupsByName(name);
        return ResponseEntity.ok(groups);
    }

    /**
     * Get groups by contract ID
     */
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<List<Group>> getGroupsByContractId(@PathVariable UUID contractId) {
        List<Group> groups = groupService.findGroupsByContractId(contractId);
        return ResponseEntity.ok(groups);
    }

    /**
     * Get groups without contract
     */
    @GetMapping("/without-contract")
    public ResponseEntity<List<Group>> getGroupsWithoutContract() {
        List<Group> groups = groupService.getGroupsWithoutContract();
        return ResponseEntity.ok(groups);
    }

    /**
     * Get groups with active members
     */
    @GetMapping("/with-active-members")
    public ResponseEntity<List<Group>> getGroupsWithActiveMembers() {
        List<Group> groups = groupService.getGroupsWithActiveMembers();
        return ResponseEntity.ok(groups);
    }

    /**
     * Get group statistics
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getGroupStats(@PathVariable UUID id) {
        if (!groupService.groupExists(id)) {
            return ResponseEntity.notFound().build();
        }

        long memberCount = groupService.getGroupMemberCount(id);
        
        return ResponseEntity.ok(Map.of(
                "groupId", id.toString(),
                "memberCount", memberCount
        ));
    }

    /**
     * Get total group count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getGroupCount() {
        long count = groupService.getTotalGroups();
        return ResponseEntity.ok(Map.of("total_groups", count));
    }

    /**
     * Check if group name exists
     */
    @GetMapping("/check-name/{name}")
    public ResponseEntity<Map<String, Boolean>> checkGroupName(@PathVariable String name) {
        boolean exists = groupService.groupNameExists(name);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
