package com.evcc.voting.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.evcc.voting.enums.VoteType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO request để tạo vote mới
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVoteRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;

    @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
    private String description;

    @NotNull(message = "Loại vote không được để trống")
    private VoteType voteType;

    @NotNull(message = "ID nhóm không được để trống")
    private Long groupId;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @Future(message = "Thời gian bắt đầu phải ở tương lai")
    private LocalDateTime startTime;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @Future(message = "Thời gian kết thúc phải ở tương lai")
    private LocalDateTime endTime;

    @Min(value = 1, message = "Số phiếu tối thiểu phải ít nhất là 1")
    @Builder.Default
    private Integer minimumVotes = 1;

    @DecimalMin(value = "0.01", message = "Tỷ lệ phần trăm phải lớn hơn 0")
    @DecimalMax(value = "100.00", message = "Tỷ lệ phần trăm không được vượt quá 100")
    @Builder.Default
    private BigDecimal requiredPercentage = new BigDecimal("50.00");

    @Builder.Default
    private Boolean allowVoteChange = true;

    @Builder.Default
    private Boolean anonymous = false;

    private BigDecimal relatedAmount;
    private Long relatedEntityId;
    private String relatedEntityType;

    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    private String notes;

    @NotEmpty(message = "Phải có ít nhất một lựa chọn")
    @Valid
    private List<VoteOptionRequest> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VoteOptionRequest {

        @NotBlank(message = "Tên lựa chọn không được để trống")
        @Size(max = 255, message = "Tên lựa chọn không được vượt quá 255 ký tự")
        private String optionText;

        @Size(max = 1000, message = "Mô tả lựa chọn không được vượt quá 1000 ký tự")
        private String description;

        @Builder.Default
        private Integer displayOrder = 0;
    }
}
