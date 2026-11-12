package com.rica.api.publicaciones_service.publicaciones.aplicacion;

import com.rica.api.publicaciones_service.publicaciones.dominio.Publicacion;

/**
 * [L8 · Paso 2 · puerto de salida] Lado de escritura de CQRS: cada vez que se registra una publicación
 * (comando), se anuncia el hecho para que otros servicios puedan construir sus propias vistas.
 * La capa de aplicación no conoce Kafka; el adaptador KafkaPublicadorEventosPublicaciones lo implementa.
 */
public interface PublicadorEventosPublicaciones {

    void publicacionRegistrada(Publicacion publicacion);

}
