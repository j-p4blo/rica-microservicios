# Matriz de decisión de la arquitectura de RICA

## 1. Alternativas comparadas

- **A. Monolito modular.** Una sola aplicación con tres módulos (investigadores, publicaciones y consultas), una sola base de datos PostgreSQL y llamadas normales entre módulos.
- **B. Microservicios completos.** Un servicio por cada responsabilidad (incluso autenticación y notificaciones), cada uno con su base de datos, un orquestador central para los procesos largos, Kubernetes, seguridad completa y seguimiento de peticiones en todos los servicios.
- **C. Híbrida (la nuestra).** Tres servicios de negocio (investigadores, publicaciones y consultas) más el gateway y Eureka, con avisos por Kafka, CQRS para la lectura y saga para eliminar. Corre con Docker Compose.

## 2. Criterios y pesos

El peso va de 1 a 5.

| Criterio | Peso | Por qué ese peso |
|---|---|---|
| Cumplir los objetivos del curso | 5 | RICA es un proyecto del curso de Ingeniería de Software II. Su propósito es practicar microservicios, eventos, CQRS y arquitectura hexagonal, y se evalúa por eso. |
| Esfuerzo de construir y operar (una persona, un semestre) | 5 | Todo lo hace una sola persona con un plazo fijo. Una arquitectura que no se pueda terminar ni mantener no sirve. |
| Integridad y consistencia de los datos | 4 | Perder un aviso o dejar un investigador a medias es el error más grave para un sistema de registro. |
| Aislamiento de fallos | 3 | Es deseable que un servicio caído no detenga a los demás, pero con pocos usuarios un fallo general se tolera. |
| Rendimiento de consulta | 3 | La ficha y el ranking son lo que más se consulta, pero el volumen es pequeño. |
| Facilidad de probar y cambiar | 3 | Importa para avanzar rápido y sin romper cosas, pero se puede lograr en cualquiera de las tres. |

## 3. Puntajes

Cada celda muestra puntaje → puntaje por peso.

| Criterio | Peso | A: monolito modular | B: microservicios completos | C: híbrida (nuestra) |
|---|---|---|---|---|
| Cumplir los objetivos del curso | 5 | 2 → 10 | 5 → 25 | 5 → 25 |
| Esfuerzo de construir y operar | 5 | 5 → 25 | 1 → 5 | 3 → 15 |
| Integridad y consistencia de los datos | 4 | 5 → 20 | 2 → 8 | 3 → 12 |
| Aislamiento de fallos | 3 | 2 → 6 | 5 → 15 | 4 → 12 |
| Rendimiento de consulta | 3 | 4 → 12 | 3 → 9 | 5 → 15 |
| Facilidad de probar y cambiar | 3 | 4 → 12 | 3 → 9 | 4 → 12 |
| **TOTAL** | | **85** | **71** | **91** |

Por qué se puso cada puntaje:

- **Objetivos del curso.** El monolito sigue practicando arquitectura hexagonal y diseño por módulos, pero no microservicios, eventos ni CQRS (2). B y C practican todo (5).
- **Esfuerzo.** El monolito es lo más simple (5). B exige mucha infraestructura para una sola persona (1). C tiene 5 aplicaciones, Kafka y dos bases de datos, pero todo se levanta con Docker Compose (3).
- **Integridad de datos.** En el monolito una sola transacción lo cubre todo (5). En B hay más procesos repartidos sin transacción común (2). En C hay Outbox y saga, pero con huecos conocidos (3).
- **Aislamiento de fallos.** En el monolito un error tumba todo (2). B aísla todo (5). C aísla los servicios y protege la llamada más delicada con el Circuit Breaker (4).
- **Rendimiento de consulta.** El monolito resuelve con una base de datos y joins (4). B tiene que juntar datos de varios servicios en cada consulta (3). C tiene la ficha ya armada en una sola lectura (5).
- **Probar y cambiar.** Los puertos permiten probar la lógica con repositorios falsos en C y en A (4). En B hay muchas más piezas que probar juntas (3).

## 4. Análisis de sensibilidad

Se cambió el peso de los dos criterios más importantes (los dos tienen peso 5). Como la escala llega hasta 5, el +2 se prueba como ejercicio con peso 7.

| Cambio | A | B | C | Ganador |
|---|---|---|---|---|
| Base | 85 | 71 | 91 | C |
| Objetivos del curso: 5 → 3 | 81 | 61 | 81 | Empate A y C |
| Objetivos del curso: 5 → 7 | 89 | 81 | 101 | C |
| Esfuerzo: 5 → 3 | 75 | 69 | 85 | C |
| Esfuerzo: 5 → 7 | 95 | 73 | 97 | C |
| Objetivos del curso y esfuerzo, ambos 5 → 3 | 71 | 59 | 75 | C |
| Objetivos del curso: 5 → 1 | 77 | 51 | 71 | A |

La decisión es frágil con respecto al primer criterio. Si el objetivo del curso pesara 3 o menos, el monolito modular empata o gana. Con el segundo criterio, aunque se mueva ±2, C sigue ganando.

Para reducir esa incertidumbre se haría una medición: tomar tres cambios típicos (agregar un campo a la publicación, agregar un nuevo tipo de aviso y agregar un filtro a la consulta), anotar cuánto tiempo toma cada uno en la arquitectura actual y estimar cuánto tomaría en un monolito modular. Si la diferencia fuera muy grande, el esfuerzo pesaría más de lo que se supuso.

## 5. Conclusión

Elegimos C porque es la que mejor cumple el propósito del proyecto, que es practicar microservicios, eventos y CQRS, y porque además da la consulta más rápida (una sola lectura) y aísla bien los fallos, aceptando el costo de una operación más compleja para una sola persona, datos que pueden tardar un momento en actualizarse y varios riesgos abiertos (saga sin tiempo límite y envío de mensajes sin Outbox en publicaciones).

El resultado es parejo y no hay que esconderlo: para un sistema real de una sola persona y sin fines académicos, un monolito modular habría sido casi igual de bueno o mejor, y por eso en el análisis de sensibilidad empata cuando el peso del curso baja a 3. Si el objetivo de aprendizaje no existiera, se recomendaría empezar con el monolito modular y separar servicios solo cuando una parte lo pidiera.
