package com.evcc.notification.repository;

import com.evcc.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    // Lấy tất cả thông báo (chưa đọc) của 1 user
    List<Notification> findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID recipientUserId);
    
    // Lấy tất cả thông báo của 1 user
    List<Notification> findByRecipientUserIdOrderByCreatedAtDesc(UUID recipientUserId);
}