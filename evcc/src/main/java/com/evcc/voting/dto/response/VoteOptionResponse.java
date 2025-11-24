package com.evcc.voting.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO response cho vote option
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteOptionResponse {

    private Long id;
    private String optionText;
    private String description;
    private Integer displayOrder;

    private Long voteCount;
    private Double votePercentage;
    private Boolean isWinning;

    private List<VoteRecordResponse> voteRecords; // Chỉ hiển thị nếu không anonymous

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
