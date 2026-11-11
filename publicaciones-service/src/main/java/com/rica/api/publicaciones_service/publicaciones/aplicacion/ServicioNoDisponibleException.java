package com.rica.api.publicaciones_service.publicaciones.aplicacion;

/**
 * [L6 · Paso 3] "No pude hablar con el otro servicio": se distingue del 404 ("el investigador no existe").
 * GlobalExceptionHandler la traduce a HTTP 503 Service Unavailable.
 */
public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
