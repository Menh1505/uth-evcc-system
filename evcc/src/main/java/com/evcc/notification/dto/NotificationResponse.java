package com.evcc.notification.dto;

import com.evcc.notification.enums.NotificationType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationResponse {
    private UUID id;
    private String message;
    private boolean isRead;
    private NotificationType type;
    private String relatedEntityId;
    private LocalDateTime createdAt;

    // Hàm chuyển đổi (Converter) từ Entity sang DTO
    public static NotificationResponse fromEntity(com.evcc.notification.entity.Notification entity) {
        NotificationResponse dto = new NotificationResponse();
        dto.setId(entity.getId());
        dto.setMessage(entity.getMessage());
        dto.setRead(entity.isRead());
        dto.setType(entity.getType());
        dto.setRelatedEntityId(entity.getRelatedEntityId());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}