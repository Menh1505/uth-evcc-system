package com.evcc.document.dto;
import java.util.UUID;
import lombok.Data;

@Data
public class ParticipantInviteRequest {
    private String name; 
    private UUID  userId;
    // ... các thông tin khác
}