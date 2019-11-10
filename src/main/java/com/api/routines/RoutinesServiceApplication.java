package com.api.routines;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableBatchProcessing
@EnableFeignClients
@ComponentScan({ "com.api.routines", "com.vinicius.entity.commons", "com.vinicius.util.commons",
		"com.vinicius.request.response.commons.routines", "com.api.routines.readers" })
public class RoutinesServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RoutinesServiceApplication.class, args);
		
	}
}
