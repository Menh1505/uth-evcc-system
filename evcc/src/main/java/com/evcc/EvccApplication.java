package com.evcc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
public class EvccApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvccApplication.class, args);
        System.out.println("test");
    }

}
