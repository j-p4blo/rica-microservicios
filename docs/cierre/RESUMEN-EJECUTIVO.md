# Resumen ejecutivo - RICA

Pedimos: aprobar que RICA siga con su arquitectura actual, dividida en servicios independientes, y dedicar la siguiente etapa a corregir tres riesgos antes de usarla con datos reales.

Por qué: RICA debe llevar el registro de investigadores y de sus publicaciones. Lo más importante es que no se pierda ningún dato ni quede un registro a medias cuando algo falla, y que consultar la ficha de un investigador sea rápido.

Qué proponemos: RICA está dividido en tres partes que trabajan por separado: una guarda a los investigadores, otra guarda sus publicaciones y la tercera solo sirve para consultar. Cuando algo cambia, la parte que lo hizo deja un aviso y las otras lo leen cuando pueden, en lugar de llamarse todo el tiempo. La parte de consulta mantiene la ficha de cada investigador ya armada, por eso responde de inmediato.

Qué ganamos / qué cuesta:
- Ganamos: si una parte se cae, las otras siguen trabajando; los cambios no se pierden aunque haya una caída; la consulta es rápida; y se puede probar cada parte sin encender las demás.
- Cuesta: más tiempo de construcción y de mantenimiento que un sistema de una sola pieza, sobre todo para una sola persona; y los cambios pueden tardar un momento en verse en la consulta.

Principales riesgos y cómo los controlamos:
1. Al guardar una publicación, el aviso se envía por separado y podría perderse si el sistema se cae justo en ese momento. Se corrige guardando la publicación y su aviso juntos, como ya se hace con los investigadores.
2. Al eliminar un investigador, el proceso no tiene tiempo límite y podría quedarse trabado. Se corrige agregando un tiempo máximo de espera y un estado de error que permita revisarlo.
3. El sistema todavía no tiene claves de acceso ni copias de respaldo, porque hoy corre solo en un computador de desarrollo. Aceptamos este riesgo por ahora y lo resolvemos antes de usarlo con personas o datos reales.

Siguiente paso: quien desarrolla RICA corrige los dos primeros riesgos y presenta el resultado; el tercero se atiende antes de cualquier uso real.
