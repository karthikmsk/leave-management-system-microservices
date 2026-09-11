package com.leave_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class LeaveServiceApplication {
    
    public static void main(String[] args) {
        System.out.println("******** LEAVE SERVICE STARTED ********");
        SpringApplication.run(LeaveServiceApplication.class, args);
    }
}
