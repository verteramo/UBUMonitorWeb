import csv
import json
import logging
import re
from collections import defaultdict
from enum import Enum
from typing import Match, Pattern
from urllib import request

logging.basicConfig(level=logging.DEBUG)

DATABASE_URL = "https://raw.githubusercontent.com/yjx0003/UBUMonitor/refs/heads/master/python/Componentes%20y%20eventos.json"

type TemplatesDict = dict[str, dict[str, list[str]]]
"""
Diccionario de templates, soporta estructuras como la siguiente:
```
Component:
  Event:
    - Template
    - Template
    - Template
    ...
```
"""


type AliasesDict = dict[str, list[str]]
"""
Diccionario de alias para definir nombres de campos repetidos, por ejemplo:
```
"pageId": ["prevPageId", "nextPageId"]
```
"""


class FieldType(Enum):
    """
    Tipos de datos disponibles para las capturas.
    - INT: Permite extraer números enteros, entrecomillados o no.
    - STR: Permite extraer cadenas estrictamente entrecomilladas (se debe a que pueden estar vacías).
    - ANY: Permite extraer cualquier dato sin entrecomillar (cuando se sabe con certeza que está ahí).

    Se utilizan en los patterns de la siguiente manera:
    ```
    Regex: grade item with id {gradeItemId:int} of type {moduleType:str} and name {moduleName:str} in the course
    Match: grade item with id '13' of type 'mod' and name '...' in the course
    ```

    Se pueden utilizar sin nombre de campo, entonces se requerirá su existencia, pero no se realizará su captura:
    ```
    Regex: has {any} (a|the) {pageType:any} page
    Match: has created a Matching page
    Match: has moved a Content page

    Regex: has {any} the {questionType:any} question
    Match: has answered the Matching question
    Match: has viewed the Multichoice question
    ```
    """

    INT = ("int", r"-?\d+", "[\"']?")
    STR = ("str", r".+?", "[\"']")
    ANY = ("any", r".+?", "")

    def __init__(self, keyword: str, regex: str, quote: str):
        self.keyword = keyword
        self.regex = regex
        self.quote = quote

    @classmethod
    def from_keyword(cls, keyword: str):
        """
        Busca y devuelve el Enum correspondiente a partir de su keyword.
        """
        for field in cls:
            if field.keyword == keyword:
                return field
        # Fallback o manejo de errores
        raise ValueError(f"Tipo de campo desconocido: {keyword}")


type Metadata = dict[str, tuple[str, FieldType]]


class AttributeInjector:

    def __init__(self, patterns: list[str], placeholders: dict[FieldType, str] = None):
        """
        Convierte patrones del tipo:
        ```
        user with (?:the )?id {userId:int}
        user with (?:the )?id (?P<userId0>["\']?(?:-?\\d+)["\']?)
        ```

        Si se construye con placeholders:
        ```
        injector = AttributeInjector(placeholders={FieldType.INT: "INTEGER"})
        ```

        La conversión sería la siguiente:
        ```
        user with (?:the )?id {userId:int}
        user with (?:the )?id (?P<userId0>["\']?(?:INTEGER|-?\\d+)["\']?)
        ```
        """

        self.__rules: list[tuple[Pattern, Metadata]] = []

        # Se ordenan los patrones de mayor a menor longitud para
        # garantizar que primero se testean los más complejos y detallados
        patterns = sorted(patterns, key=len, reverse=True)

        for pattern in patterns:
            metadata: Metadata = {}
            occurrences = defaultdict(int)

            def get_group(match: Match[str]) -> str:
                # Coincidencias del re.sub de debajo de esta función
                field_name, field_type_keyword = match.groups()

                # Si no se indica tipo, se asume str
                field_type = FieldType.from_keyword(field_type_keyword or "str")

                # Expresión regular del grupo
                group_regex = (
                    field_type.regex
                    if not placeholders or field_type not in placeholders
                    else f"{placeholders[field_type]}|{field_type.regex}"
                )

                q = field_type.quote

                # Grupo anónimo: {int|str|any}, sirven para
                # indicar que hay datos sin realizar una captura
                if not field_name:
                    return f"(?:{q}(?:{group_regex})?{q})"

                # Recuento de ocurrencias para no repetir nombres de grupos
                occurrence = occurrences[field_name]
                occurrences[field_name] += 1

                # Nombre del grupo con su ocurrencia: userId0, userId1, ...
                group_name = f"{field_name}{occurrence}"

                # Metadatos del grupo para la construcción del template final en process_description
                metadata[group_name] = (field_name, field_type)

                return (
                    # Grupo final que capturará la presencia de campos en el log crudo
                    f"(?P<{group_name}>{q}(?:{group_regex})?{q})"
                )

            # Se guardan todas las reglas en memoria
            self.__rules.append(
                (
                    # El patrón de las etiquetas es: {tagName:type}
                    re.compile(
                        re.sub(r"\{(?:(\w+):)?(int|str|any)?\}", get_group, pattern)
                    ),
                    metadata,
                )
            )

        # print(*self.__rules, sep="\n")
        logging.debug(self.__rules)

    def build_template(self, text: str, aliases: AliasesDict = None) -> str:
        """
        Sustituye los valores o placeholders por sus grupos de captura correspondientes.
        Por ejemplo, con esta regla en memoria:
        ```
        user with (?:the )?id (?P<userId0>["\']?(?:INTEGER|-?\\d+)["\']?)
        ```

        Y, ante estos textos:
        ```
        The user with id '13'...
        The user with id 'INTEGER'...
        ```

        Captura tanto el valor como el placeholder "INTEGER"
        y en su lugar inyecta un grupo con el nombre del campo,
        ambos textos de ejemplo derivarían en:
        ```
        The user with id '(?<userId>-?\\d*)'...
        ```
        """

        # Limpieza de blancos en el texto (\s, \t, \n, \r)
        text = re.sub(r"\s+", " ", text).strip()

        tokens = {}
        occurrences = defaultdict(int)
        aliases = aliases or {}

        for rule, metadata in self.__rules:

            def inject(match: Match[str]) -> str:
                # Porción de texto coincidente con la regla
                # Por ejemplo: "user with id '13' deleted the file 'report.pdf'"
                text = match.group(0)

                # Grupos capturados con sus índices inicial y final,
                # ordenados de derecha a izquierda por índice inicial
                # para no pisarlos durante las sustituciones.
                # Por ejemplo:
                # [
                #   ("fileName0", (37, 49))
                #   ("userId0", (17, 19))
                # ]
                groups = sorted(
                    [
                        (group_name, value, match.span(group_name))
                        for group_name, value in match.groupdict().items()
                        if value is not None
                    ],
                    key=lambda x: x[2][0],
                    reverse=True,
                )

                for group_name, value, (start, end) in groups:
                    # Extracción de los metadatos del grupo
                    field_name, field_type = metadata[group_name]

                    # Recuento de ocurrencias para utilizar los alias
                    # o, en su ausencia, utilizar el nombre del campo
                    # concatenado con su número de ocurrencia
                    occurrence = occurrences[field_name]
                    occurrences[field_name] += 1
                    alias_list = aliases.get(field_name, [])
                    alias_index = occurrence - 1
                    final_field_name = (
                        field_name
                        if occurrence == 0
                        else (
                            alias_list[alias_index]
                            if alias_index < len(alias_list)
                            else f"{field_name}{occurrence}"
                        )
                    )

                    # Comillas
                    # Utilizando las que vienen en el texto original
                    # podrían pasar incluso logs malformados como: "13', 13', ...
                    qs = value[0] if value and value[0] in "\"'" else ""
                    qe = value[-1] if value and value[-1] in "\"'" else ""

                    # Token único como marcador de sustitución posterior ya que
                    # primero hay que escapar la cadena y luego inyectar la regex
                    token = f"__TOKEN{len(tokens)}__"
                    tokens[token] = (
                        f"{qs}(?<{final_field_name}>{field_type.regex})?{qe}"
                    )

                    # Slicing exacto usando índices relativos al match
                    offset = match.start(0)
                    rs = start - offset
                    re = end - offset
                    text = text[:rs] + token + text[re:]

                return text

            # Se pasa cada regla por el text para inyectar campos
            text = rule.sub(inject, text)

        # Escapado selectivo
        text = re.sub(r"([*+?^${}()|\[\]\\])", r"\\\1", text)

        # Restauración de las regex en el texto ya escapado
        for token, group in tokens.items():
            text = text.replace(token, group)

        return text


def load_database(patterns: list[str], aliases: AliasesDict = None) -> TemplatesDict:
    """
    Carga en memoria el fichero `Componentes y eventos.json` de UBUMonitor desktop,
    considerado como "la base de datos" por la cantidad de plantillas anonimizadas
    disponibles, se recorre y se le inyectan grupos de captura,
    retornando un diccionario de templates.
    """

    # Se indica el tipo de dato al que corresponde cada placeholder
    injector = AttributeInjector(
        patterns, placeholders={FieldType.INT: "INTEGER", FieldType.STR: "STRING"}
    )

    # Se construye el diccionario de templates recursivamente
    def replacer(data):
        if isinstance(data, dict):
            return {k: replacer(v) for k, v in data.items()}
        elif isinstance(data, list):
            return [replacer(item) for item in data]
        elif isinstance(data, str):
            # Paso final donde se construye el template
            return injector.build_template(data, aliases)
        return data

    with request.urlopen(DATABASE_URL) as response:
        data = response.read().decode("utf-8")

    return replacer(json.loads(data))


def load_logs_csv(
    path: str, patterns: list[str], aliases: AliasesDict
) -> TemplatesDict:
    """
    Carga en memoria el contenido de un fichero de logs en formato CSV,
    se recorre y se construye el diccionario de templates final.
    """

    # En este caso no requiere placeholders por son entradas con datos crudos
    injector = AttributeInjector(patterns)
    templates = defaultdict(lambda: defaultdict(set))

    with open(path, mode="r", encoding="utf-8") as f:
        reader = csv.DictReader(f)

        for line in reader:
            component = line.get("Component")
            event_name = line.get("Event name")
            description = line.get("Description")

            # Los eventos contienen inicialmente un conjunto
            # de templates para garantizar templates únicos
            templates[component][event_name].add(
                injector.build_template(description, aliases)
            )

    # Finalmente esos conjuntos son convertidos a listas
    # para facilizar la serialización del diccionario
    return {
        component: {
            event: sorted(templates_set) for event, templates_set in events.items()
        }
        for component, events in templates.items()
    }


def merge_templates(*templates_dicts: TemplatesDict) -> TemplatesDict:
    """
    Recibe un número variable de diccionarios de templates y los
    fusiona en uno solo con templates únicos.

    Esta función es útil para fusionar los templates de "la base de datos"
    con los que se extraigan de otros ficheros de logs crudos.
    """
    merged_templates = defaultdict(lambda: defaultdict(set))

    # Se recorren los diccionarios y se forma
    # uno solo con el contenido de todos los demás
    for current in templates_dicts:
        for component, events in current.items():
            for event, templates in events.items():
                # Se añaden directamente todos los elementos de la lista al conjunto
                merged_templates[component][event].update(templates)

    # Finalmente esos conjuntos son convertidos a listas
    # para facilizar la serialización del diccionario
    return {
        component: {
            event: sorted(templates_set) for event, templates_set in events.items()
        }
        for component, events in merged_templates.items()
    }
