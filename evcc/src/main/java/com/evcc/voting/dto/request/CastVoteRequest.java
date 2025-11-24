package com.evcc.voting.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO request để cast vote
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CastVoteRequest {

    @NotNull(message = "ID vote không được để trống")
    private Long voteId;

    @NotNull(message = "ID lựa chọn không được để trống")
    private Long optionId;

    @Size(max = 500, message = "Bình luận không được vượt quá 500 ký tự")
    private String comment;
}
