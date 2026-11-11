package com.rica.api.investigadores_service.investigadores.infraestructura.entrada.grpc;

import com.rica.investigadoresservice.grpc.InvestigadorGrpcGrpc;
import com.rica.investigadoresservice.grpc.InvestigadorRequest;
import com.rica.investigadoresservice.grpc.InvestigadorResponse;
import com.rica.api.investigadores_service.investigadores.aplicacion.RepositorioInvestigadores;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

/**
 * [L6 · Paso 2A · gRPC (RPI sincrono)] Adaptador de ENTRADA del hexagono, igual que el controlador REST:
 * atiende la llamada ExisteInvestigador que hace publicaciones-service por el puerto 9090. Usa el puerto
 * RepositorioInvestigadores (existeCorreoInstitucional; en el taller se llama existeCorreo).
 * Spring registra solo cualquier bean que extienda una clase ...ImplBase.
 */
@Service
public class InvestigadorGrpcService extends InvestigadorGrpcGrpc.InvestigadorGrpcImplBase {

    private final RepositorioInvestigadores repositorio;

    public InvestigadorGrpcService(RepositorioInvestigadores repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void existeInvestigador(InvestigadorRequest request,
                                   StreamObserver<InvestigadorResponse> responseObserver) {
        boolean existe = repositorio.existeCorreoInstitucional(request.getCorreoInstitucional());
        responseObserver.onNext(InvestigadorResponse.newBuilder().setExiste(existe).build());
        responseObserver.onCompleted();
    }

}
