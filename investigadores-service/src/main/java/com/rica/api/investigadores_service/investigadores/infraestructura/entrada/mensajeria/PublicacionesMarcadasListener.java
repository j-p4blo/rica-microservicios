package com.rica.api.investigadores_service.investigadores.infraestructura.entrada.mensajeria;

import com.rica.api.investigadores_service.investigadores.aplicacion.InvestigadorUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * [L6 · Paso 7A · Saga (coreografia)] Ultimo eslabon en este servicio: cuando publicaciones-service confirma que
 * marco las publicaciones, se hace el borrado fisico del investigador.
 */
@Component
public class PublicacionesMarcadasListener {

    private static final Logger log = LoggerFactory.getLogger(PublicacionesMarcadasListener.class);

    private final InvestigadorUseCase investigadorService;
    private final ObjectMapper objectMapper;

    public PublicacionesMarcadasListener(InvestigadorUseCase investigadorService, ObjectMapper objectMapper) {
        this.investigadorService = investigadorService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "publicaciones.marcadas-huerfanas")
    public void alMarcarsePublicaciones(String mensaje) {
        String correo = objectMapper.readTree(mensaje).get("correoInstitucional").asString();
        log.info("Saga: publicaciones confirmó para {}. Eliminando investigador.", correo);
        investigadorService.confirmarEliminacion(correo);
    }

}
