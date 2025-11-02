package com.evcc.common.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;



@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final DatabaseService databaseService;

    public DatabaseInitializer(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Database Connection Test ===");

        // Test connection
        boolean isConnected = databaseService.testConnection();
        if (isConnected) {
            System.out.println("✅ Database connection successful!");
            System.out.println("📊 " + databaseService.getConnectionInfo());
            System.out.println("📈 Total users in database: " + databaseService.getTotalUsers());
            System.out.println("🔍 " + databaseService.executeTestQuery());
        } else {
            System.out.println("❌ Database connection failed!");
            System.out.println("⚠️  Please check your PostgreSQL configuration:");
            System.out.println("   - Make sure PostgreSQL is running on localhost:5432");
            System.out.println("   - Create database 'evcc_db' if it doesn't exist");
            System.out.println("   - Check username/password in application.properties");
        }

        System.out.println("=== Database API Endpoints ===");
        System.out.println("🌐 Test connection: GET http://localhost:8080/api/database/test-connection");
        System.out.println("📊 Database info: GET http://localhost:8080/api/database/info");
        System.out.println("👥 All users: GET http://localhost:8080/api/database/users");
        System.out.println("➕ Create user: POST http://localhost:8080/api/database/users");
        System.out.println("================================");
    }
}
