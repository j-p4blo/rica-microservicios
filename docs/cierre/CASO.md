# Caso de estudio: Segment vuelve de los microservicios a un monolito

## 1. Contexto

Segment (hoy parte de Twilio) recibe cientos de miles de eventos por segundo de aplicaciones web y móviles y los reenvía a más de cien servicios de otras empresas, que el artículo llama "destinos" (por ejemplo Google Analytics). Al principio había una sola cola para todos los destinos. Cuando un destino fallaba, los reintentos llenaban la cola y todos los demás se retrasaban.

Para evitarlo, el equipo creó un servicio y una cola por cada destino y, más tarde, un repositorio por destino. Con el tiempo llegaron a más de 140 servicios y a unos tres destinos nuevos cada mes. El equipo era pequeño: el propio artículo cuenta que tres ingenieros a tiempo completo se iban en mantener el sistema funcionando.

## 2. Decisión tomada

Juntar los más de 140 servicios en un solo servicio (un monolito) y todo el código en un solo repositorio. Para poder hacerlo reemplazaron las colas individuales por un componente nuevo llamado Centrifuge, y escribieron una herramienta (Traffic Recorder) que graba las respuestas de los destinos para que las pruebas no dependan de internet. También se comprometieron a usar una sola versión de cada una de las 120 librerías que compartían.

## 3. Resultado

- Desplegar un cambio pasó de tocar más de 140 servicios a que una persona lo haga en unos minutos.
- Las pruebas de más de 140 destinos, que podían tardar hasta una hora, pasaron a tardar milisegundos.
- Las mejoras hechas a las librerías compartidas subieron de 32 en la época de los microservicios a 46 un año después.
- Dejaron de avisar a la persona de guardia por picos en destinos pequeños, porque un grupo grande de procesos absorbe la carga.
- Perdieron algunas cosas y lo reconocen: si un destino tiene un error, puede tumbar a todos; la memoria de apoyo (caché) rinde menos; y actualizar una librería puede romper varios destinos a la vez.

El artículo aclara que Segment sigue usando microservicios en otras partes de su infraestructura. La conclusión no es que los microservicios sean malos, sino que para ese caso concreto no servían.

## 4. Qué tendría que ser cierto en RICA para que esta lección se aplicara

- Que RICA tuviera muchos servicios casi iguales entre sí, que se multiplicaran con cada novedad. En Segment cada destino era una copia con pequeñas diferencias.
- Que los servicios compartieran librerías cuyas versiones se fueran separando con el tiempo.
- Que el mantenimiento del sistema consumiera a la mayoría del equipo y que hubiera alertas nocturnas por picos de carga.
- Que las pruebas dependieran de sistemas externos y fallaran por causas ajenas al código.

## 5. ¿Se aplica? Parcialmente

**No se aplica en lo principal.** RICA tiene tres servicios de negocio con funciones distintas, cada uno con su propio tipo de datos. No son copias de un mismo servicio y no crecen por cada novedad. Tampoco hay carga que justifique alertas nocturnas, así que el problema que llevó a Segment a volver al monolito no está presente.

**Sí se aplica en tres puntos:**

1. **Pruebas que no dependen de nada externo.** Segment sufría porque sus pruebas hacían llamadas por internet. En RICA ocurre algo parecido a pequeña escala: las pruebas que arrancan toda la aplicación necesitan Kafka y bases de datos, por eso en GitHub Actions solo se ejecutan las pruebas unitarias, que no necesitan nada.
2. **Contratos y código repetido entre servicios.** El archivo `investigador.proto` está copiado en investigadores y en publicaciones. Es una versión pequeña del problema de las librerías compartidas que se desincronizan. Si el proyecto creciera, habría que tener una sola copia del contrato.
3. **No separar por moda.** El artículo muestra que partir en servicios resolvió un problema real al principio y luego creó otros. La matriz de decisión de RICA lo reconoce: el monolito modular casi empata cuando el peso del objetivo del curso baja.

## Referencia

[1] A. Noonan, "Goodbye Microservices: From 100s of problem children to 1 superstar," *Twilio Segment Blog*, Jul. 10, 2018. [Online]. Available: https://www.twilio.com/en-us/blog/developers/best-practices/goodbye-microservices. [Accessed: Nov. 14, 2026].
