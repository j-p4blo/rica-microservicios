package com.rica.api.investigadores_service.investigadores.aplicacion;

import org.springframework.stereotype.Component;

import com.rica.api.investigadores_service.investigadores.dominio.CorreoDuplicadoException;
import com.rica.api.investigadores_service.investigadores.dominio.CorreoInstitucional;
import com.rica.api.investigadores_service.investigadores.dominio.Investigador;

@Component
public class InvestigadorFactory {

    private final RepositorioInvestigadores repositorioInvestigadores;

    public InvestigadorFactory(RepositorioInvestigadores repositorioInvestigadores) {
        this.repositorioInvestigadores = repositorioInvestigadores;
    }

    public Investigador crear(String nombreCompleto, String correoInstitucional, String grupoInvestigacion) {
        CorreoInstitucional correo = new CorreoInstitucional(correoInstitucional);

        if (repositorioInvestigadores.existeCorreoInstitucional(correoInstitucional)) {
            throw new CorreoDuplicadoException(
                    "Ya existe un investigador registrado con el correo " + correo.valor());
        }

        return new Investigador(null, nombreCompleto, correo, grupoInvestigacion);
    }
}
