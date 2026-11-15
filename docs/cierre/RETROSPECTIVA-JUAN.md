# Retrospectiva individual

## Autoevaluación (1 = no lo logro, 4 = lo hago con autonomía)

**RA1. Explico principios, patrones y estilos arquitectónicos: 4**
Evidencia: en `ARQUITECTURA.md` explico, para cada componente, qué problema resuelve su estilo (API Gateway, hexagonal, Outbox, Circuit Breaker, CQRS y Saga). En los tres ADR dejé el contexto, la alternativa descartada y los costos de cada decisión.

**RA2. Analizo ventajas, limitaciones y contextos de los estilos: 4**
Evidencia: `docs/cierre/MATRIZ.md` compara tres alternativas con pesos justificados y análisis de sensibilidad, y reconoce que el monolito modular empata si baja el peso del objetivo del curso. `docs/cierre/CASO.md` analiza el caso de Segment con su fuente original, y `docs/cierre/ATAM-LITE.md` clasifica riesgos, no-riesgos, sensibilidades y compromisos.

**RA3. Diseño arquitecturas con modelos, diagramas y estándares: 4**
Evidencia: el diagrama de contenedores C4 de `ARQUITECTURA.md`, con las conexiones síncronas y asíncronas marcadas, los tres ADR con su formato, y el árbol de utilidad de seis escenarios con medida en `ATAM-LITE.md`.

**RA5. Implemento arquitecturas modernas (microservicios, EDA, CQRS, DDD, hexagonal): 4**
Evidencia: Lección 6, Outbox y Saga entre investigadores y publicaciones. Lección 8, `consultas-service` con CQRS (rama `taller-cqrs`). Lección 9, consumidor idempotente y Dead Letter Topic (rama `taller-eda`). Lección 10, identificador de correlación y Actuator (rama `taller-observabilidad`). Lección 11, Dockerfile multi-etapa y CI con GitHub Actions (rama `taller-contenedores`). Los servicios de investigadores y publicaciones siguen arquitectura hexagonal, con puertos y adaptadores.

## Tres aprendizajes que me llevo

1. **Cada patrón existe por un problema concreto y cobra un costo.** Antes veía estos patrones como una lista de cosas modernas. Con el Outbox entendí que existe porque guardar en la base de datos y avisar a Kafka son dos escrituras distintas que pueden fallar por separado, y con CQRS entendí que una consulta rápida se paga con datos que tardan un momento en actualizarse.

2. **En un sistema con mensajes, lo normal es que algo llegue repetido o dañado.** Al trabajar el consumidor idempotente y la Dead Letter Topic de `consultas-service` aprendí que el código no puede asumir que cada mensaje llega una sola vez y bien formado. Hay que diseñar para que un repetido no cambie el resultado y para que uno dañado no frene a los demás.

3. **Evaluar con honestidad es parte del diseño.** Al armar la matriz de decisión vi que mi arquitectura gana por poco y que el resultado depende de cuánto pese el objetivo del curso. Eso me enseñó a escribir también lo que cuesta y lo que falla, no solo lo que sirve. El caso de Segment me mostró además que volver a un monolito puede ser una buena decisión cuando el contexto lo pide.

## Una decisión de arquitectura que tomaría distinto hoy, y por qué

Usaría Outbox también en `publicaciones-service` desde el principio. Hoy ese servicio guarda la publicación en MongoDB y después envía el mensaje a Kafka directamente. Si se cae justo entre las dos cosas, el mensaje se pierde y `consultas-service` queda desactualizado. En investigadores sí apliqué el patrón y funcionó bien, así que debí aplicar la misma regla a todos los servicios que avisan cambios. Es el primero de los tres riesgos que dejé documentados en `ATAM-LITE.md`, y se arregla con una solución que ya existe en el proyecto.

## Lo que me falta por aprender y cómo lo haría

- **Seguridad:** autenticación en el gateway y manejo de contraseñas fuera del repositorio. Lo haría leyendo la documentación de Spring Security y agregándola al gateway de RICA, empezando por una sola ruta protegida.
- **Reglas de arquitectura automáticas:** hoy los guardarraíles de `GOBERNANZA.md` están definidos pero dos de ellos no están implementados. Los implementaría con ArchUnit y un paso adicional en GitHub Actions.
- **Kafka con varios nodos y seguimiento completo de peticiones:** montaría en Docker Compose un clúster de Kafka con tres nodos y OpenTelemetry, y probaría qué pasa cuando apago un nodo.
- **Compensación en la saga:** me falta implementar el tiempo límite y la vuelta atrás cuando la confirmación no llega. Lo haría agregando un estado de eliminación fallida y una prueba que simule el servicio de publicaciones caído.

## Mi aporte al proyecto

RICA es un proyecto individual, así que todo el trabajo lo hice yo, de principio a fin: las cinco aplicaciones (gateway, eureka-server, investigadores-service, publicaciones-service y consultas-service), el pipeline de CI y los documentos de arquitectura y de cierre.

Evidencia en el historial de Git del repositorio: una rama por taller (`taller-cqrs`, `taller-eda`, `taller-observabilidad`, `taller-contenedores`), con sus commits, y los documentos `ARQUITECTURA.md` y `docs/cierre/`.
