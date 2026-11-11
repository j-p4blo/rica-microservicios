package com.rica.api.investigadores_service.investigadores.infraestructura.salida.persistencia;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.rica.api.investigadores_service.investigadores.aplicacion.RepositorioInvestigadores;
import com.rica.api.investigadores_service.investigadores.dominio.Investigador;

@Component
public class InvestigadorRepositoryJpaAdapter implements RepositorioInvestigadores {

    private final InvestigadorRepository investigadorRepository;

    public InvestigadorRepositoryJpaAdapter(InvestigadorRepository investigadorRepository) {
        this.investigadorRepository = investigadorRepository;
    }

    @Override
    public List<Investigador> listarTodos() {
        return investigadorRepository.findAll();
    }

    @Override
    public Optional<Investigador> buscarPorId(Long id) {
        return investigadorRepository.findById(id);
    }

    @Override
    public boolean existeCorreoInstitucional(String correoInstitucional) {
        return investigadorRepository.existsByCorreoInstitucional_Valor(correoInstitucional);
    }

    @Override
    public Investigador guardar(Investigador investigador) {
        return investigadorRepository.save(investigador);
    }

    @Override
    public Optional<Investigador> buscarPorCorreo(String correoInstitucional) {
        return investigadorRepository.findByCorreoInstitucional_Valor(correoInstitucional);
    }

    @Override
    public void eliminar(Investigador investigador) {
        investigadorRepository.delete(investigador);
    }
}
