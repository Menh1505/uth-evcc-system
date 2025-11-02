package com.evcc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EvccApplication {
    private static final Logger logger = LoggerFactory.getLogger(EvccApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EvccApplication.class, args);
        logger.info("EVCC backend is running!");
    }

}
