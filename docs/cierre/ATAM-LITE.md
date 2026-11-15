# Evaluación de la arquitectura de RICA (ATAM-lite)

## 1. Árbol de utilidad

Cada escenario dice qué pasa (estímulo), qué debe hacer el sistema (respuesta) y cómo se mide. La importancia (I) es para el negocio y la dificultad (D) es lo difícil que es lograrlo. Las dos van de A (alta) a B (baja).

| N° | Atributo | Escenario | I | D |
|---|---|---|---|---|
| E1 | Integridad de datos | Se registra un investigador y el servicio de investigadores se cae antes de avisarlo. Al volver a arrancar, el aviso sale igual. Medida: 0 avisos perdidos. | A | M |
| E2 | Integridad de datos | Se pide eliminar un investigador mientras el servicio de publicaciones está caído. La eliminación queda pendiente y termina sola cuando ese servicio vuelve, sin perder publicaciones y sin dejar al investigador a medias. Medida: 0 publicaciones sin marcar. | A | A |
| E3 | Disponibilidad | El servicio de investigadores no responde cuando alguien registra una publicación. El sistema responde con un error claro en menos de 6 segundos y el servicio de publicaciones sigue atendiendo otras peticiones. | A | M |
| E4 | Rendimiento de consulta | Alguien pide la ficha de un investigador. Se resuelve con 1 lectura a la base de datos y 0 llamadas a otros servicios. | M | B |
| E5 | Facilidad de probar | Se hace un push al repositorio. Las pruebas unitarias de los cinco servicios se ejecutan solas, sin Kafka ni bases de datos encendidas. | M | B |
| E6 | Seguimiento | Hay que investigar qué pasó con una eliminación. Con el identificador de la petición se puede seguir el recorrido en los registros (logs) de los servicios. | M | M |

El escenario E2 es el único A/A, así que es el que más se analiza. E1 y E3 son A/M y también se analizan.

## 2. Análisis de los escenarios importantes

### E1: aviso de investigador registrado

- **Qué lo sostiene:** el patrón Outbox. El investigador y el aviso se guardan juntos en la misma transacción de PostgreSQL, y un proceso aparte revisa cada 2 segundos si hay avisos pendientes y los envía a Kafka.
- **Qué podría fallar:** que el envío a Kafka falle varias veces seguidas. En ese caso el aviso se queda en la tabla y se vuelve a intentar, así que solo se retrasa, no se pierde.
- **Clasificación:** no-riesgo. Además, el intervalo de 2 segundos es una sensibilidad: define cuánto puede tardar un aviso en salir.

### E2: eliminación coherente (Saga)

- **Qué lo sostiene:** la Saga coreografiada. El investigador pasa a "en eliminación", publicaciones marca sus publicaciones como huérfanas y confirma, y solo entonces se borra de verdad. Si un servicio está caído, Kafka guarda el mensaje y el proceso sigue cuando vuelva.
- **Qué podría fallar:** no hay tiempo límite ni vuelta atrás. Si la confirmación nunca llega, el investigador se queda "en eliminación" para siempre. Tampoco hay una pantalla ni un aviso que lo muestre.
- **Clasificación:** riesgo (R2). También es un compromiso: no hace falta un componente central, pero el proceso queda repartido y es difícil de seguir.

### E3: servicio de investigadores caído

- **Qué lo sostiene:** la llamada gRPC con un máximo de 5 segundos de espera y un Circuit Breaker (ventana de 10 llamadas, mínimo 5 llamadas, se abre con 50 % de fallos y espera 10 segundos antes de volver a intentar).
- **Qué podría fallar:** la cadena de llamadas síncronas cliente, gateway, publicaciones e investigadores es de varios saltos. Si los tiempos no están bien puestos, un servicio lento podría ir acumulando peticiones esperando.
- **Clasificación:** no-riesgo, porque el límite de tiempo y el circuito evitan que se acumulen. Los cinco números de la configuración son sensibilidades. También es un compromiso: la respuesta siempre está al día, pero sin investigadores no se pueden registrar publicaciones.

## 3. Riesgos

| N° | Riesgo | Dónde está |
|---|---|---|
| R1 | Guardar y avisar por separado: publicaciones guarda en MongoDB y después envía el mensaje directamente a Kafka. Si se cae entre las dos cosas, el mensaje se pierde y consultas queda desactualizado. | publicaciones-service |
| R2 | La saga no tiene tiempo límite ni compensación. Un investigador puede quedar "en eliminación" para siempre. | investigadores-service y publicaciones-service |
| R3 | Kafka no garantiza el orden entre tópicos distintos. Al reconstruir las consultas, una ficha ya eliminada podría volver a aparecer. | consultas-service |
| R4 | Infraestructura mínima y sin seguridad: un solo Kafka con una partición y sin réplicas, dos bases de MongoDB en la misma instancia, gateway sin autenticación y contraseñas en los archivos de configuración. | Todo el sistema |
| R5 | El identificador de la petición no se retoma cuando consultas procesa los mensajes, así que el seguimiento queda cortado ahí. | consultas-service |

## 4. No-riesgos

- El patrón Outbox en investigadores: el aviso no se pierde aunque el servicio se caiga.
- El Circuit Breaker con límite de 5 segundos: un servicio caído no deja trabado a otro.
- El consumidor idempotente y la Dead Letter Topic en consultas: un mensaje repetido no descuadra el contador y un mensaje dañado no frena a los demás.
- La ficha del investigador guardada como un solo documento: la consulta es una sola lectura.
- Los puertos de la arquitectura hexagonal: permiten probar la lógica con repositorios falsos sin Spring ni bases de datos.

## 5. Sensibilidades

- Intervalo del Outbox (2 segundos): decide cuánto tarda un cambio en llegar a consultas.
- Límite de espera de gRPC (5 segundos) y valores del Circuit Breaker (ventana 10, mínimo 5, 50 %, 10 segundos): deciden qué tan rápido falla publicaciones y qué tan pronto se recupera.
- Reintentos de consultas (3 intentos, esperando 1, 2 y 4 segundos): deciden cuánto se tarda en mandar un mensaje dañado a la Dead Letter Topic.
- Particiones y réplicas de Kafka: hoy es una partición y una copia, lo que da orden total pero ningún paralelismo ni tolerancia a la caída del servidor de Kafka.

## 6. Compromisos

- **CQRS:** más rendimiento de consulta, pero la información puede tardar un poco en aparecer (consistencia eventual) y hay datos repetidos.
- **gRPC síncrono entre publicaciones e investigadores:** la validación siempre está al día, pero un servicio depende del otro mientras se registra una publicación.
- **Saga coreografiada:** no necesita un componente central, pero el proceso queda repartido entre servicios y cuesta seguirlo.
- **Una base de datos por servicio:** cada servicio es independiente, pero no se pueden hacer consultas que junten datos de varios ni transacciones entre ellos.

## 7. Los 3 mayores riesgos y qué se hace con cada uno

1. **R1, guardar y avisar por separado en publicaciones.** *Mitigación:* aplicar el mismo Outbox que ya usa investigadores, de modo que la publicación y su mensaje se guarden juntos. Es el riesgo más fácil de corregir porque ya existe el patrón en el proyecto.
2. **R2, saga sin tiempo límite.** *Mitigación:* agregar un estado "eliminación fallida" y un tiempo máximo de espera de la confirmación. Al vencerse, el investigador vuelve a "activo" y se registra el error para revisarlo.
3. **R4, infraestructura mínima y sin seguridad.** *Mitigación:* aceptar el riesgo por ahora. RICA corre en un computador con fines académicos, sin datos reales ni usuarios externos. Antes de cualquier uso real hay que agregar autenticación en el gateway, sacar las contraseñas del repositorio y usar un Kafka con varias copias.
