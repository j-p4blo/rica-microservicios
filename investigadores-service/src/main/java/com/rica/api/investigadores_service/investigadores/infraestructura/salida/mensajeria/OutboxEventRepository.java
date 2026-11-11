package com.rica.api.investigadores_service.investigadores.infraestructura.salida.mensajeria;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop50ByPublicadoFalseOrderByIdAsc();

}
