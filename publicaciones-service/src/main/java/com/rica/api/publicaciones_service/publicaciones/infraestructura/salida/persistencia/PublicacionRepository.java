package com.rica.api.publicaciones_service.publicaciones.infraestructura.salida.persistencia;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rica.api.publicaciones_service.publicaciones.dominio.Publicacion;

public interface PublicacionRepository extends MongoRepository<Publicacion, String> {

    List<Publicacion> findByInvestigadorCorreo(String investigadorCorreo);
    long countByInvestigadorCorreoAndAnio(String investigadorCorreo, Integer anio);

}
