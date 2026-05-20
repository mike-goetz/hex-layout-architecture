package com.example.travel.core.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.example.travel.core")
public class TravelCoreService {

    static void main(String[] args) {
        SpringApplication.run(TravelCoreService.class, args);
    }

}