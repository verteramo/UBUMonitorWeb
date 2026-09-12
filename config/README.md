# Gestión de logs de Moodle
- Marcelo Verteramo Pérsico
- https://github.com/verteramo/ubumonitorweb

## Enfoques
### Enfoque de UBUMonitor Desktop
El enfoque actual recae sobre una extracción ciega de enteros en orden y la aplicación del patrón strategy, donde cada clase sabe cómo construir la entrada de log, dándole un significado semántico a cada uno de los enteros extraídos.

### Alternativas para UBUMonitorWeb
#### Enfoque Strategy
En este caso se utiliza un enfoque conceptualmente similar al de la versión desktop, pero aprovechando la potencia de Kotlin para crear mapas al vuelo, en lugar de creando clases, utilizando un mapa YAML que vincula cada evento con los campos que contiene, en el mismo orden en que serán extraídos, por ejemplo:
```yaml
Book:
  Chapter viewed: [ userId, chapterId, moduleId ]
  Course module viewed: [ userId, moduleId ]
Choice:
  Choice answer added: [ userId, targetUserId, moduleId ]
  Course module viewed: [ userId, moduleId ]
```

#### Enfoque Template matching
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
