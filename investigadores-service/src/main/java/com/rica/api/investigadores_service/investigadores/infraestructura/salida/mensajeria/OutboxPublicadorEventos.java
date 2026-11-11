package com.rica.api.investigadores_service.investigadores.infraestructura.salida.mensajeria;

import com.rica.api.investigadores_service.investigadores.aplicacion.PublicadorEventos;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

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
        String payload = objectMapper.writeValueAsString(contenido);
        repositorio.save(new OutboxEvent(topico, clave, payload));
    }

}
