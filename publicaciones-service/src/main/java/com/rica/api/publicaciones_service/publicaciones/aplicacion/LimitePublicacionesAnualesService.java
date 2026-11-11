package com.rica.api.publicaciones_service.publicaciones.aplicacion;

import org.springframework.stereotype.Service;

import com.rica.api.publicaciones_service.publicaciones.dominio.Publicacion;

@Service
public class LimitePublicacionesAnualesService {

    private static final int MAXIMO_POR_ANIO = 5;

    private final RepositorioPublicaciones repositorioPublicaciones;

    public LimitePublicacionesAnualesService(RepositorioPublicaciones repositorioPublicaciones) {
        this.repositorioPublicaciones = repositorioPublicaciones;
    }

    public boolean puedeRegistrar(Publicacion nueva) {
        long registradasEsteAnio = repositorioPublicaciones.contarPorInvestigadorYAnio(
                nueva.getInvestigadorCorreo(), nueva.getAnio());
        return registradasEsteAnio < MAXIMO_POR_ANIO;
    }
}
