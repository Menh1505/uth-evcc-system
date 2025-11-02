package com.evcc.database.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Database initializer component
 * Runs database checks and setup when application starts
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final DatabaseService databaseService;

    public DatabaseInitializer(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== Database Module Initialization ===");

        // Test connection
        boolean isConnected = databaseService.testConnection();
        if (isConnected) {
            logger.info("✅ Database connection successful!");
            logger.info("📊 {}", databaseService.getConnectionInfo());
            logger.info("📈 Database stats: {}", databaseService.getDatabaseStats());
            logger.info("🔍 {}", databaseService.executeTestQuery());
        } else {
            logger.error("❌ Database connection failed!");
            logger.warn("⚠️  Please check your PostgreSQL configuration:");
            logger.warn("   - Make sure PostgreSQL is running on localhost:5432");
            logger.warn("   - Create database 'evcc_db' if it doesn't exist");
            logger.warn("   - Check username/password in application.properties");
        }

        logger.info("=== Database API Endpoints ===");
        logger.info("🏥 Health check: GET http://localhost:8080/api/database/health");
        logger.info("🌐 Test connection: GET http://localhost:8080/api/database/test-connection");
        logger.info("📊 Database info: GET http://localhost:8080/api/database/info");
        logger.info("📈 Database stats: GET http://localhost:8080/api/database/stats");
        logger.info("👥 All users: GET http://localhost:8080/api/database/users");
        logger.info("➕ Create user: POST http://localhost:8080/api/database/users");
        logger.info("================================");
    }
}