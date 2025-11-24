package evcc.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO response cho vote record (frontend)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRecordResponseDto {

    private Long id;
    private UUID userId;
    private String username;
    private LocalDateTime votedAt;
    private String comment;
}
