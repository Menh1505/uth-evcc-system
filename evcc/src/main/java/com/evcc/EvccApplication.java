package com.evcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EvccApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvccApplication.class, args);
        System.out.println("EVCC backend is running!");
    }

}
