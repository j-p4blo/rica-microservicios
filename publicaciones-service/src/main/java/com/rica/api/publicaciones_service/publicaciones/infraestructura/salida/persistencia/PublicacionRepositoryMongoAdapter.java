package com.rica.api.publicaciones_service.publicaciones.infraestructura.salida.persistencia;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.rica.api.publicaciones_service.publicaciones.aplicacion.RepositorioPublicaciones;
import com.rica.api.publicaciones_service.publicaciones.dominio.Publicacion;

@Component
public class PublicacionRepositoryMongoAdapter implements RepositorioPublicaciones {

    private final PublicacionRepository publicacionRepository;

    public PublicacionRepositoryMongoAdapter(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    @Override
    public List<Publicacion> listarPorInvestigador(String investigadorCorreo) {
        return publicacionRepository.findByInvestigadorCorreo(investigadorCorreo);
    }

    @Override
    public Optional<Publicacion> buscarPorId(String id) {
        return publicacionRepository.findById(id);
    }

    @Override
    public long contarPorInvestigadorYAnio(String investigadorCorreo, Integer anio) {
        return publicacionRepository.countByInvestigadorCorreoAndAnio(investigadorCorreo, anio);
    }

    @Override
    public Publicacion guardar(Publicacion publicacion) {
        return publicacionRepository.save(publicacion);
    }
}
