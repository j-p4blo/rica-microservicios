package com.rica.api.consultas_service.consultas;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FichaRepository extends MongoRepository<FichaInvestigador, String> {

    List<FichaInvestigador> findByEstado(String estado);

    List<FichaInvestigador> findAllByOrderByTotalPublicacionesDesc(Pageable pageable);

}
