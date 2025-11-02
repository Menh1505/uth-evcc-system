package com.evcc.group.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evcc.group.dto.AddMemberRequest;
import com.evcc.group.dto.CreateGroupRequest;
import com.evcc.group.dto.GroupResponse;
import com.evcc.group.dto.MemberResponse;
import com.evcc.group.service.GroupService;
import com.evcc.user.entity.User;
import com.evcc.user.repository.UserRepository;

import jakarta.validation.Valid;

/**
 * REST Controller cho quản lý nhóm
 */
@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;
    private final UserRepository userRepository;

    public GroupController(GroupService groupService, UserRepository userRepository) {
        this.groupService = groupService;
        this.userRepository = userRepository;
    }

    /**
     * Tạo nhóm mới
     * POST /api/groups
     */
    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        UUID creatorUserId = getCurrentUserId();
        GroupResponse response = groupService.createGroup(request, creatorUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Thêm thành viên vào nhóm
     * POST /api/groups/{groupId}/members
     */
    @PostMapping("/{groupId}/members")
    public ResponseEntity<MemberResponse> addMember(
            @PathVariable Long groupId,
            @Valid @RequestBody AddMemberRequest request) {
        
        UUID adminUserId = getCurrentUserId();
        MemberResponse response = groupService.addMember(groupId, request, adminUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Xóa thành viên khỏi nhóm
     * DELETE /api/groups/{groupId}/members/{memberId}
     */
    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable UUID memberId) {
        
        UUID adminUserId = getCurrentUserId();
        groupService.removeMember(groupId, memberId, adminUserId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lấy thông tin chi tiết nhóm
     * GET /api/groups/{groupId}
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable Long groupId) {
        GroupResponse response = groupService.getGroupById(groupId);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách nhóm mà user hiện tại tham gia
     * GET /api/groups/my-groups
     */
    @GetMapping("/my-groups")
    public ResponseEntity<List<GroupResponse>> getMyGroups() {
        UUID userId = getCurrentUserId();
        List<GroupResponse> response = groupService.getUserGroups(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy User ID từ Spring Security Authentication
     * Không tin tưởng vào request body, lấy từ JWT token đã được xác thực
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User chưa đăng nhập");
        }
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SecurityException("Không tìm thấy user: " + username));
        
        return user.getId();
    }
}