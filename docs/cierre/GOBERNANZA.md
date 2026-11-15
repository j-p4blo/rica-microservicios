# Gobernanza de la arquitectura de RICA

## Guardarraíles de arquitectura

1. Regla: las pruebas unitarias de los cinco servicios deben pasar antes de integrar un cambio a `main`.
   Se verifica con: GitHub Actions (`.github/workflows/ci.yml`), que ejecuta las pruebas de cada servicio sin necesitar Kafka ni bases de datos.
   Cuándo: en cada push y en cada Pull Request hacia `main`.
   Estado: activo desde la Lección 11.

2. Regla: las carpetas `dominio` y `aplicacion` de investigadores-service y publicaciones-service no importan clases de `infraestructura`, ni de Kafka, gRPC, la capa web (`springframework.web`) ni `jakarta.servlet`.
   Se verifica con: una prueba ArchUnit dentro de cada servicio, que falla si aparece alguno de esos imports.
   Cuándo: en cada push y Pull Request, porque corre junto con las demás pruebas en GitHub Actions.
   Estado: por implementar. Hoy se cumple, se comprobó buscando esos imports en el código.

3. Regla: cada servicio usa solo su propia base de datos (investigadores usa PostgreSQL `rica`, publicaciones usa MongoDB `rica` y consultas usa MongoDB `rica_consultas`).
   Se verifica con: un paso extra en GitHub Actions que falla si el archivo de configuración de un servicio contiene la dirección de la base de otro.
   Cuándo: en cada push y Pull Request hacia `main`.
   Estado: por implementar. Hoy se cumple, se comprobó leyendo los tres archivos de configuración.

4. Regla: todo mensaje nuevo que se publique en Kafka lleva el identificador de petición (`correlationId`).
   Se verifica con: una prueba unitaria por publicador que revisa que el mensaje enviado incluya ese campo.
   Cuándo: en cada push y Pull Request, junto con las demás pruebas.
   Estado: por implementar.

## Cómo se toman las decisiones

- Decisiones locales (dentro de un servicio): las toma quien lo implementa y las deja anotadas en el mensaje del commit.
- Decisiones que cruzan fronteras (mensajes de Kafka, contratos gRPC, bases de datos): se escribe un ADR en `ARQUITECTURA.md`. Como el proyecto lo mantiene una sola persona, el ADR se relee un día después de escribirlo y se integra por Pull Request.
- Las excepciones a una regla se registran como deuda técnica con una condición de revisión.

## Deuda técnica reconocida

| Qué es | Por qué se aceptó | Riesgo que trae | Cuándo se revisaría |
|---|---|---|---|
| Publicaciones guarda en MongoDB y luego envía el mensaje a Kafka sin Outbox | Se quiso mantener corta la práctica de eventos; el Outbox ya estaba en investigadores | Si el servicio se cae entre las dos cosas, el mensaje se pierde y consultas queda desactualizado | Antes de que RICA se use con datos reales |
| La saga de eliminación no tiene tiempo límite ni vuelta atrás | El taller pedía el flujo normal de la saga, no su compensación | Un investigador puede quedar "en eliminación" para siempre | Antes de que RICA se use con datos reales |
| Los mensajes de investigadores van por varios tópicos y Kafka no garantiza orden entre tópicos | Un tópico por tipo de mensaje era lo más simple de entender | Al reconstruir consultas, una ficha eliminada puede volver a aparecer | Cuando se necesite reconstruir las consultas con datos que importen |
| Un solo Kafka sin réplicas, dos bases de MongoDB en una misma instancia, gateway sin autenticación y contraseñas en los archivos de configuración | RICA corre en un computador, sin usuarios externos ni datos reales | Si se publicara tal como está, cualquiera podría usarlo y se podrían perder datos si algo se daña | Antes de cualquier publicación del sistema fuera del computador de desarrollo |
| Las clases `Investigador` y `Publicacion` llevan anotaciones de la base de datos (JPA y Spring Data) | Usar la misma clase para el dominio y para la base evitó duplicar código | Si el modelo de datos cambia mucho, el dominio queda atado a la base | Cuando cambie de forma importante el modelo de datos |
| El identificador de petición no se retoma en consultas al procesar mensajes | La Lección 10 pedía propagarlo en la saga entre investigadores y publicaciones | El seguimiento se corta cuando el mensaje llega a consultas | Cuando se agregue el guardarraíl 4 |

## Radar tecnológico

- **Adoptamos:** Spring Boot, Kafka con el patrón Outbox y arquitectura hexagonal, porque ya se usan y mostraron que funcionan para mantener los datos coherentes y el código fácil de probar.
- **Evaluaríamos:** ArchUnit para automatizar las reglas de arquitectura, y OpenTelemetry para ver el recorrido completo de una petición, porque dejarían de depender de que alguien se acuerde de revisarlo a mano.
- **Evitaríamos:** las transacciones distribuidas (2PC) y compartir una misma base de datos entre servicios, porque amarran a los servicios y quitan la independencia que se busca con esta arquitectura.
