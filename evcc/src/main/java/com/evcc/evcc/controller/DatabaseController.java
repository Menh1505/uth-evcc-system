package com.evcc.evcc.controller;

import com.evcc.evcc.entity.User;
import com.evcc.evcc.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    /**
     * Test database connection
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        boolean isConnected = databaseService.testConnection();
        String info = databaseService.getConnectionInfo();

        return ResponseEntity.ok(Map.of(
                "connected", isConnected,
                "info", info,
                "timestamp", System.currentTimeMillis()));
    }

    /**
     * Get database info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getDatabaseInfo() {
        return ResponseEntity.ok(Map.of(
                "connection_info", databaseService.getConnectionInfo(),
                "total_users", databaseService.getTotalUsers(),
                "test_query_result", databaseService.executeTestQuery()));
    }

    /**
     * Create sample user
     */
    @PostMapping("/create-sample-user")
    public ResponseEntity<User> createSampleUser() {
        try {
            User user = databaseService.createSampleUser();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = databaseService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by username
     */
    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = databaseService.findUserByUsername(username);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new user
     */
    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody Map<String, String> userRequest) {
        try {
            String username = userRequest.get("username");
            String email = userRequest.get("email");
            String fullName = userRequest.get("fullName");

            if (username == null || email == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Username and email are required"));
            }

            User user = databaseService.createUser(username, email, fullName);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()));
        }
    }

    /**
     * Update user
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<Object> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> userRequest) {
        try {
            String username = userRequest.get("username");
            String email = userRequest.get("email");
            String fullName = userRequest.get("fullName");

            User user = databaseService.updateUser(id, username, email, fullName);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()));
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        boolean deleted = databaseService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of(
                    "message", "User deleted successfully",
                    "id", id));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get user count
     */
    @GetMapping("/users/count")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        long count = databaseService.getTotalUsers();
        return ResponseEntity.ok(Map.of("total_users", count));
    }
}
