package com.rica.api.publicaciones_service.plataforma;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    @GetMapping
    public Map<String, String> status() {
        return Map.of("servicio", "publicaciones-service", "estado", "OK");
    }
}
