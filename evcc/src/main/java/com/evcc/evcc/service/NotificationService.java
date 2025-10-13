package com.evcc.evcc.service;

import com.evcc.evcc.entity.Notification;
import com.evcc.evcc.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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