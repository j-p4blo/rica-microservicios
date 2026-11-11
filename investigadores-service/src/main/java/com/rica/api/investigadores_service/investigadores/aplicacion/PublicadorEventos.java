package com.rica.api.investigadores_service.investigadores.aplicacion;

import java.util.Map;

/**
 * [L6 · Paso 6B · puerto de salida] "Publicar un evento": la capa de aplicacion no conoce tablas ni Kafka.
 * El adaptador OutboxPublicadorEventos lo implementa escribiendo en la tabla outbox.
 */
public interface PublicadorEventos {

    void publicar(String topico, String clave, Map<String, Object> contenido);

}
