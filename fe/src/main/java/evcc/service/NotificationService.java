package evcc.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

/**
 * Service for managing notifications
 */
@Service
public class NotificationService {
    
    /**
     * Get all notifications for current user
     */
    public List<String> getAllNotifications() {
        List<String> notifications = new ArrayList<>();
        notifications.add("Notification 1");
        notifications.add("Notification 2");
        notifications.add("Notification 3");
        return notifications;
    }
    
    /**
     * Get unread notifications
     */
    public List<String> getUnreadNotifications() {
        List<String> unreadNotifications = new ArrayList<>();
        unreadNotifications.add("Unread Notification 1");
        unreadNotifications.add("Unread Notification 2");
        return unreadNotifications;
    }
    
    /**
     * Mark notification as read
     */
    public boolean markAsRead(String notificationId) {
        return true;
    }
    
    /**
     * Delete notification
     */
    public boolean deleteNotification(String notificationId) {
        return true;
    }
}
