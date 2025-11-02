package com.evcc.config;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evcc.common.service.DatabaseService;
import com.evcc.user.entity.User;




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
    public ResponseEntity<User> getUserBy(@PathVariable String username) {
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
            String phone = userRequest.get("phone");
            String password = userRequest.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Username and password are required"));
            }

            User user = databaseService.createUser(username, phone, password);
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
            @PathVariable UUID id,
            @RequestBody Map<String, String> userRequest) {
        try {
            String username = userRequest.get("username");
            String phone = userRequest.get("phone");
            String password = userRequest.get("password");

            User user = databaseService.updateUser(id, username, phone, password);
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
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable UUID id) {
        boolean deleted = databaseService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of(
                    "message", "User deleted successfully",
                    "id", id.toString()));
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
