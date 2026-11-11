package com.rica.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * [L6 · Paso 4 · Service Registry] Registro de servicios (Eureka): cada microservicio se anota aqui con su
 * nombre y su direccion; quien quiera hablar con uno pregunta por el nombre y no por un puerto fijo.
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}

}
