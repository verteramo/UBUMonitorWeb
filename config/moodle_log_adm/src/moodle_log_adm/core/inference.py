import logging
import re
from collections import defaultdict
from functools import cached_property
from pathlib import Path
from typing import Annotated, Any, Callable, Generator, Iterable, NamedTuple, Sized

import yaml
from pydantic import AfterValidator, BaseModel, BeforeValidator, RootModel

################################################################################
# Clases de utilidad
################################################################################


class Plumber:
    """Clase interna de utilidad para la normalización de cadenas.

    Attributes:
        rules: Diccionario de patrones regex y sus respectivos reemplazos.
        compiled_rules: Caché interna de tuplas que contiene los patrones compilados.
    """

    rules = {
        r"\s+": " ",
    }

    compiled_rules = [
        (re.compile(rule), replacement) for rule, replacement in rules.items()
    ]

    @staticmethod
    def prepare(string: str) -> str:
        """Limpia los extremos de la cadena y aplica las reglas de normalización."""
        string = string.strip()

        for pattern, replacement in Plumber.compiled_rules:
            string = pattern.sub(replacement, string)

        return string


################################################################################
# Tipos de utilidad para Pydantic
################################################################################


class SortValidator[T](NamedTuple):
    """Validador para ordenar iterables en modelos Pydantic.

    Attributes:
        key: Función de extracción de valor usado para la ordenación.
        reverse: Si es `True`, ordena de forma descendente. Por defecto es `False`.
    """

    key: Callable[[T], int]
    reverse: bool = False

    def __call__(self, iterable: Iterable[T]) -> Iterable[T]:
        """Ejecuta la ordenación sobre el iterable proporcionado."""
        return sorted(iterable, key=self.key, reverse=self.reverse)


type ReverseSortedIterable[T: Sized] = Annotated[
    Iterable[T], AfterValidator(SortValidator[T](key=len, reverse=True))
]
"""Colección ordenada de forma descendente por longitud."""

type SubstitutionDict = dict[Annotated[re.Pattern, BeforeValidator(re.compile)], str]
"""Diccionario que asocia patrones regex compilados con cadenas de reemplazo."""

################################################################################
# Abstracción del problema
################################################################################

type AliasDict = dict[str, list[str]]
"""Diccionario que asocia nombres de grupos de captura con sus alias."""


class Wrapper(NamedTuple):
    """Delimitadores (prefijo y sufijo) que envuelven valores.

    Attributes:
        prefix: Carácter o cadena que abre el bloque (`'`, `"`, `<b>`, ...).
        suffix: Carácter o cadena que cierra el bloque (`'`, `"`, `</b>`, ...).
    """

    prefix: str
    suffix: str

    def __len__(self) -> int:
        """Número de caracteres totales del envoltorio."""
        return len(self.prefix + self.suffix)

    def wrap(self, value: str) -> str:
        """Envuelve un valor con el prefijo y sufijo escapados de forma segura."""
        return re.escape(self.prefix) + value + re.escape(self.suffix)

    def wraps(self, value: str) -> bool:
        """Determina si un valor está envuelto por este envoltorio."""
        return value.startswith(self.prefix) and value.endswith(self.suffix)


class Keyword(BaseModel):
    """Definición de un tipo de dato inyectable en las reglas de inferencia.

    Attributes:
        pattern: Patrón regex puro que define esta keyword (ej. `-?\\d+`).
        wrappers: Lista de envoltorios permitidos, ordenados de forma descendente por longitud.
        placeholders: Patrones regex alternativos al patrón principal.
    """

    pattern: str
    wrappers: ReverseSortedIterable[Wrapper]
    placeholders: ReverseSortedIterable[str] = []

    @cached_property
    def patterns(self) -> str:
        """Unión del patrón principal y los patrones alternativos."""
        return "|".join([self.pattern, *self.placeholders])

    @property
    def inference_pattern(self) -> str:
        """Genera el patrón regex de inferencia para detectar este tipo de dato.

        Aplica opcionalidad `?` al contenido si el envoltorio tiene longitud mayor a cero.
        """
        return "|".join(
            (
                wrapper.wrap(f"(?:{self.patterns})?")
                if len(wrapper)
                else f"(?:{self.patterns})"
            )
            for wrapper in self.wrappers
        )


class KeywordDict(RootModel[dict[str, Keyword]]):
    """Contenedor para el diccionario global de keywords."""

    @cached_property
    def pattern(self) -> str:
        """Unión escapada de todos los nombres de keywords disponibles."""
        return "|".join(re.escape(key) for key in self.root.keys())


################################################################################
# Proceso de construcción de patrones de inferencia
# ------------------------------------------------------------------------------
# Es el paso en el que se traducen las reglas de inferencia desde las pseudo
# expresiones regulares del usuario a expresiones regulares de Python:
#
# Regla de inferencia en pseudo regex (usuario):
# - `user with (?:the )?id {userId:int}`
# - `course with (?:the )?id {courseId:int}`
#
# Sustitución por su regex Python correspondiente, de acuerdo con los metadatos
# de la keyword:
# - `user with (?:the )?id (?P<userId0>"(?:-?\d+|INTEGER)?"|'(?:-?\d+|INTEGER)?'|(?:-?\d+|INTEGER))`
# - `course with (?:the )?id (?P<courseId0>"(?:-?\d+|INTEGER)?"|'(?:-?\d+|INTEGER)?'|(?:-?\d+|INTEGER))`
#
# Nótese que, en presencia de wrappers, el valor interno es opcional:
# - `"(?:-?\d+|INTEGER)?"`, `'(?:-?\d+|INTEGER)?'`
# En ausencia de wrappers, el valor es requerido:
# - `(?:-?\d+|INTEGER)`
# Este comportamiento es determinante para evitar coincidencias de longitud
# cero, siempre debe haber o wrappers vacíos o un valor; ya que el usuario que
# define una regla de inferencia espera, intuitivamente, que en el lugar de un
# tag haya algo.
################################################################################


class InferenceGroup(NamedTuple):
    """Metadatos generados transitoriamente para recordar el origen de un campo.

    Attributes:
        name: Nombre original del campo (ej. 'userId').
        occurrence: Índice de aparición para resolver colisiones en la regex.
        pattern: Patrón base puro del tipo de dato (sin comodines).
        wrappers: Envoltorios permitidos para identificar el wrapper real más tarde.
    """

    name: str
    occurrence: int
    pattern: str
    wrappers: list[Wrapper]

    @property
    def identifier(self) -> str:
        """Identificador único del grupo en el patrón (ej. 'userId0')."""
        return f"{self.name}{self.occurrence}"


class InferenceRule:
    """Traduce tags (ej. '{nombreGrupo:keyword}') a patrones regex y mapea sus grupos de captura.

    Attributes:
        pattern: Expresión regular precompilada lista para buscar coincidencias.
        inference_groups: Diccionario que contiene metadatos de los grupos de inferencia.
    """

    pattern: re.Pattern
    inference_groups: dict[str, InferenceGroup]

    def __init__(self, string: str, keywords: KeywordDict) -> None:
        """Traduce la regla de usuario en un patrón regex válido.

        Args:
            string: Regla original (ej. `user {userId:int}`).
            keywords: Diccionario global de keywords.
        """
        self.inference_groups = {}
        occurrences = defaultdict(int)

        def replace_tag(match: re.Match[str]) -> str:
            """Clausura que resuelve cada tag capturando el contexto local."""
            group_name, group_keyword_name = match.groups()
            group_keyword = keywords.root[group_keyword_name]

            if not group_name:
                return f"(?:{group_keyword.inference_pattern})"

            group_occurrence = occurrences[group_name]
            occurrences[group_name] += 1

            inference_group = InferenceGroup(
                group_name,
                group_occurrence,
                group_keyword.pattern,
                group_keyword.wrappers,
            )

            self.inference_groups[inference_group.identifier] = inference_group

            return (
                f"(?P<{inference_group.identifier}>{group_keyword.inference_pattern})"
            )

        tag = rf"\{{(?:(\w+):)?({keywords.pattern})\}}"
        self.pattern = re.compile(re.sub(tag, replace_tag, string))
        logging.debug(self.pattern)


################################################################################
# Proceso de inferencia de campos
# ------------------------------------------------------------------------------
# Es el paso en el que se detecta la presencia de los campos, se inyectan
# tókenes en su lugar y, posteriormente, se sustituyen por los grupos de captura
# finales.
#
# Ejemplo de línea de log a procesar:
# - `The user with id '13' viewed the log report for the course with id '72'.`
#
# Reglas de inferencia compiladas en el paso anterior:
# - `user with (?:the )?id (?P<userId0>"(?:-?\d+|INTEGER)?"|'(?:-?\d+|INTEGER)?'|(?:-?\d+|INTEGER))`
# - `course with (?:the )?id (?P<courseId0>"(?:-?\d+|INTEGER)?"|'(?:-?\d+|INTEGER)?'|(?:-?\d+|INTEGER))`
#
# Metadatos de las capturas tras aplicar las reglas de inferencia:
# - (group="userId0", value="'13'", start=17-4, end=21-4)
# - (group="courseId0", value="'72'", start=67-52, end=71-52)
#
# Como los grupos se procesan de derecha a izquierda (reverse=True) para
# proteger los offsets de las posiciones al recortar, los tokens se inyectan en
# orden inverso (el de más a la derecha recibe el índice 0):
# - `The user with id __TOKEN1__ viewed the log report for the course with id __TOKEN0__.`
#
# Finalmente, tras reemplazar todos los tókenes por sus grupos de captura:
# - `The user with id '(?<userId>-?\d+)?' viewed the log report for the course with id '(?<courseId>-?\d+)?'.`
################################################################################


class Formatter(BaseModel):
    """Gestiona la sintaxis final de los grupos de captura y la sanitización.

    Attributes:
        syntax: Plantilla base para estructurar los grupos de captura (por defecto, sintaxis Java).
        aliases: Diccionario que asocia nombres de grupos con sus alias.
        substitutions: Reglas aplicadas al texto literal final (sanitización).
    """

    syntax: str = "(?<{name}>{pattern})"
    aliases: AliasDict = {}
    substitutions: SubstitutionDict = {}

    def __resolve_group_name(self, group: InferenceGroup) -> str:
        """Resuelve el nombre definitivo de un grupo manejando colisiones y alias."""
        if group.occurrence == 0:
            return group.name

        alias_index = group.occurrence - 1

        if group.name in self.aliases and alias_index < len(self.aliases[group.name]):
            return self.aliases[group.name][alias_index]

        return group.identifier

    def create_template_group(self, group: InferenceGroup, wrapper: Wrapper) -> str:
        """Construye el bloque regex definitivo para el template.

        Args:
            group: Metadatos del grupo a formatear.
            wrapper: El envoltorio exacto que rodeaba al valor detectado.

        Returns:
            Cadena de expresión regular definitiva (ej. `'(?P<userId>-?\\d+)?'`).
        """
        resolved_name = self.__resolve_group_name(group)

        group_pattern = self.syntax.format(name=resolved_name, pattern=group.pattern)

        if len(wrapper) > 0:
            group_pattern += "?"

        return wrapper.wrap(group_pattern)

    def apply_substitutions(self, string: str) -> str:
        """Aplica los reemplazos configurados por el usuario."""
        for pattern, replacement in self.substitutions.items():
            string = pattern.sub(replacement, string)

        return string


class CapturedGroup(NamedTuple):
    """Representa un valor real extraído del log mediante regex.

    Attributes:
        identifier: Identificador del grupo (ej. 'userId0').
        value: El fragmento de texto extraído, envuelto (ej. "'13'").
        start: Posición inicial absoluta en el match general.
        end: Posición final absoluta en el match general.
    """

    identifier: str
    value: str
    start: int
    end: int


class TemplateBuilder:
    """Motor de ejecución que infiere campos e inyecta plantillas.

    Aplica una arquitectura de 3 fases (búsqueda, sanitización, ensamblado)
    garantizando que no se produzcan colisiones de "canibalización" de regex
    durante las transformaciones secuenciales masivas.
    """

    __rules: Iterable[InferenceRule]
    __formatter: Formatter
    __tokens: dict[str, str]
    __inference_groups: dict[str, InferenceGroup]

    def __inject_token(self, match: re.Match[str]) -> str:
        """Callback privado para aislar grupos y protegerlos con tókenes.

        Extrae valores validados, encuentra su envoltorio real,
        delega la creación del patrón final al Formatter y guarda dicho patrón
        en un diccionario, vinculándolo a un token insertado en el texto.
        """
        text = match.group()
        offset = match.start()

        # Extracción de capturas ignorando nulos
        captured_groups = [
            CapturedGroup(group_indetifier, group_value, *match.span(group_indetifier))
            for group_indetifier, group_value in match.groupdict().items()
            if group_value is not None
        ]

        # Proceso de grupos de derecha a izquierda para no alterar los offsets del texto
        captured_groups.sort(key=lambda group: group.start, reverse=True)

        for captured_group in captured_groups:
            inference_group = self.__inference_groups[captured_group.identifier]

            # Deducción del envoltorio del valor
            wrapper = next(
                w for w in inference_group.wrappers if w.wraps(captured_group.value)
            )

            token_identifier = f"__TOKEN{len(self.__tokens)}__"
            token_pattern = self.__formatter.create_template_group(
                inference_group, wrapper
            )
            self.__tokens[token_identifier] = token_pattern

            start, end = captured_group.start - offset, captured_group.end - offset
            text = text[:start] + token_identifier + text[end:]

        return text

    def __sanitize(self, string: str) -> Generator[str]:
        """Generador que aísla la sanitización del texto literal de los patrones regex.

        Usa los tókenes como puntos de fragmentación. Esto evita
        que reglas de reemplazo del usuario corrompan la sintaxis de captura.
        """
        for chunk in re.split(r"(__TOKEN\d+__)", string):
            if chunk in self.__tokens:
                yield self.__tokens[chunk]
            elif chunk:
                yield self.__formatter.apply_substitutions(chunk)

    def __init__(self, rules: Iterable[InferenceRule], formatter: Formatter) -> None:
        """Inicializa el inyector vinculándolo a unas reglas base ya compiladas."""
        self.__rules = rules
        self.__formatter = formatter
        self.__tokens = {}

    def build(self, string: str) -> str | None:
        """Infiere campos en el log original y devuelve un template regex preparado para el motor regex de destino.

        Args:
            string: Línea de log en bruto original a procesar.

        Returns:
            Template regex o None si no hay campos identificables.
        """
        self.__tokens.clear()
        string = Plumber.prepare(string)

        # Fase de inferencia (búsqueda e inyección de tókenes)
        for rule in self.__rules:
            self.__inference_groups = rule.inference_groups
            string = rule.pattern.sub(self.__inject_token, string)

        if not self.__tokens:
            return None

        # Fase de ensamblaje y sanitización
        string = "".join(self.__sanitize(string))

        return string


################################################################################
# Carga del motor de inferencia
################################################################################


class InferenceEngine(BaseModel):
    """Punto de entrada general. Configura y orquesta el Builder.

    Attributes:
        keywords: Diccionario con los tipos de datos disponibles.
        rules: Reglas de inferencia ordenadas en prioridad descendente.
        formatter: Motor de sintaxis regex que unificará la salida.
    """

    keywords: KeywordDict
    rules: ReverseSortedIterable[str]
    formatter: Formatter = Formatter()

    def get_template_builder(self) -> TemplateBuilder:
        inference_rules = [InferenceRule(rule, self.keywords) for rule in self.rules]

        return TemplateBuilder(inference_rules, self.formatter)

    @classmethod
    def from_yaml(cls, path: str | Path) -> InferenceEngine:
        with open(path, encoding="utf-8") as file:
            config: dict[str, Any] = yaml.safe_load(file) or {}

        return cls(**config)
