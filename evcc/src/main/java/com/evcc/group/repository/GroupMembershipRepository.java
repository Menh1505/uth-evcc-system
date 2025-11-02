package com.evcc.group.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evcc.group.entity.Group;
import com.evcc.group.entity.GroupMembership;
import com.evcc.group.enums.GroupRole;
import com.evcc.user.entity.User;

/**
 * Repository interface cho GroupMembership entity
 */
@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {
    
    /**
     * Tìm GroupMembership theo groupId và userId
     * Đây là phương thức quan trọng được yêu cầu trong đề bài
     */
    Optional<GroupMembership> findByGroup_IdAndUser_Id(Long groupId, UUID userId);
    
    /**
     * Tìm GroupMembership theo Group và User objects
     */
    Optional<GroupMembership> findByGroupAndUser(Group group, User user);
    
    /**
     * Kiểm tra xem user có thuộc group không
     */
    boolean existsByGroup_IdAndUser_Id(Long groupId, UUID userId);
    
    /**
     * Lấy tất cả membership của một user
     */
    List<GroupMembership> findByUser_Id(UUID userId);
    
    /**
     * Lấy tất cả membership của một group
     */
    List<GroupMembership> findByGroup_Id(Long groupId);
    
    /**
     * Lấy thành viên theo role trong một group
     */
    List<GroupMembership> findByGroup_IdAndRole(Long groupId, GroupRole role);
    
    /**
     * Đếm số lượng theo role trong một group
     */
    long countByGroup_IdAndRole(Long groupId, GroupRole role);
}