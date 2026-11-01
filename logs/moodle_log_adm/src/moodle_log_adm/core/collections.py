from abc import ABC, abstractmethod
from collections import defaultdict
from pathlib import Path
from typing import Any, Callable, Iterable, Iterator, TextIO

import yaml
from moodle_log_adm.core.inference import TemplateBuilder
from pydantic import RootModel, TypeAdapter

################################################################################
# Tipos de utilidad
################################################################################

type TextIOLoader[T] = Callable[[TextIO], T]
"""Loader genérico para ficheros de texto.

Algunas funciones que cumplen esta interfaz son:
- json.load
- yaml.safe_load
- csv.DictReader
"""

type RecordCollection = Iterable[dict[str, Any]]
"""Colección de registros de logs planos."""

type TemplateTree = dict[str, dict[str, Iterable[str]]]
"""Árbol de componentes, eventos y templates."""

################################################################################
# Diccionario de logs
# ------------------------------------------------------------------------------
# Soporta las siguientes operaciones (mediante métodos mágicos):
# - Acceso: `log_dict[component, event]` <-- set[str]
# - Fusión: `log_dict0 += log_dict1 + log_dict2 + ... + log_dictn`
# - Recuento de logs: `len(log_dict)`
# - Conversión a cadena (YAML): `str(log_dict)`
################################################################################


class LogDict:
    """Estructura de árbol preparada para albergar logs únicos.

    Organiza la información de acuerdo al siguiente esquema jerárquico:
    ```
    Component:
        Event: [Log, Log, Log, ...]
    ```
    """

    def __init__(self, *dicts: LogDict) -> None:
        """Inicializa el diccionario interno admitiendo diccionarios iniciales opcionales."""
        self.data = defaultdict(lambda: defaultdict(set))

        for current in dicts:
            self += current

    def __getitem__(self, key: tuple[str, str]) -> set[str]:
        """Accede a los conjuntos con notación de corchetes usando pares (`[component, event]`) como clave de acceso."""
        component, event = key
        return self.data[component][event]

    def __iadd__(self, other: LogDict) -> LogDict:
        """Implementa la operación de suma in-place (`+=`), fusionando otro LogDict en este."""
        for component, events in other.data.items():
            for event, logs in events.items():
                self[component, event].update(logs)

        return self

    def __add__(self, other: LogDict) -> LogDict:
        """Implementa la operación de suma (`+`), fusionando dos LogDict en uno nuevo."""
        return LogDict(self, other)

    def __len__(self) -> int:
        """Retorna el número total de logs almacenados entre todos los componentes y eventos."""
        return sum(
            len(logs) for events in self.data.values() for logs in events.values()
        )

    def __str__(self) -> str:
        """Obtiene la representación de cadena con formato YAML."""

        # En primer lugar se aplana el diccionario de defaultdicts y sets inicial
        flat_dict = {
            component: {event: sorted(logs) for event, logs in events.items()}
            for component, events in self.data.items()
        }

        #
        # Configuración del YAML
        #
        # defailt_flow_style=False
        # Se evita el flow_style=True porque hace YAML más verboso, utilizando llaves y comas, asemejándose a JSON;
        # Estableciéndolo a False se crean listas con guiones, muy cómodo para usuarios y ficheros más ligeros.
        #
        # sort_keys=True
        # Se mantienen los componentes y eventos ordenados (no es estrictamente necesario, sí útil).
        #
        # allow_unicode=True
        # Se conserva la compatibilidad con Unicode para evitar imprevistos,
        # los logs pueden incluir cualquier carácter.
        #
        # width=float("inf")
        # Ancho de línea indefinido para que las líneas largas no incluyan saltos de línea.
        #
        return yaml.dump(
            flat_dict,
            default_flow_style=False,
            sort_keys=True,
            allow_unicode=True,
            width=float("inf"),
        )


################################################################################
# Fuente de logs abstracta
# ------------------------------------------------------------------------------
# Unifica lógica común como la construcción de dos diccionarios:
# - success_dict: Templates correctamente inferidos.
# - failure_dict: Logs que no se han podido inferir en absoluto.
#
# De esta manera se da la oportunidad al usuario de mejorar sus patrones de
# inferencia. Aún así, se podrían considerar templates correctos aquellos en los
# que se haya inferido al menos un campo, pero no todos.
#
# Las implementaciones deben proporcionar la iteración sobre los logs.
################################################################################


class LogSource[T](ABC):
    """Clase base abstracta para fuentes de logs.

    Flexibiliza la carga de diferentes formatos permitiendo construcciones como:
    ```
    csv_file  = LogSourceImpl("logs.csv", csv.DictReader, ...)
    json_file = LogSourceImpl("logs.json", json.load, ...)
    yaml_file = LogSourceImpl("logs.yaml", yaml.load, ...)
    ```
    """

    def __init__(
        self,
        path: str | Path,
        loader: TextIOLoader[T],
        encoding: str | None = None,
    ) -> None:
        """Inicializa la fuente con la ruta del fichero, el loader y la codificación."""
        self.path = Path(path)
        self.loader = loader
        self.encoding = encoding

    @abstractmethod
    def entries(self, data: T) -> Iterator[tuple[str, str, str]]:
        """Extrae y entrega iterativamente tuplas de `(component, event, description)`."""
        ...

    def extract_templates(
        self, template_builder: TemplateBuilder
    ) -> tuple[LogDict, LogDict]:
        """Abre el fichero de forma segura y procesa las entradas mediante el TemplateBuilder.

        Returns:
            Tupla de diccionarios `(success_dict, failure_dict)` con los resultados de la inferencia.
        """
        success_dict = LogDict()
        failure_dict = LogDict()

        with open(self.path, encoding=self.encoding) as file:
            for component, event, description in self.entries(self.loader(file)):
                template = template_builder.build(description)

                if template is not None:
                    success_dict[component, event].add(template)
                else:
                    failure_dict[component, event].add(description)

        return success_dict, failure_dict


################################################################################
# Fuente de logs crudos
# ------------------------------------------------------------------------------
# Este tipo de ficheros son los que se obtienen directamente desde Moodle con
# datos reales y se encuentran estructurados en campos (CSV/JSON).
#
# Para este tipo de fuentes se deben indicar los nombres de los campos.
################################################################################


class RecordFile(LogSource[RecordCollection]):
    """Representa un fichero de logs de origen."""

    def __init__(
        self,
        path: str | Path,
        loader: TextIOLoader[RecordCollection],
        fields: tuple[str, str, str],
        encoding: str | None = None,
    ) -> None:
        """Inicializa el fichero de registros guardando la configuración de los campos."""
        self.fields = fields
        super().__init__(path, loader, encoding)

    def entries(self, data: RecordCollection) -> Iterator[tuple[str, str, str]]:
        """Recorre la colección de registros planos y entrega las entradas válidas."""
        component_field, event_field, description_field = self.fields

        for record in data:
            component = str(record.get(component_field, "")).strip()
            event = str(record.get(event_field, "")).strip()
            description = str(record.get(description_field, "")).strip()

            if component and event and description:
                yield component, event, description


################################################################################
# Fuente de logs anonimizados
# ------------------------------------------------------------------------------
# Este tipo de fuentes nace de la existencia de esta recopilación del
# repositorio de UBUMonitor escritorio:
# https://github.com/yjx0003/UBUMonitor/blob/master/python/Componentes%20y%20eventos.json
#
# Tiene estructura de árbol y contiene una rica cantidad de templates
# anonimizados en los que, en lugar de datos reales, se utilizan placeholders;
# es por ello que en la configuración del InferenceEngine se pueden definir
# patrones adicionales en la clave `placeholders`:
# ```
# keywords:
#  int:
#    pattern: '-?\d+'
#    wrappers: [['"', '"'], ["'", "'"], ['', '']]
#    placeholders: ['INTEGER']
#
#  str:
#    pattern: '.+?'
#    wrappers: [['"', '"'], ["'", "'"]]
#    placeholders: ['STRING']
# ```
################################################################################


class TemplateFile(LogSource[TemplateTree]):
    """Representa un fichero que almacena templates estructurados en árbol."""

    def entries(self, data: TemplateTree) -> Iterator[tuple[str, str, str]]:
        """Recorre el árbol jerárquico de templates y entrega las entradas válidas."""
        # Validación de la estructura del árbol mediante Pydantic
        tree = TypeAdapter(RootModel[TemplateTree]).validate_python(data).root

        for component, events in tree.items():
            for event, descriptions in events.items():
                for description in descriptions:
                    if description:
                        yield component, event, description
