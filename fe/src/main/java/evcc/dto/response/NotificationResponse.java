package evcc.dto.response;

import java.time.LocalDateTime;

/**
 * Response object for notifications
 */
public class NotificationResponse {
    private String id;
    private String title;
    private String message;
    private String type;
    private String priority;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    
    public NotificationResponse() {}
    
    public NotificationResponse(String id, String title, String message, String type, String priority) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public boolean isRead() {
        return read;
    }
    
    public void setRead(boolean read) {
        this.read = read;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
