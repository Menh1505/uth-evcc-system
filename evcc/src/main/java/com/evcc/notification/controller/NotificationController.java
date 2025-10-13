package com.evcc.notification.controller;

import com.evcc.notification.entity.Notification;
import com.evcc.notification.service.NotificationService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @PostMapping
    public Notification saveNotification(@RequestBody Notification notification) {
        return notificationService.saveNotification(notification);
    }

    @GetMapping("/{id}")
    public Notification getNotificationById(@PathVariable UUID id) {
        return notificationService.getNotificationById(id);
    }
}
