package evcc.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import evcc.dto.response.GroupResponseDto;
import evcc.dto.response.GroupMemberDto;
import evcc.exception.ApiException;

/**
 * Service để quản lý group local (demo) thay thế API backend
 */
@Service
public class GroupLocalService {

    private final AtomicLong groupIdGenerator = new AtomicLong(1);
    private final Map<Long, LocalGroup> groups = new HashMap<>();
    private final Map<Long, List<LocalGroupMember>> groupMembers = new HashMap<>();

    public static class LocalGroup {

        private Long id;
        private String name;
        private String description;
        private UUID createdBy;
        private String createdByUsername;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int memberCount;

        public LocalGroup() {
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public UUID getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(UUID createdBy) {
            this.createdBy = createdBy;
        }

        public String getCreatedByUsername() {
            return createdByUsername;
        }

        public void setCreatedByUsername(String createdByUsername) {
            this.createdByUsername = createdByUsername;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        public int getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(int memberCount) {
            this.memberCount = memberCount;
        }
    }

    public static class LocalGroupMember {

        private Long id;
        private Long groupId;
        private UUID userId;
        private String username;
        private String role; // OWNER, MEMBER
        private LocalDateTime joinedAt;

        public LocalGroupMember() {
        }

        public LocalGroupMember(Long id, Long groupId, UUID userId, String username, String role) {
            this.id = id;
            this.groupId = groupId;
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.joinedAt = LocalDateTime.now();
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getGroupId() {
            return groupId;
        }

        public void setGroupId(Long groupId) {
            this.groupId = groupId;
        }

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public LocalDateTime getJoinedAt() {
            return joinedAt;
        }

        public void setJoinedAt(LocalDateTime joinedAt) {
            this.joinedAt = joinedAt;
        }
    }

    public GroupLocalService() {
        initializeDemoData();
    }

    private void initializeDemoData() {
        // Tạo demo groups
        LocalGroup group1 = new LocalGroup();
        group1.setId(1L);
        group1.setName("Nhóm Mua Xe Điện Hà Nội");
        group1.setDescription("Nhóm mua xe điện tập thể tại Hà Nội");
        group1.setCreatedBy(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        group1.setCreatedByUsername("user1@example.com");
        group1.setCreatedAt(LocalDateTime.now().minusDays(30));
        group1.setUpdatedAt(LocalDateTime.now().minusDays(30));
        group1.setMemberCount(4);
        groups.put(1L, group1);

        LocalGroup group2 = new LocalGroup();
        group2.setId(2L);
        group2.setName("Câu lạc bộ EV Sài Gòn");
        group2.setDescription("Cộng đồng xe điện tại TP.HCM");
        group2.setCreatedBy(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        group2.setCreatedByUsername("user2@example.com");
        group2.setCreatedAt(LocalDateTime.now().minusDays(20));
        group2.setUpdatedAt(LocalDateTime.now().minusDays(20));
        group2.setMemberCount(3);
        groups.put(2L, group2);

        // Tạo demo group members
        List<LocalGroupMember> group1Members = new ArrayList<>();
        group1Members.add(new LocalGroupMember(1L, 1L, UUID.fromString("11111111-1111-1111-1111-111111111111"), "user1@example.com", "OWNER"));
        group1Members.add(new LocalGroupMember(2L, 1L, UUID.fromString("22222222-2222-2222-2222-222222222222"), "user2@example.com", "MEMBER"));
        group1Members.add(new LocalGroupMember(3L, 1L, UUID.fromString("33333333-3333-3333-3333-333333333333"), "user3@example.com", "MEMBER"));
        group1Members.add(new LocalGroupMember(4L, 1L, UUID.fromString("44444444-4444-4444-4444-444444444444"), "user4@example.com", "MEMBER"));
        groupMembers.put(1L, group1Members);

        List<LocalGroupMember> group2Members = new ArrayList<>();
        group2Members.add(new LocalGroupMember(5L, 2L, UUID.fromString("22222222-2222-2222-2222-222222222222"), "user2@example.com", "OWNER"));
        group2Members.add(new LocalGroupMember(6L, 2L, UUID.fromString("33333333-3333-3333-3333-333333333333"), "user3@example.com", "MEMBER"));
        group2Members.add(new LocalGroupMember(7L, 2L, UUID.fromString("44444444-4444-4444-4444-444444444444"), "user4@example.com", "MEMBER"));
        groupMembers.put(2L, group2Members);
    }

    public List<GroupResponseDto> getMyGroups(UUID userId) {
        return groupMembers.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(member -> member.getUserId().equals(userId)))
                .map(entry -> {
                    LocalGroup group = groups.get(entry.getKey());
                    GroupResponseDto dto = new GroupResponseDto();
                    dto.setId(group.getId());
                    dto.setName(group.getName());
                    dto.setDescription(group.getDescription());
                    dto.setMemberCount(group.getMemberCount());
                    dto.setCreatedAt(group.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public GroupResponseDto getGroupDetail(Long groupId) throws ApiException {
        LocalGroup group = groups.get(groupId);
        if (group == null) {
            throw new ApiException(404, "Không tìm thấy nhóm");
        }

        List<LocalGroupMember> members = groupMembers.get(groupId);
        GroupResponseDto dto = new GroupResponseDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setMemberCount(members != null ? members.size() : 0);
        dto.setCreatedAt(group.getCreatedAt());

        // Set members
        if (members != null) {
            List<GroupMemberDto> memberDtos = members.stream()
                    .map(member -> {
                        GroupMemberDto memberDto = new GroupMemberDto();
                        memberDto.setMembershipId(member.getId());
                        memberDto.setUserId(member.getUserId());
                        memberDto.setUsername(member.getUsername());
                        memberDto.setRole(member.getRole());
                        memberDto.setJoinedAt(member.getJoinedAt());
                        return memberDto;
                    })
                    .collect(Collectors.toList());
            dto.setMembers(memberDtos);
        }

        return dto;
    }

    public List<LocalGroupMember> getGroupMembers(Long groupId) {
        return groupMembers.getOrDefault(groupId, new ArrayList<>());
    }

    public GroupResponseDto createGroup(String name, String description, UUID createdBy, String createdByUsername) {
        Long groupId = groupIdGenerator.getAndIncrement();
        LocalGroup group = new LocalGroup();
        group.setId(groupId);
        group.setName(name);
        group.setDescription(description);
        group.setCreatedBy(createdBy);
        group.setCreatedByUsername(createdByUsername);
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());
        group.setMemberCount(1);

        groups.put(groupId, group);

        // Add creator as owner
        List<LocalGroupMember> members = new ArrayList<>();
        members.add(new LocalGroupMember(null, groupId, createdBy, createdByUsername, "OWNER"));
        groupMembers.put(groupId, members);

        GroupResponseDto dto = new GroupResponseDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setMemberCount(group.getMemberCount());
        dto.setCreatedAt(group.getCreatedAt());

        return dto;
    }

    public void addMemberToGroup(Long groupId, UUID userId, String username) throws ApiException {
        LocalGroup group = groups.get(groupId);
        if (group == null) {
            throw new ApiException(404, "Không tìm thấy nhóm");
        }

        List<LocalGroupMember> members = groupMembers.get(groupId);
        if (members == null) {
            members = new ArrayList<>();
            groupMembers.put(groupId, members);
        }

        // Check if user is already member
        boolean isMember = members.stream().anyMatch(member -> member.getUserId().equals(userId));
        if (isMember) {
            throw new ApiException(409, "User đã là thành viên của nhóm");
        }

        // Add new member
        Long membershipId = (long) (members.size() + 1);
        LocalGroupMember newMember = new LocalGroupMember(membershipId, groupId, userId, username, "MEMBER");
        members.add(newMember);

        // Update group member count
        group.setMemberCount(members.size());
        group.setUpdatedAt(LocalDateTime.now());
    }

    public void removeMemberFromGroup(Long groupId, Long membershipId) throws ApiException {
        LocalGroup group = groups.get(groupId);
        if (group == null) {
            throw new ApiException(404, "Không tìm thấy nhóm");
        }

        List<LocalGroupMember> members = groupMembers.get(groupId);
        if (members == null) {
            throw new ApiException(404, "Không tìm thấy thành viên");
        }

        boolean removed = members.removeIf(member -> member.getId().equals(membershipId) && !"OWNER".equals(member.getRole()));
        if (!removed) {
            throw new ApiException(404, "Không thể xóa thành viên (không tìm thấy hoặc là OWNER)");
        }

        // Update group member count
        group.setMemberCount(members.size());
        group.setUpdatedAt(LocalDateTime.now());
    }
}
