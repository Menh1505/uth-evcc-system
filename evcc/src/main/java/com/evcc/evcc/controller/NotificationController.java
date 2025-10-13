package com.evcc.evcc.controller;

import com.evcc.evcc.entity.Notification;
import com.evcc.evcc.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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