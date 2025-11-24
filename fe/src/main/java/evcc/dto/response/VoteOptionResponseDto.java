package evcc.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO response cho vote option (frontend)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteOptionResponseDto {

    private Long id;
    private String optionText;
    private String description;
    private Integer displayOrder;

    private Long voteCount;
    private Double votePercentage;
    private Boolean isWinning;

    private List<VoteRecordResponseDto> voteRecords;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
