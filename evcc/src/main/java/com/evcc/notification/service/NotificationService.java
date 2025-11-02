package com.evcc.notification.service;

import com.evcc.notification.entity.Notification;
import com.evcc.notification.enums.NotificationType;
import com.evcc.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Đây là hàm chính mà các service khác sẽ gọi.
     * Ví dụ: ContractService.acceptContract(...) sẽ gọi hàm này.
     *
     * @param userId ID của người nhận (kiểu UUID)
     * @param message Nội dung thông báo
     * @param type Loại thông báo (CONTRACT, BOOKING, ...)
     * @param relatedEntityId ID của đối tượng liên quan (ví dụ: contract.getId().toString())
     */
    public void createNotification(UUID userId, String message, NotificationType type, String relatedEntityId) {
        Notification notification = Notification.builder()
                .recipientUserId(userId)
                .message(message)
                .type(type)
                .relatedEntityId(relatedEntityId)
                .isRead(false) // Mặc định là chưa đọc
                .build();
        
        notificationRepository.save(notification);
        
        // TODO: (Sau này) Tích hợp WebSocket hoặc Push Notification
        // để gửi thông báo real-time đến client.
    }

    /**
     * Lấy tất cả thông báo cho một User (dùng cho API)
     */
    public List<Notification> getNotificationsForUser(UUID userId) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Đánh dấu một thông báo là đã đọc
     */
    public Notification markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));
        
        // Đảm bảo user này sở hữu thông báo
        if (!notification.getRecipientUserId().equals(userId)) {
            throw new SecurityException("Không có quyền xem thông báo này");
        }
        
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}