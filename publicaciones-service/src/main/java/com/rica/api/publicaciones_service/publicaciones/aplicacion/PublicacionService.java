package com.rica.api.publicaciones_service.publicaciones.aplicacion;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rica.api.publicaciones_service.compartido.RecursoNoEncontradoException;
import com.rica.api.publicaciones_service.publicaciones.dominio.Publicacion;

@Service
public class PublicacionService implements PublicacionUseCase {

    private final RepositorioPublicaciones repositorioPublicaciones;
    private final VerificadorInvestigador verificadorInvestigador;
    private final LimitePublicacionesAnualesService limitePublicacionesAnualesService;
    private final PublicadorEventosPublicaciones publicadorEventos;

    public PublicacionService(RepositorioPublicaciones repositorioPublicaciones,
            VerificadorInvestigador verificadorInvestigador,
            LimitePublicacionesAnualesService limitePublicacionesAnualesService,
            PublicadorEventosPublicaciones publicadorEventos) {
        this.repositorioPublicaciones = repositorioPublicaciones;
        this.verificadorInvestigador = verificadorInvestigador;
        this.limitePublicacionesAnualesService = limitePublicacionesAnualesService;
        this.publicadorEventos = publicadorEventos;
    }

    @Override
    public Publicacion registrar(Publicacion publicacion) {
        if (!verificadorInvestigador.existe(publicacion.getInvestigadorCorreo())) {
            throw new RecursoNoEncontradoException(
                    "No existe un investigador con correo " + publicacion.getInvestigadorCorreo());
        }
        if (!limitePublicacionesAnualesService.puedeRegistrar(publicacion)) {
            throw new LimiteAnualExcedidoException(
                    "El investigador " + publicacion.getInvestigadorCorreo()
                            + " ya alcanzó el máximo de publicaciones registradas para " + publicacion.getAnio());
        }
        Publicacion guardada = repositorioPublicaciones.guardar(publicacion);
        // L8 · Paso 2: el evento se anuncia SOLO después de guardar con éxito.
        // Si el comando falla (investigador inexistente, límite anual excedido) no se anuncia nada.
        publicadorEventos.publicacionRegistrada(guardada);
        return guardada;
    }

    @Override
    public List<Publicacion> listarPorInvestigador(String investigadorCorreo) {
        return repositorioPublicaciones.listarPorInvestigador(investigadorCorreo);
    }

    @Override
    public Publicacion buscarPorId(String id) {
        return repositorioPublicaciones.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                "No se encontró la publicación con id: " + id));
    }
}
