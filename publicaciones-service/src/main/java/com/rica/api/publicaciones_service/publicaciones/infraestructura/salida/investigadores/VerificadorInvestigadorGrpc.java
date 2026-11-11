package com.rica.api.publicaciones_service.publicaciones.infraestructura.salida.investigadores;

import com.rica.publicacionesservice.grpc.InvestigadorGrpcGrpc.InvestigadorGrpcBlockingStub;
import com.rica.publicacionesservice.grpc.InvestigadorRequest;
import com.rica.api.publicaciones_service.publicaciones.aplicacion.ServicioNoDisponibleException;
import com.rica.api.publicaciones_service.publicaciones.aplicacion.VerificadorInvestigador;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * [L6 · Pasos 2B y 3 · gRPC + Circuit Breaker] Adaptador REAL del puerto VerificadorInvestigador: reemplaza a
 * VerificadorInvestigadorPendiente. Llama a investigadores-service por gRPC (plazo de 5 s) protegido por un
 * Circuit Breaker de Resilience4j. PublicacionService no cambio: solo cambio el adaptador (hexagonal, L4).
 */
@Component
public class VerificadorInvestigadorGrpc implements VerificadorInvestigador {

    private static final Logger log = LoggerFactory.getLogger(VerificadorInvestigadorGrpc.class);

    private final InvestigadorGrpcBlockingStub stub;
    private final CircuitBreaker circuitBreaker;

    public VerificadorInvestigadorGrpc(InvestigadorGrpcBlockingStub stub) {
        this.stub = stub;
        this.circuitBreaker = CircuitBreaker.of("investigadores-service",
                CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .build());
        this.circuitBreaker.getEventPublisher().onStateTransition(
                e -> log.warn("Circuit Breaker investigadores-service: {}", e.getStateTransition()));
    }

    @Override
    public boolean existe(String correoInstitucional) {
        try {
            return circuitBreaker.executeSupplier(() -> {
                var request = InvestigadorRequest.newBuilder()
                        .setCorreoInstitucional(correoInstitucional)
                        .build();
                return stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                        .existeInvestigador(request)
                        .getExiste();
            });
        } catch (CallNotPermittedException e) {
            log.warn("Circuit Breaker ABIERTO: se rechaza la llamada sin intentar la red");
            throw new ServicioNoDisponibleException(
                    "investigadores-service no está disponible (circuito abierto)", e);
        } catch (StatusRuntimeException e) {
            throw new ServicioNoDisponibleException(
                    "No se pudo contactar a investigadores-service: " + e.getStatus().getCode(), e);
        }
    }

}
