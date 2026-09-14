# Gestión de logs de Moodle
- Marcelo Verteramo Pérsico
- https://github.com/verteramo/ubumonitorweb

## Enfoques
### Enfoque de UBUMonitor Desktop
El enfoque actual recae sobre una extracción ciega de enteros en orden y la aplicación del patrón strategy, donde cada clase sabe cómo construir la entrada de log, dándole un significado semántico a cada uno de los enteros extraídos; Por ejemplo:
```java
// Extracción "ciega" de IDs
Pattern INTEGER_PATTERN = Pattern.compile(/* regex */);
List<Integer> ids = INTEGER_PATTERN.matcher(log.description);

// Selección de la estrategia
ReferencesLog referencesLog = LogTypes.getReferenceLog(log.component, log.event);

// Método abstracto que, en cada implementación, mapea los datos que necesita ese tipo de registro
referencesLog.setLogReferencessAttributes(log, ids);
```


### Alternativas para UBUMonitorWeb
#### Enfoque "Strategy" (extracción posicional)
En este caso se utiliza un enfoque conceptualmente similar al de la versión desktop, pero aprovechando la potencia de YAML para representar las "estrategias" de forma declarativa, y aprovechando que el consumidor de los objetos resultantes acepta tipado dinámico (Typescript permite definir campos opcionales). En lugar de creando clases, se utiliza un mapa YAML que vincula cada evento con los campos que contiene, en el mismo orden en que serán extraídos, por ejemplo:
```yaml
Book:
  Chapter viewed: [ userId, chapterId, moduleId ]
  Course module viewed: [ userId, moduleId ]
Choice:
  Choice answer added: [ userId, targetUserId, moduleId ]
  Course module viewed: [ userId, moduleId ]
```
Luego la implementación se reduciría a cargar este mapa (`mappings`) en memoria y asignar los enteros extraídos a los nombres de campos definidos en el YAML:
```kotlin
fun compose(
  // Entrada de log
  entry: LogEntry,
  // Lista de IDs (extracción ciega también)
  // Ejemplo: [1, 75, 1129]
  values: List<Int>,
  ) {
  val events = mappings[entry.component] ?: emptyMap()

  // Ejemplo: [userId,chapterId,moduleId]
  val fields = events[entry.event] ?: emptyList()

  // El método zip los empareja
  entry.attributes.putAll(fields zip values)
}
```
- **Ventajas**:
  - Se usa actualmente y funciona bien.
  - La implementación es sencilla en Kotlin.
  - Es fácil mantener actualizado el YAML.
- **Desventajas**:
  - Frágil ante cambios silenciosos, si una actualización de Moodle añadiera un identificador extra en el texto de un evento, los valores se desplazarían y los datos se corromperían sin levantar excepciones, pero sin duda es un escenario poco probable que se solventaría con un ajuste en el YAML.

Ejemplo de resultado:
```json
[
  {
    "datetime": "2026-09-13T11:14:53+01:00",
    "component": "Forum",
    "event": "Discussion viewed",
    "origin": "web",
    "ipAddress": "85.137.98.152",
    "attributes": {
      "userId": 1,
      "discussionId": 241,
      "moduleId": 1169
    }
  },
  {
    "datetime": "2026-09-13T11:11:30+01:00",
    "component": "System",
    "event": "Course activity completion updated",
    "origin": "web",
    "ipAddress": "154.222.105.26",
    "attributes": {
      "userId": 1,
      "moduleId": 1172,
      "targetUserId": 1
    }
  },
  {
    "datetime": "2026-09-13T11:11:30+01:00",
    "component": "System",
    "event": "Course activity completion updated",
    "origin": "web",
    "ipAddress": "154.222.105.26",
    "attributes": {
      "userId": 1,
      "moduleId": 1172,
      "targetUserId": 1
    }
  }
]
```

#### Enfoque Template matching (similar a [Grok y su Oniguruma syntax](https://www.elastic.co/docs/reference/logstash/plugins/plugins-filters-grok#_regular_expressions))
Otro enfoque, más autocontenido y autodescriptivo, pero también más estricto, elimina por completo la dependencia del orden posicional, los datos ya no se extraen como una lista ordenada, sino como un mapa directo clave-valor; el patrón define simultáneamente el contrato de coincidencia y los metadatos:
```yaml
Overview report:
  Grade overview report viewed:
  - The user with id '(?<userId>-?\d+)' viewed the overview report in the gradebook.
Page:
  Course module viewed:
  - The user with id '(?<userId>-?\d+)' viewed the 'STRING' activity with course module id '(?<moduleId>-?\d+)'.
Quiz:
  Course module instance list viewed:
  - The user with id '(?<userId>-?\d+)' viewed the instance list for the module 'STRING' in the course with id '(?<courseId>-?\d+)'.
  Course module viewed:
  - The user with id '(?<userId>-?\d+)' viewed the 'STRING' activity with course module id '(?<moduleId>-?\d+)'.
```
Se incluye este ejemplo porque se desconoce el motivo por el que se utilizó `'STRING'`, pero una opción de saltársela sin capturarla (ya que cambiará con el nombre de la actividad particular), es haciendo una captura perezosa:
```regexp
  - The user with id '(?<userId>-?\d+)' viewed the '.*?' activity with course module id '(?<moduleId>-?\d+)'.
```
- **Ventajas**:
  - Contratos exactos a ojos del desarrollador.
  - Posibilidad de varios contratos por evento, algo que según [Componentes y eventos.json](https://github.com/yjx0003/UBUMonitor/blob/c73e5576fc49531c2b1f2149d61425c4bb2a930c/python/Componentes%20y%20eventos.json#L38) es posible.
- **Desventajas**:
  - **Necesidad de compilar todos los contratos durante el levantamiento de la aplicación, ya que son expresiones regulares**.
  - Si algún log no se ajusta al contrato, resulta en un objeto log sin atributos; mantenimiento de YAML más complejo.

Ejemplo de resultado (se puede observar la fragilidad de errores silenciosos en los contratos):
```json
[
  {
    "datetime": "2026-09-13T11:04:49+01:00",
    "component": "Forum",
    "event": "Discussion viewed",
    "origin": "web",
    "ipAddress": "66.227.164.85",
    "attributes": {
      "discussionId": 241,
      "moduleId": 1169,
      "userId": 1
    }
  },
  {
    "datetime": "2026-09-13T11:01:33+01:00",
    "component": "System",
    "event": "Section viewed",
    "origin": "web",
    "ipAddress": "216.244.66.246",
    "attributes": {}
  },
  {
    "datetime": "2026-09-13T11:01:27+01:00",
    "component": "System",
    "event": "Section viewed",
    "origin": "web",
    "ipAddress": "216.244.66.246",
    "attributes": {}
  }
]
```

### Conclusiones
Strategy proporciona mejores resultados, su implementación es más sencilla, el YAML de configuración es más pequeño y sencillo de mantener.
- En el directorio `src` paralelo a este fichero se encuentran los notebooks para convertir [Componentes y eventos.json](https://github.com/yjx0003/UBUMonitor/blob/c73e5576fc49531c2b1f2149d61425c4bb2a930c/python/Componentes%20y%20eventos.json#L38) en los YAML de configuración.
- En la [rama `main` de UBUMonitorWeb](https://github.com/verteramo/UBUMonitorWeb/tree/main) se encuentra la implementación del enfoque Strategy; en la rama [`alt-#24`](https://github.com/verteramo/UBUMonitorWeb/tree/alt-%2324) la implementación del enfoque Template matching.

### Actualidad
Actualmente, se está implementando la primera propuesta con una adopción de la segunda, la posibilidad de varios tipos de captura:
```yaml
  Grading table viewed:
    - [userId, moduleId]
  Submission confirmation form viewed.:
    - [userId, moduleId]
  Submission form viewed.:
    - [userId, moduleId]
    - [userId, targetUserId, moduleId]
```
De esta manera se puede escoger la lista que coincida en longitud con el número de valores extraídos:
```kotlin
fun compose(
  entry: LogEntry,
  values: List<Int>, ) {
  // Se selecciona la lista de campos que coincide
  // en longitud con el número real de enteros extraídos
  val fields =
    mappings[entry.component]
      ?.get(entry.event)
      ?.find { it.size == values.size } ?: emptyList()
  entry.attributes.putAll(fields zip values)
}
```
