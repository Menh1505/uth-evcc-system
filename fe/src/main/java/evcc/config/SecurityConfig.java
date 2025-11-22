package evcc.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration for security-related settings
 */
@Configuration
public class SecurityConfig {
    
    /**
     * Security configuration for the application
     */
    public static final String JWT_SECRET = "your-secret-key-change-in-production";
    public static final long JWT_EXPIRATION = 86400000; // 24 hours
    
}
