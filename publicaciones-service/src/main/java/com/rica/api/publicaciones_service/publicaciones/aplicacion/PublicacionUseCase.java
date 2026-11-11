package com.rica.api.publicaciones_service.publicaciones.aplicacion;

import java.util.List;

import com.rica.api.publicaciones_service.publicaciones.dominio.Publicacion;

public interface PublicacionUseCase {

    Publicacion registrar(Publicacion publicacion);

    List<Publicacion> listarPorInvestigador(String investigadorCorreo);

    Publicacion buscarPorId(String id);
}
