--------------------------------------------------- 
# _TALLER MICROSERVICIOS_

| Paquete / clase actual | Destino |
|---|---|
| `investigadores.*` | `investigadores_service` |
| `publicaciones.*` | `publicaciones_service` |
| `compartido.GlobalExceptionHandler`, `RecursoNoEncontradoException` | Se duplica en ambos |
| `plataforma.StatusController`, CorsConfig | Se duplica en ambos, adaptado |
| `plataforma.ArranqueInformativo`, `SaludoInstitucionalService` | Se descarta (demo del Tutorial 2) |

* Decisión de arquitectura: duplicar `compartido` evita acoplar los servicios,
pero obliga a mantener dos copias. 

