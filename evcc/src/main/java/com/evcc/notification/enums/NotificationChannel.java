package com.evcc.notification.enums;

/**
 * Enum đại diện cho kênh gửi thông báo
 */
public enum NotificationChannel {
    IN_APP,         // Thông báo trong app
    EMAIL,          // Email
    SMS,            // Tin nhắn SMS
    PUSH,           // Push notification
    WEBHOOK,        // Webhook callback
    SLACK,          // Slack message
    TELEGRAM,       // Telegram bot
    ALL             // Tất cả các kênh
}