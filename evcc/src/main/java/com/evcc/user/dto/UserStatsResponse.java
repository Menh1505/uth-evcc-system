package com.evcc.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO response cho thống kê user (chỉ admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatsResponse {

    private long totalUsers;
    private long verifiedUsers;
    private long unverifiedUsers;
    private long usersWithCompleteInfo; // Có cả CCCD và bằng lái
    private long usersWithIncompleteInfo; // Thiếu CCCD hoặc bằng lái
}