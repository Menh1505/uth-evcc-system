package com.evcc.evcc.service;

import com.evcc.evcc.config.DatabaseConfig;
import com.evcc.evcc.entity.User;
import com.evcc.evcc.entity.UserStatus;
import com.evcc.evcc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DatabaseService {

    @Autowired
    private DatabaseConfig databaseConfig;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    /**
     * Test database connection
     */
    public boolean testConnection() {
        return databaseConfig.testConnection();
    }

    /**
     * Get database connection info
     */
    public String getConnectionInfo() {
        try {
            String result = jdbcTemplate.queryForObject(
                    "SELECT 'Connected to PostgreSQL version: ' || version()",
                    String.class);
            return result;
        } catch (Exception e) {
            return "Failed to get database info: " + e.getMessage();
        }
    }

    /**
     * Create a sample user
     */
    public User createSampleUser() {
        User user = new User("admin@evcc.com", "0123456789", "hashedPassword123", UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Find user by email
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Create a new user
     */
    public User createUser(String email, String phone, String passwordHash) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }
        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new RuntimeException("Phone already exists: " + phone);
        }

        User user = new User(email, phone, passwordHash);
        return userRepository.save(user);
    }

    /**
     * Update user
     */
    public User updateUser(UUID id, String email, String phone, String passwordHash, UserStatus status) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id: " + id);
        }

        User user = userOpt.get();

        // Check if email is taken by another user
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        // Check if phone is taken by another user
        if (phone != null && !phone.equals(user.getPhone()) && userRepository.existsByPhone(phone)) {
            throw new RuntimeException("Phone already exists: " + phone);
        }

        user.setEmail(email);
        user.setPhone(phone);
        if (passwordHash != null) {
            user.setPasswordHash(passwordHash);
        }
        if (status != null) {
            user.setStatus(status);
        }

        return userRepository.save(user);
    }

    /**
     * Delete user by id
     */
    public boolean deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get total user count
     */
    public long getTotalUsers() {
        return userRepository.count();
    }

    /**
     * Execute raw SQL query (for testing purposes)
     */
    public String executeTestQuery() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public'",
                    Integer.class);
            return "Number of tables in public schema: " + count;
        } catch (Exception e) {
            return "Error executing test query: " + e.getMessage();
        }
    }
}
