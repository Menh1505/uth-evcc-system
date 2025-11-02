package com.evcc.document.dto;

import lombok.Data;

@Data
public class ParticipantInviteRequest {
    private String inviteeName;
    private String inviteePhone;
    // ... các thông tin khác
}