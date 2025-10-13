package com.evcc.notification.service;

import com.evcc.notification.entity.Notification;
import com.evcc.notification.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Notification getNotificationById(UUID id) {
        return notificationRepository.findById(id).orElse(null);
    }
}
