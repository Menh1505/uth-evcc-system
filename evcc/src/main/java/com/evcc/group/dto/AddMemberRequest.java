package com.evcc.group.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho request thêm thành viên vào nhóm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberRequest {
    
    @NotNull(message = "User ID không được để trống")
    private UUID userId;
}