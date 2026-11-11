package com.rica.api.investigadores_service.investigadores.aplicacion;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.rica.api.investigadores_service.investigadores.dominio.Investigador;

class InvestigadorServiceConFalsoTest {

    @Test 
    void registraYRecuperaUnInvestigadorSinNingunaDependenciaDeSpringNiDeBaseDeDatos() {
    RepositorioInvestigadoresFalso repositorio = new RepositorioInvestigadoresFalso();
    InvestigadorFactory factory = new InvestigadorFactory(repositorio);
    InvestigadorService service = new InvestigadorService(repositorio, factory, (topico, clave, contenido) -> { });

    Investigador guardado = service.registrar("Ana Torres", "ana.torres@uptc.edu.co", "GIT-UPTC");

    assertThat(guardado.getId()).isNotNull();
    assertThat(service.buscarPorId(guardado.getId()).getNombreCompleto()).isEqualTo("Ana Torres");
  }

}
