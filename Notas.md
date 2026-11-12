
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

---------------------------------------------------
## _TALLER CQRS (LECCIÓN 8)

### Plan de CQRS

| Servicio | Lado | Qué hace |
|---|---|---|
| `investigadores-service` | Escritura | Publica `investigadores.registrados`, `investigadores.en-eliminacion` y (nuevo) `investigadores.eliminados`, vía Outbox |
| `publicaciones-service` | Escritura | Publica (nuevo) `publicaciones.registradas` solo después de guardar con éxito |
| `consultas-service` (nuevo, puerto 8083) | Lectura | Escucha los cuatro eventos y mantiene una **ficha por investigador** en su propia base MongoDB (`rica_consultas`). Solo responde `GET` |

### Endpoints de consultas-service

| N° | Metodo | EndPoint |
|---|---|---|
| 10 | `GET` | `http://localhost:8083/api/status` |
| 11 | `GET` | `http://localhost:8083/api/consultas/investigadores/{correo}` |
| 12 | `GET` | `http://localhost:8083/api/consultas/investigadores?estado=EN_ELIMINACION` |
| 13 | `GET` | `http://localhost:8083/api/consultas/ranking?top=3` |

Las mismas rutas están disponibles por el Gateway en `http://localhost:8000/api/consultas/...`.

### Decisiones y limitaciones anotadas

* El lado de lectura **nunca comparte base de datos** con el de escritura: mismo contenedor MongoDB (puerto 27019), base `rica_consultas`.
* `FichaInvestigador.agregarPublicacion` es **idempotente**: un evento duplicado no duplica la publicación.
* `nombreCompleto` puede ser `null`: la ficha se crea aunque el evento de la publicación llegue antes que el del investigador (no hay orden garantizado entre tópicos).
* `totalPublicaciones` se **guarda** (no se calcula) para poder ordenar por él en MongoDB (ranking).
* `ProyectorFichas` usa métodos `synchronized`; con varias instancias haría falta bloqueo optimista (`@Version`).
* Reconstrucción: se borra la colección `fichas_investigador` y se cambia el `group-id` de Kafka; con `auto-offset-reset: earliest` el grupo nuevo reproduce todos los eventos. Las publicaciones anteriores a la Lección 8 no se pueden reconstruir porque antes no se publicaba ningún evento de publicaciones.
* Trampa de reproducir eventos: `investigadores.registrados` e `investigadores.eliminados` están en tópicos distintos y Kafka no garantiza el orden entre tópicos; una ficha eliminada puede "resucitar" al reconstruir. En producción se evita publicando todos los eventos de un agregado en un solo tópico, con número de versión.
* `publicaciones-service` ahora valida el límite anual de publicaciones (`LimitePublicacionesAnualesService`) antes de guardar y responde 409 si se excede.
