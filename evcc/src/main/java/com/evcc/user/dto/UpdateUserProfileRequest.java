package com.evcc.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho request cập nhật thông tin user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequest {

    @Size(max = 20, message = "Số căn cước công dân không được vượt quá 20 ký tự")
    @Pattern(regexp = "^[0-9]*$", message = "Số căn cước công dân chỉ được chứa số")
    private String citizenId;

    @Size(max = 20, message = "Số bằng lái xe không được vượt quá 20 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "Số bằng lái xe chỉ được chứa chữ và số")
    private String driverLicense;
}