package com.rica.api.investigadores_service.investigadores.aplicacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rica.api.investigadores_service.investigadores.dominio.CorreoDuplicadoException;
import com.rica.api.investigadores_service.investigadores.dominio.CorreoInstitucional;
import com.rica.api.investigadores_service.investigadores.dominio.Investigador;

@ExtendWith(MockitoExtension.class)
public class InvestigadorFactoryTest {

    @Mock
    private RepositorioInvestigadores repositorioInvestigadores;

    @InjectMocks
    private InvestigadorFactory investigadorFactory;

    @Test
    void crearDevuelveInvestigadorCuandoElCorreoNoExiste() {
        when(repositorioInvestigadores.existeCorreoInstitucional("ana.torres@uptc.edu.co"))
                .thenReturn(false);

        Investigador resultado = investigadorFactory.crear(
                "Ana Torres", "ana.torres@uptc.edu.co", "GIT-UPTC");

        assertThat(resultado.getId()).isNull();
        assertThat(resultado.getNombreCompleto()).isEqualTo("Ana Torres");
        assertThat(resultado.getCorreoInstitucional())
                .isEqualTo(new CorreoInstitucional("ana.torres@uptc.edu.co"));
        assertThat(resultado.getGrupoDeInvestigacion()).isEqualTo("GIT-UPTC");
        verify(repositorioInvestigadores).existeCorreoInstitucional("ana.torres@uptc.edu.co");
    }

    @Test
    void crearLanzaExcepcionCuandoElCorreoYaExiste() {
        when(repositorioInvestigadores.existeCorreoInstitucional("ana.torres@uptc.edu.co"))
                .thenReturn(true);

        assertThatThrownBy(() -> investigadorFactory.crear(
                "Ana Torres", "ana.torres@uptc.edu.co", "GIT-UPTC"))
                .isInstanceOf(CorreoDuplicadoException.class)
                .hasMessageContaining("ana.torres@uptc.edu.co");
    }

    @Test
    void crearLanzaExcepcionCuandoElCorreoNoEsInstitucional() {
        assertThatThrownBy(() -> investigadorFactory.crear(
                "Ana Torres", "ana@gmail.com", "GIT-UPTC"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("@uptc.edu.co");

        verify(repositorioInvestigadores, never()).existeCorreoInstitucional("ana@gmail.com");
    }
}