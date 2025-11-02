package com.evcc.group.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.group.dto.AddMemberRequest;
import com.evcc.group.dto.CreateGroupRequest;
import com.evcc.group.dto.GroupResponse;
import com.evcc.group.dto.MemberResponse;
import com.evcc.group.entity.Group;
import com.evcc.group.entity.GroupMembership;
import com.evcc.group.enums.GroupRole;
import com.evcc.group.repository.GroupMembershipRepository;
import com.evcc.group.repository.GroupRepository;
import com.evcc.user.entity.User;
import com.evcc.user.repository.UserRepository;

/**
 * Service xử lý logic nghiệp vụ cho quản lý nhóm
 */
@Service
@Transactional
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMembershipRepository membershipRepository;

    public GroupService(GroupRepository groupRepository, 
                       UserRepository userRepository,
                       GroupMembershipRepository membershipRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    /**
     * Tạo nhóm mới và thêm creator làm ADMIN
     */
    public GroupResponse createGroup(CreateGroupRequest request, UUID creatorUserId) {
        // 1. Lấy User từ creatorUserId
        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với ID: " + creatorUserId));

        // 2. Kiểm tra tên nhóm đã tồn tại chưa
        if (groupRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Tên nhóm đã tồn tại: " + request.getName());
        }

        // 3. Tạo và lưu Group mới
        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        
        Group savedGroup = groupRepository.save(group);

        // 4. Tạo GroupMembership cho creator với role ADMIN
        GroupMembership creatorMembership = GroupMembership.builder()
                .user(creator)
                .group(savedGroup)
                .role(GroupRole.ADMIN)
                .build();
        
        membershipRepository.save(creatorMembership);

        // 5. Trả về GroupResponse
        return convertToGroupResponse(savedGroup);
    }

    /**
     * Thêm thành viên vào nhóm (chỉ ADMIN mới được thêm)
     */
    public MemberResponse addMember(Long groupId, AddMemberRequest request, UUID adminUserId) {
        // 1. Kiểm tra quyền ADMIN
        checkAdminPermission(groupId, adminUserId);

        // 2. Tìm group và user cần thêm
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhóm với ID: " + groupId));
        
        User userToAdd = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với ID: " + request.getUserId()));

        // 3. Kiểm tra user đã ở trong nhóm chưa
        if (membershipRepository.existsByGroup_IdAndUser_Id(groupId, request.getUserId())) {
            throw new IllegalArgumentException("User đã là thành viên của nhóm này");
        }

        // 4. Tạo và lưu GroupMembership mới với role MEMBER
        GroupMembership membership = GroupMembership.builder()
                .user(userToAdd)
                .group(group)
                .role(GroupRole.MEMBER)
                .build();
        
        GroupMembership savedMembership = membershipRepository.save(membership);

        // 5. Trả về MemberResponse
        return convertToMemberResponse(savedMembership);
    }

    /**
     * Xóa thành viên khỏi nhóm (chỉ ADMIN mới được xóa)
     */
    public void removeMember(Long groupId, UUID memberToRemoveId, UUID adminUserId) {
        // 1. Kiểm tra quyền ADMIN
        checkAdminPermission(groupId, adminUserId);

        // 2. Tìm GroupMembership của member cần xóa
        GroupMembership membershipToRemove = membershipRepository
                .findByGroup_IdAndUser_Id(groupId, memberToRemoveId)
                .orElseThrow(() -> new IllegalArgumentException("User không phải là thành viên của nhóm này"));

        // 3. Không cho phép ADMIN tự xóa mình (để tránh nhóm không có ADMIN)
        if (membershipToRemove.getRole() == GroupRole.ADMIN) {
            throw new IllegalArgumentException("Không thể xóa ADMIN khỏi nhóm");
        }

        // 4. Xóa GroupMembership
        membershipRepository.delete(membershipToRemove);
    }

    /**
     * Lấy thông tin chi tiết nhóm
     */
    @Transactional(readOnly = true)
    public GroupResponse getGroupById(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhóm với ID: " + groupId));
        
        return convertToGroupResponse(group);
    }

    /**
     * Lấy danh sách nhóm mà user tham gia
     */
    @Transactional(readOnly = true)
    public List<GroupResponse> getUserGroups(UUID userId) {
        List<GroupMembership> memberships = membershipRepository.findByUser_Id(userId);
        
        return memberships.stream()
                .map(membership -> convertToGroupResponse(membership.getGroup()))
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra quyền ADMIN - method private quan trọng
     */
    private void checkAdminPermission(Long groupId, UUID userId) {
        GroupMembership membership = membershipRepository
                .findByGroup_IdAndUser_Id(groupId, userId)
                .orElseThrow(() -> new SecurityException("Bạn không phải là thành viên của nhóm này"));

        if (membership.getRole() != GroupRole.ADMIN) {
            throw new SecurityException("Bạn không có quyền admin trong nhóm này");
        }
    }

    /**
     * Convert Group entity thành GroupResponse DTO
     */
    private GroupResponse convertToGroupResponse(Group group) {
        List<GroupMembership> memberships = membershipRepository.findByGroup_Id(group.getId());
        
        List<MemberResponse> members = memberships.stream()
                .map(this::convertToMemberResponse)
                .collect(Collectors.toList());

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .members(members)
                .memberCount(members.size())
                .build();
    }

    /**
     * Convert GroupMembership entity thành MemberResponse DTO
     */
    private MemberResponse convertToMemberResponse(GroupMembership membership) {
        return MemberResponse.builder()
                .membershipId(membership.getId())
                .userId(membership.getUser().getId())
                .username(membership.getUser().getUsername())
                .role(membership.getRole())
                .joinedAt(membership.getJoinedAt())
                .build();
    }
}