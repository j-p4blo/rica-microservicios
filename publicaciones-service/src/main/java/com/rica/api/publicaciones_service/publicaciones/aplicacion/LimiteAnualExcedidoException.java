package com.rica.api.publicaciones_service.publicaciones.aplicacion;

/** El investigador ya alcanzó el máximo de publicaciones permitidas para ese año. GlobalExceptionHandler la traduce a HTTP 409. */
public class LimiteAnualExcedidoException extends RuntimeException {

    public LimiteAnualExcedidoException(String mensaje) {
        super(mensaje);
    }

}
