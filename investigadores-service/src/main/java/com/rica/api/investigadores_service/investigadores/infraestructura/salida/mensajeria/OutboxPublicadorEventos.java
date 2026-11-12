package com.rica.api.investigadores_service.investigadores.infraestructura.salida.mensajeria;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.rica.api.investigadores_service.investigadores.aplicacion.PublicadorEventos;

import tools.jackson.databind.ObjectMapper;

/**
 * [L6 · Paso 6B · adaptador de salida] En lugar de hablar con Kafka, guarda el evento en el outbox (dentro de
 * la transaccion que ya este abierta). Jackson 3: el paquete es tools.jackson.databind.
 */
@Component
public class OutboxPublicadorEventos implements PublicadorEventos {

    private final OutboxEventRepository repositorio;
    private final ObjectMapper objectMapper;

    public OutboxPublicadorEventos(OutboxEventRepository repositorio, ObjectMapper objectMapper) {
        this.repositorio = repositorio;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publicar(String topico, String clave, Map<String, Object> contenido) {
        // [L10 · Paso 5] El id de correlación viaja dentro del evento: así la traza cruza servicios y Kafka
        Map<String, Object> conTraza = new HashMap<>(contenido);
        String correlationId = MDC.get("correlationId");
        if (correlationId != null) {
            conTraza.put("correlationId", correlationId);
        }
        String payload = objectMapper.writeValueAsString(conTraza);
        repositorio.save(new OutboxEvent(topico, clave, payload));
    }

}
