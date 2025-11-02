package com.evcc.common.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.evcc.config.DatabaseConfig;
import com.evcc.user.entity.User;
import com.evcc.user.repository.UserRepository;




@Service
public class DatabaseService {

    private final DatabaseConfig databaseConfig;
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    public DatabaseService(DatabaseConfig databaseConfig, JdbcTemplate jdbcTemplate, UserRepository userRepository) {
        this.databaseConfig = databaseConfig;
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

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
        User user = new User("admin", "0123456789");
        return userRepository.save(user);
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Find user by username
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Create a new user
     */
    public User createUser(String username, String phone, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }

        User user = new User(username, password);
        return userRepository.save(user);
    }

    /**
     * Update user
     */
    public User updateUser(UUID id, String username, String phone, String password) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id: " + id);
        }

        User user = userOpt.get();

        // Check if username is taken by another user
        if (!user.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
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
