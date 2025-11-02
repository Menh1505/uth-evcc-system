package com.evcc.document.dto;

import lombok.Data;

@Data
public class ParticipantInviteRequest {
    private String name; 
    private Long userId;
    // ... các thông tin khác
}