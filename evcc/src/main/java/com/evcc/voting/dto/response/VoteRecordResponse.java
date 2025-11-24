package com.evcc.voting.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO response cho vote record
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRecordResponse {

    private Long id;
    private UUID userId;
    private String username;
    private LocalDateTime votedAt;
    private String comment;

    // Không include IP address vì security
}
