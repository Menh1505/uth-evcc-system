package com.evcc.notification.entity;

import com.evcc.notification.enums.NotificationType;
// import com.evcc.user.entity.User; // Tạm thời chưa cần
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID; // Dùng UUID giống như User.java

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Chúng ta sẽ lưu ID của user (kiểu UUID) thay vì liên kết @ManyToOne
    // để giữ module này độc lập.
    @Column(name = "recipient_user_id", nullable = false)
    private UUID recipientUserId; 

    @Column(nullable = false)
    private String message; // Nội dung thông báo

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false; // Mặc định là chưa đọc

    @Enumerated(EnumType.STRING)
    private NotificationType type; // Loại thông báo

    // ID của đối tượng liên quan (ví dụ: ID của contract, ID của booking)
    private String relatedEntityId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}