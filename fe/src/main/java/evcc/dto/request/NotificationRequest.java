package evcc.dto.request;

/**
 * Request object for creating a new notification
 */
public class NotificationRequest {
    private String title;
    private String label;
    private String message;
    private String type;
    private String priority;
    
    public NotificationRequest() {}
    
    public NotificationRequest(String title, String message, String type, String priority) {
        this.title = title;
        this.label= label;
        this.message = message;
        this.type = type;
        this.priority = priority;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
        
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
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
    
    @Override
    public String toString() {
        return "NotificationRequest{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }
}
