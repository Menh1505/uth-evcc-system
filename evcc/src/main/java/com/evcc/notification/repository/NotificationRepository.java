package com.evcc.notification.repository;

import com.evcc.notification.entity.Notification;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
