package com.rica.api.investigadores_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InvestigadoresServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvestigadoresServiceApplication.class, args);
	}

}
