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
En este caso se utiliza un enfoque conceptualmente similar al de la versión desktop, pero aprovechando la potencia de YAML para representar las "estrategias" de forma declarativa, en lugar de creando clases, se utiliza un mapa YAML que vincula cada evento con los campos que contiene, en el mismo orden en que serán extraídos, por ejemplo:
```yaml
Book:
  Chapter viewed: [ userId, chapterId, moduleId ]
  Course module viewed: [ userId, moduleId ]
Choice:
  Choice answer added: [ userId, targetUserId, moduleId ]
  Course module viewed: [ userId, moduleId ]
```
- **Ventajas**:
  - Enfoque UBUMonitor desktop funcional.
  - Implementación sencilla.
  - Fácil mantenimiento sel YAML.
- **Desventajas**:
  - Muy frágil, la presencia de un identificador extra desplazaría los datos  y se corromperían sin levantar excepciones.
  - Solo permite capturar enteros.

#### Enfoque Template matching (similar a [Grok](https://www.elastic.co/docs/reference/logstash/plugins/plugins-filters-grok#_regular_expressions))

Otro enfoque, más autocontenido y autodescriptivo, elimina por completo la dependencia del orden posicional, los datos ya no se extraen como una lista ordenada, sino como un mapa directo clave-valor; el patrón define simultáneamente el contrato de coincidencia y los metadatos:
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

- **Ventajas**:
  - Contratos exactos a ojos del desarrollador y de usuarios no tan avanzados.
  - Posibilidad de varios contratos por evento.
- **Desventajas**:
  - Aún no se han objetivado.

### Conclusiones
Template matching proporciona mejores resultados, su implementación es sencilla, el YAML de configuración es sencillo de mantener, incluso para usuarios no desarrolladores, se utiliza en Logstash, el gestor de logs de ElasticSearch.

Se proporciona el notebook `logs.ipynb` para realizar el mantenimiento del YAML.
