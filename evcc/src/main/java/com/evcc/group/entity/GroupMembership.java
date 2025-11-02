package com.evcc.group.entity;

import java.time.LocalDateTime;

import com.evcc.group.enums.GroupRole;
import com.evcc.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity trung gian đại diện cho thành viên trong nhóm
 * Đây là mấu chốt của thiết kế - liên kết User với Group và định nghĩa vai trò
 */
@Entity
@Table(name = "group_memberships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Quan hệ ManyToOne với User
     * Một User có thể tham gia nhiều nhóm (One User -> Many GroupMemberships)
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Quan hệ ManyToOne với Group  
     * Một Group có thể có nhiều thành viên (One Group -> Many GroupMemberships)
     */
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    /**
     * Vai trò trong nhóm: ADMIN hoặc MEMBER
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole role;

    /**
     * Thời điểm tham gia nhóm - tự động gán khi tạo
     */
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }
}