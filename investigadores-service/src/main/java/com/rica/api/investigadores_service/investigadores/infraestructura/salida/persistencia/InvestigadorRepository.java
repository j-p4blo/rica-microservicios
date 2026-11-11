package com.rica.api.investigadores_service.investigadores.infraestructura.salida.persistencia;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rica.api.investigadores_service.investigadores.dominio.Investigador;

public interface InvestigadorRepository extends JpaRepository<Investigador, Long>{

     boolean existsByCorreoInstitucional_Valor(String valor);

    Optional<Investigador> findByCorreoInstitucional_Valor(String correoInstitucional);
   
}
