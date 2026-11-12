package com.rica.api.consultas_service.consultas;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consultas")
public class EstadisticasController {

    private final ContadorAltas contador;

    public EstadisticasController(ContadorAltas contador) {
        this.contador = contador;
    }

    @GetMapping("/estadisticas")
    public Map<String, Long> estadisticas() {
        return Map.of("investigadoresRegistrados", contador.total());
    }

}
