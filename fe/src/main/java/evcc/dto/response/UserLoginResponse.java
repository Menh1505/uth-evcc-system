package evcc.dto.response;

import java.util.List;

public class UserLoginResponse {
    
    private boolean success;
    private String message;
    private String userId;
    private String username;
    private List<String> roles;
    private String token;
    
    public UserLoginResponse() {}
    
    public UserLoginResponse(boolean success, String message, String userId, String username, List<String> roles) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }
    
    public UserLoginResponse(boolean success, String message, String userId, String username, List<String> roles, String token) {
        this(success, message, userId, username, roles);
        this.token = token;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    @Override
    public String toString() {
        return "UserLoginResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                ", token='" + token + '\'' +
                '}';
    }
}
