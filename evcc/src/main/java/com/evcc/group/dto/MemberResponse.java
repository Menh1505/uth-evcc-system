package com.evcc.group.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.evcc.group.enums.GroupRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO response cho thông tin thành viên trong nhóm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {
    
    private Long membershipId;
    private UUID userId;
    private String username;
    private GroupRole role;
    private LocalDateTime joinedAt;
}