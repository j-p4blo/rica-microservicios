package com.rica.api.publicaciones_service.publicaciones.infraestructura.entrada.mensajeria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * [L6 · Paso 6C · consumidor de eventos] Recibe investigadores.registrados desde Kafka; por ahora solo lo
 * registra en el log (ni publicaciones ni investigadores se llaman directamente).
 */
@Component
public class InvestigadorRegistradoListener {

    private static final Logger log = LoggerFactory.getLogger(InvestigadorRegistradoListener.class);

    @KafkaListener(topics = "investigadores.registrados")
    public void alRegistrarseInvestigador(String mensaje) {
        log.info("Evento recibido [investigadores.registrados]: {}", mensaje);
    }

}
