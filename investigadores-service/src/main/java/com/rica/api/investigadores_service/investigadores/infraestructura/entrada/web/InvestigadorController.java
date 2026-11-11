package com.rica.api.investigadores_service.investigadores.infraestructura.entrada.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rica.api.investigadores_service.investigadores.aplicacion.InvestigadorUseCase;
import com.rica.api.investigadores_service.investigadores.dominio.Investigador;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/investigadores")
public class InvestigadorController {

     private final InvestigadorUseCase investigadorService;

    public InvestigadorController(InvestigadorUseCase investigadorService) {
        this.investigadorService = investigadorService;
    }

    @GetMapping
    public List<InvestigadorResponse> listar() {
        return investigadorService.listarTodos().stream()
                .map(InvestigadorMapper::aResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public InvestigadorResponse buscarPorId(@PathVariable Long id) {
        Investigador investigador = investigadorService.buscarPorId(id);
        return InvestigadorMapper.aResponse(investigador);
    }

    @PostMapping
    public ResponseEntity<InvestigadorResponse> registrar(@Valid @RequestBody InvestigadorRequest request) {
        Investigador guardado = investigadorService.registrar(
            request.getNombreCompleto(),
            request.getCorreoInstitucional(),
            request.getGrupoInvestigacion());
        InvestigadorResponse response = InvestigadorMapper.aResponse(guardado);
        return ResponseEntity
                .created(URI.create("/api/investigadores/" + guardado.getId()))
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        investigadorService.iniciarEliminacion(id);
        return ResponseEntity.accepted().build();
    }
}
