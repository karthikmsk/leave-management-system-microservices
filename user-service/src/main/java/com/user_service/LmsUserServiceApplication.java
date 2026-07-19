package com.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LmsUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmsUserServiceApplication.class, args);
	}

}
