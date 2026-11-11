
--------------------------------------------------- 
## _TALLER MICROSERVICIOS DATOS DE PRUEBA_

--------------------------------------------------- 
### _INVESTIGADORES_

| N° | Metodo | EndPoint |
|---|---|---|
| 1 || `GET` | `http://localhost:8080/api/status` |
| 2 || `POST` | `http://localhost:8080/api/investigadores` |
| 3 || `GET` | `http://localhost:8080/api/investigadores` |

* Body peticion #2

{
  "nombreCompleto": "Ana Torres",
  "correoInstitucional": "ana.torres@uptc.edu.co",
  "grupoDeInvestigacion": "GIT-UPTC"
}

--------------------------------------------------- 
### _PUBLICACIONES_

| N° | Metodo | EndPoint |
|---|---|---|
| 4 || `GET` | `http://localhost:8081/api/status` |
| 5 || `POST` | `http://localhost:8081/api/publicaciones` |
| 6 || `GET` | `http://localhost:8081/api/publicaciones/{id}	` |
| 7 || `GET` | `http://localhost:8081/api/publicaciones?investigadorCorreo=ana.torres@uptc.edu.co` |
| 8 || `POST` | `http://localhost:8081/api/publicaciones (correo inventado)` |
| 9 || `GET` | `http://localhost:8081/api/publicaciones/id-que-no-existe	` |

* Body peticion #5

{
  "investigadorCorreo": "ana.torres@uptc.edu.co",
  "titulo": "Arquitectura hexagonal en la practica",
  "tipo": "ARTICULO",
  "anio": 2026,
  "detalles": { "revista": "Revista UPTC", "doi": "10.1234/rica.2026" }
}

* Body peticion #8

{
  "investigadorCorreo": "fantasma.inexistente@uptc.edu.co",
  "titulo": "Publicacion de un investigador que no existe",
  "tipo": "ARTICULO",
  "anio": 2026,
  "detalles": {}
}


---------------------------------------------------
## _TALLER COMUNICACIÓN (LECCIÓN 6)

### Paso 1 · Plan de comunicación

| Necesidad | Patrón | Dónde |
|---|---|---|
| Confirmar que un investigador existe antes de registrar una publicación | RPI síncrono — gRPC | Paso 2 |
| No caer en cascada si investigadores-service está lento o caído | Circuit Breaker (Resilience4j) | Paso 3 |
| Que ningún servicio dependa de una dirección de red fija | Service Registry (Eureka) | Paso 4 |
| Un único punto de entrada para el frontend | API Gateway | Paso 5 |
| Notificar que se registró un investigador sin acoplar al publicador | Domain Event + Transactional Outbox (Kafka) | Paso 6 |
| Eliminar un investigador y marcar sus publicaciones sin transacción distribuida | Saga (coreografía) | Paso 7 |

### Decisiones y limitaciones anotadas

* La dirección gRPC sigue fija (`static://localhost:9090`): Eureka resuelve descubrimiento para llamadas HTTP balanceadas (`lb://` del Gateway), pero integrarlo con el balanceo de canales gRPC añade complejidad fuera del alcance del taller.
* El `.proto` se copia en cada servicio (solo cambia `java_package`): cada servicio genera sus propias clases y ninguno depende del código del otro. En un equipo real se versionaría en un repositorio compartido.
* `publicaciones-service` confirma la Saga con `KafkaTemplate` directo, sin outbox: si cae entre guardar y confirmar, la Saga queda detenida. En producción también necesitaría outbox.
* Falta la transacción de compensación de la Saga (temporizador que devuelva el estado a `ACTIVO` tras un plazo sin confirmación): queda como reto opcional.
