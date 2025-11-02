package com.evcc.notification.controller;

import com.evcc.notification.dto.NotificationResponse;
import com.evcc.notification.service.NotificationService;
// import com.evcc.user.entity.User; // Sẽ dùng User thật sau
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    
    // (Tạm thời hardcode User ID để test)
    private UUID getTestUserId() {
        // Đây PHẢI là UUID của User ID=1 mà bạn đã tạo (nếu dùng UUID)
        // Nếu User.java của bạn (bản mới) vẫn là Long ID=1, bạn cần sửa lại hàm này
        //
        // Tạm thời, chúng ta sẽ giả định một UUID cố định
        return UUID.fromString("00000000-0000-0000-0000-000000000001");
    }

    /**
     * API lấy tất cả thông báo của user đang đăng nhập
     */
    @GetMapping("/")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            /* @AuthenticationPrincipal User currentUser */) {
        
        // UUID userId = currentUser.getId(); // Sẽ dùng cái này sau
        UUID userId = getTestUserId(); // Tạm thời dùng test
        
        List<NotificationResponse> responses = notificationService.getNotificationsForUser(userId)
                .stream()
                .map(NotificationResponse::fromEntity) // Chuyển Entity -> DTO
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * API đánh dấu một thông báo là đã đọc
     */
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable UUID notificationId
            /* @AuthenticationPrincipal User currentUser */) {
        
        // UUID userId = currentUser.getId(); // Sẽ dùng cái này sau
        UUID userId = getTestUserId(); // Tạm thời dùng test
        
        NotificationResponse response = NotificationResponse.fromEntity(
                notificationService.markAsRead(notificationId, userId)
        );
        
        return ResponseEntity.ok(response);
    }
}