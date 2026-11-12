package com.rica.api.publicaciones_service.publicaciones.aplicacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rica.api.publicaciones_service.compartido.RecursoNoEncontradoException;
import com.rica.api.publicaciones_service.publicaciones.dominio.Publicacion;

@ExtendWith(MockitoExtension.class)
class PublicacionServiceTest {

    private static final String CORREO = "ana.torres@uptc.edu.co";

    @Mock
    private RepositorioPublicaciones repositorioPublicaciones;

    @Mock
    private VerificadorInvestigador verificadorInvestigador;

    @Mock
    private LimitePublicacionesAnualesService limitePublicacionesAnualesService;

    @Mock
    private PublicadorEventosPublicaciones publicadorEventos;

    @InjectMocks
    private PublicacionService publicacionService;

    private static Publicacion publicacion(String id, String correo, String titulo, String tipo, int anio) {
        Publicacion publicacion = new Publicacion();
        publicacion.setId(id);
        publicacion.setInvestigadorCorreo(correo);
        publicacion.setTitulo(titulo);
        publicacion.setTipo(tipo);
        publicacion.setAnio(anio);
        publicacion.setDetalles(Map.of());
        return publicacion;
    }

    @Test
    void registrarPublicaElEventoDespuesDeGuardar() {
        Publicacion entrada = publicacion(null, CORREO, "Arquitecturas evolutivas", "articulo", 2026);
        Publicacion guardada = publicacion("abc123", CORREO, "Arquitecturas evolutivas", "articulo", 2026);
        when(verificadorInvestigador.existe(CORREO)).thenReturn(true);
        when(limitePublicacionesAnualesService.puedeRegistrar(entrada)).thenReturn(true);
        when(repositorioPublicaciones.guardar(entrada)).thenReturn(guardada);

        Publicacion resultado = publicacionService.registrar(entrada);

        assertThat(resultado.getId()).isEqualTo("abc123");
        verify(publicadorEventos).publicacionRegistrada(guardada);
    }

    @Test
    void registrarNoPublicaEventoSiElInvestigadorNoExiste() {
        when(verificadorInvestigador.existe("nadie@uptc.edu.co")).thenReturn(false);
        Publicacion entrada = publicacion(null, "nadie@uptc.edu.co", "X", "libro", 2026);

        assertThatThrownBy(() -> publicacionService.registrar(entrada))
                .isInstanceOf(RecursoNoEncontradoException.class);

        // el comando falla: no hay hecho que anunciar
        verify(publicadorEventos, never()).publicacionRegistrada(any());
    }

    @Test
    void registrarNoPublicaEventoSiSeExcedeElLimiteAnual() {
        Publicacion entrada = publicacion(null, CORREO, "Una mas", "articulo", 2026);
        when(verificadorInvestigador.existe(CORREO)).thenReturn(true);
        when(limitePublicacionesAnualesService.puedeRegistrar(entrada)).thenReturn(false);

        assertThatThrownBy(() -> publicacionService.registrar(entrada))
                .isInstanceOf(LimiteAnualExcedidoException.class);

        verify(repositorioPublicaciones, never()).guardar(any());
        verify(publicadorEventos, never()).publicacionRegistrada(any());
    }

}
