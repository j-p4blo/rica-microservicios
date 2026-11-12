package com.rica.api.consultas_service.consultas;

import java.time.Instant;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/** Tabla de eventos procesados: la base del patrón Idempotent Consumer. */
@Service
public class RegistroEventosProcesados {

    private final MongoTemplate mongoTemplate;

    public RegistroEventosProcesados(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /** @return true si es la primera vez que se ve este evento; false si ya se había procesado. */
    public boolean primeraVez(String idEvento) {
        try {
            mongoTemplate.insert(new EventoProcesado(idEvento, Instant.now()));
            return true;
        } catch (DuplicateKeyException yaExiste) {
            return false;
        }
    }

}
