package evcc.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO response cho vote (frontend)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponseDto {

    private Long id;
    private String title;
    private String description;
    private String voteType;
    private String voteTypeDisplay;
    private String status;
    private String statusDisplay;

    private Long groupId;
    private String groupName;

    private String createdBy;
    private String createdByUsername;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer minimumVotes;
    private BigDecimal requiredPercentage;
    private Boolean allowVoteChange;
    private Boolean anonymous;

    private BigDecimal relatedAmount;
    private Long relatedEntityId;
    private String relatedEntityType;
    private String notes;

    private List<VoteOptionResponseDto> options;

    // Computed fields
    private Long totalVotes;
    private Boolean isActive;
    private Boolean isExpired;
    private Boolean hasMinimumVotes;
    private Boolean userHasVoted;
    private Long userVotedOptionId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
