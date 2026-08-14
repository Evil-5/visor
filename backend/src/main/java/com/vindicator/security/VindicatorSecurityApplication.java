package com.vindicator.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VindicatorSecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(VindicatorSecurityApplication.class, args);
    }
}
