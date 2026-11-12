package com.rica.api.publicaciones_service.publicaciones.infraestructura.salida.mensajeria;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.rica.api.publicaciones_service.publicaciones.aplicacion.PublicadorEventosPublicaciones;
import com.rica.api.publicaciones_service.publicaciones.dominio.Publicacion;

import tools.jackson.databind.ObjectMapper;

/**
 * [L8 · Paso 2 · adaptador de salida] Publica publicaciones.registradas en Kafka.
 * La clave del mensaje es el correo del investigador: todos los eventos de un mismo investigador
 * caen en la misma partición y conservan su orden.
 */
@Component
public class KafkaPublicadorEventosPublicaciones implements PublicadorEventosPublicaciones {

    static final String TOPICO_PUBLICACION_REGISTRADA = "publicaciones.registradas";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaPublicadorEventosPublicaciones(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publicacionRegistrada(Publicacion publicacion) {
        String contenido = objectMapper.writeValueAsString(Map.of(
                "id", publicacion.getId(),
                "investigadorCorreo", publicacion.getInvestigadorCorreo(),
                "titulo", publicacion.getTitulo(),
                "tipo", publicacion.getTipo(),
                "anio", publicacion.getAnio()));
        kafkaTemplate.send(TOPICO_PUBLICACION_REGISTRADA, publicacion.getInvestigadorCorreo(), contenido);
    }

}
