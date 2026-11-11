package com.rica.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * [L6 · Paso 5 · API Gateway] Unica puerta de entrada (puerto 8000). Enruta /api/investigadores/** y
 * /api/publicaciones/** a los servicios por su nombre logico, consultando a Eureka (lb://).
 */
@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
