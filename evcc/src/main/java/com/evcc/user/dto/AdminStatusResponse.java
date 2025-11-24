package com.evcc.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho response admin status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatusResponse {

    private boolean exists;
    private String username;
    private Boolean verified;
    private String createdAt;
    private Integer roleCount;
    private String message;

    public static AdminStatusResponse success(String username, Boolean verified, String createdAt, Integer roleCount) {
        return AdminStatusResponse.builder()
                .exists(true)
                .username(username)
                .verified(verified)
                .createdAt(createdAt)
                .roleCount(roleCount)
                .build();
    }

    public static AdminStatusResponse notFound() {
        return AdminStatusResponse.builder()
                .exists(false)
                .message("Admin user không tồn tại")
                .build();
    }
}
