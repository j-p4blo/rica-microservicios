package com.rica.api.consultas_service.consultas;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Marca de que un evento ya se procesó. El _id único hace que la segunda inserción falle. */
@Document(collection = "eventos_procesados")
public class EventoProcesado {

    @Id
    private String id;
    private Instant procesadoEn;

    public EventoProcesado(String id, Instant procesadoEn) {
        this.id = id;
        this.procesadoEn = procesadoEn;
    }

    public String getId() { return id; }
    public Instant getProcesadoEn() { return procesadoEn; }

}
