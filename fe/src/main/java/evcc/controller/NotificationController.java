package evcc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import evcc.dto.response.NotificationResponse;
import evcc.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * Controller for managing notifications
 */
@Controller
@RequestMapping("/notifications")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Get all notifications API
     */
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<String>> getAllNotifications(HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
    
    /**
     * Get unread notifications API
     */
    @GetMapping("/api/unread")
    @ResponseBody
    public ResponseEntity<List<String>> getUnreadNotifications(HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(notificationService.getUnreadNotifications());
    }
    
    /**
     * Mark notification as read
     */
    @PostMapping("/api/{notificationId}/read")
    @ResponseBody
    public ResponseEntity<String> markAsRead(@PathVariable String notificationId, HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        boolean success = notificationService.markAsRead(notificationId);
        return success ? ResponseEntity.ok("OK") : ResponseEntity.badRequest().body("Failed");
    }
    
    /**
     * Delete notification
     */
    @DeleteMapping("/api/{notificationId}")
    @ResponseBody
    public ResponseEntity<String> deleteNotification(@PathVariable String notificationId, HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        boolean success = notificationService.deleteNotification(notificationId);
        return success ? ResponseEntity.ok("OK") : ResponseEntity.badRequest().body("Failed");
    }
}
