package com.smarthealthcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartHealthcareApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartHealthcareApplication.class, args);
    }
}
