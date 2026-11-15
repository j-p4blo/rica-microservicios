# Guion de la defensa de RICA (10 minutos)

## 1. El problema y los atributos de calidad (1 min)

Buenos días. Les voy a presentar RICA, un sistema para llevar el registro de investigadores y de sus publicaciones. Permite registrar investigadores, registrar sus publicaciones, ver la ficha de cada uno y consultar un ranking.

Definí cinco cosas que me importan, cada una con una medida. Primero, que los datos no se pierdan aunque algo se caiga. Segundo, que un servicio caído no tumbe a los demás: si falla, se responde un error claro en menos de seis segundos. Tercero, que consultar sea rápido: una sola lectura y ninguna llamada a otros servicios. Cuarto, que sea fácil de probar, con pruebas que corren en cada push sin necesitar infraestructura. Y quinto, que se pueda seguir qué pasó con una petición usando un identificador.

## 2. La arquitectura: diagrama C4 y tabla de estilos (2 min)

En el diagrama, el cliente entra por el gateway, que reparte las peticiones a tres servicios: investigadores, publicaciones y consultas. Eureka permite que se encuentren por nombre. Cada servicio tiene su propia base de datos: investigadores usa PostgreSQL, publicaciones usa MongoDB y consultas usa otra base de MongoDB.

Investigadores y publicaciones tienen arquitectura hexagonal: la lógica no depende de Kafka, ni de gRPC ni de la base de datos, y por eso se puede probar con piezas falsas.

Hay dos formas de comunicarse. La directa: publicaciones le pregunta a investigadores por gRPC si el investigador existe antes de guardar una publicación, porque necesita la respuesta en ese momento. Y la indirecta, por Kafka: cuando algo cambia, el servicio deja un aviso y los demás lo leen cuando pueden.

En investigadores usé el patrón Outbox: el investigador y su aviso se guardan juntos, así que el aviso no se pierde aunque el servicio se caiga. Consultas solo lee esos avisos y mantiene una ficha ya armada de cada investigador. Y cuando se elimina un investigador, como eso afecta a dos bases de datos distintas, se hace con una Saga: se avisa por pasos y cada servicio hace su parte.

## 3. Dos decisiones clave y por qué (2 min)

La primera decisión es CQRS. La ficha de un investigador mezcla datos de dos servicios, y armarla en cada consulta obligaba a llamar a ambos. Decidí crear un servicio que solo consulta y que guarda la ficha ya lista. Descarté que un servicio junte los datos en cada consulta, porque dependería de que los dos estén funcionando. Lo bueno es que consultar es una sola lectura. El costo es que la ficha puede tardar un momento en mostrar un cambio, y que hay datos repetidos.

La segunda decisión es verificar al investigador por gRPC con un Circuit Breaker. Publicaciones necesita saber en ese momento si el investigador existe. Descarté guardar una copia de los investigadores dentro de publicaciones, porque habría datos que pueden estar desactualizados y otra cosa más que mantener. Para que una caída no se contagie, la llamada espera como máximo cinco segundos, y si fallan la mitad de las últimas diez llamadas, el circuito se abre diez segundos y responde error de inmediato. El costo es que, si investigadores está caído, no se pueden registrar publicaciones. Los dos costos están escritos en el documento de arquitectura.

## 4. Qué evaluamos: riesgos principales y matriz de decisión (2 min)

Para evaluar hice dos cosas. Primero, un análisis por escenarios. El más difícil es la eliminación de un investigador, y encontré tres riesgos principales.

Uno: publicaciones guarda y después avisa por separado, así que si se cae en el medio, el aviso se pierde. Lo corregiría usando Outbox también ahí.

Dos: la saga no tiene tiempo límite, así que un investigador podría quedarse en eliminación para siempre. Agregaría un estado de error y un tiempo máximo de espera.

Tres: no hay seguridad ni copias de respaldo. Lo acepto porque RICA corre en un computador sin datos reales, pero lo resolvería antes de cualquier uso real.

Segundo, una matriz de decisión que compara tres alternativas: un monolito modular, microservicios completos y la híbrida que construí. La híbrida suma 91 puntos, el monolito 85 y los microservicios completos 71. Pero quiero ser honesto: el resultado es parejo. Si el peso del objetivo del curso baja de cinco a tres, el monolito empata. Eso me dice que, para un sistema real de una sola persona, un monolito modular sería casi igual de bueno.

## 5. Cómo la mantendríamos viva: gobernanza y deuda (1 min)

Para mantenerla viva definí cuatro reglas que se pueden verificar automáticamente. Una ya funciona: las pruebas de los cinco servicios se ejecutan en GitHub Actions en cada push. Las otras tres están definidas pero por implementar: que el dominio no dependa de la infraestructura, que cada servicio use solo su propia base de datos y que los mensajes lleven el identificador de petición. También dejé una lista de seis deudas técnicas, cada una con su riesgo y con el momento en que se revisaría.

## 6. Lo que aprendimos y lo que cambiaríamos (1 min)

Aprendí tres cosas. Que cada patrón resuelve un problema concreto y trae un costo: ninguno es gratis. Que una arquitectura se evalúa con escenarios medibles y no con gustos. Y que escribir las decisiones y los riesgos hace visible lo que todavía falta.

Si empezara de nuevo, usaría Outbox en todos los servicios desde el principio, le pondría tiempo límite a la saga y llevaría el identificador de petición por todo el sistema.

## 7. Resumen ejecutivo: lo pedimos en una frase (1 min)

Para cerrar, lo que pido en una frase: aprobar que RICA siga con su arquitectura actual y dedicar la siguiente etapa a corregir tres riesgos antes de usarla con datos reales.

Ganamos que un servicio caído no detenga a los demás, que los cambios no se pierdan y que la consulta sea rápida. Cuesta más trabajo de construcción, y que los cambios tardan un momento en verse. Gracias, quedo atento a sus preguntas.

---

# Respuestas a las tres preguntas

## ¿Por qué no un monolito?

Es una pregunta justa y la respondí con una matriz. Comparé tres alternativas y la mía sumó 91 puntos contra 85 del monolito modular, así que el resultado es parejo. Si el objetivo de practicar microservicios, eventos y CQRS pesara menos, el monolito empata o gana, y para un sistema real de una sola persona lo recomendaría.

Elegí la híbrida por dos razones. La primera es que RICA es un proyecto del curso y su propósito es practicar estos estilos. La segunda es que da dos ventajas concretas: la consulta se resuelve con una sola lectura y un servicio caído no tumba a los demás. A cambio pago una operación más compleja, datos que tardan un momento en actualizarse y los riesgos que ya mencioné. Además, como la lógica es hexagonal, no depende de Kafka ni de gRPC, lo que ayuda si algún día se quisiera simplificar.

## ¿Qué falla primero si el tráfico se multiplica por diez?

Con el volumen actual, que es muy pequeño, no pasaría nada grave. Pero lo primero que se estrecharía es el Outbox de investigadores: revisa la tabla cada 2 segundos y envía hasta 50 avisos por pasada, o sea, como máximo unos 25 avisos por segundo. Si llegaran más, se acumularían en la tabla y los cambios tardarían más en llegar a consultas. No se perderían, solo se retrasarían.

El segundo punto estrecho es consultas: lee de un tópico de Kafka con una sola partición y un solo consumidor, y procesa un mensaje a la vez, así que también se atrasaría. Lo que haría es subir el tamaño del lote o bajar el intervalo del Outbox, y aumentar las particiones de Kafka. La llamada gRPC entre publicaciones e investigadores no preocupa tanto, porque tiene límite de tiempo y circuito.

## ¿Qué decisión revertirían?

No revertiría una decisión grande como CQRS o la saga. Lo que cambiaría es haber dejado a publicaciones enviar los mensajes directamente a Kafka, sin Outbox. En investigadores sí usé Outbox y debí aplicar la misma regla a todos los servicios que avisan cambios.

Hoy, si publicaciones se cae justo entre guardar la publicación y enviar el aviso, el aviso se pierde y consultas queda desactualizado. Es el primero de mis tres riesgos, y se corrige con el mismo patrón que ya existe en el proyecto, sin diseñar nada nuevo.
