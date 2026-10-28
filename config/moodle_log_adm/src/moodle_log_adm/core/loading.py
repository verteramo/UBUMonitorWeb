import csv
import json
from pathlib import Path
from typing import Iterable, Iterator

import yaml
from moodle_log_adm.core.collections import (
    LogDict,
    RecordFile,
    TemplateFile,
    TextIOLoader,
)
from moodle_log_adm.core.inference import InferenceEngine

################################################################################
# Constantes con presets de carga habituales
################################################################################

TEMPLATE_FILE_LOADERS: dict[str, TextIOLoader] = {
    ".json": json.load,
    ".yml": yaml.safe_load,
    ".yaml": yaml.safe_load,
}
"""Presets con las funciones de carga utilizadas para ficheros de templates."""

RECORD_FILE_LOADERS: dict[str, TextIOLoader] = {
    # Los ficheros de registros JSON generados por Moodle
    # tienen los registros en una lista anidada como:
    # [[{}, {}, ...]]
    ".json": lambda f: json.load(f)[0],
    ".csv": csv.DictReader,
}
"""Presets con las funciones de carga utilizadas para ficheros de registros."""

RECORD_FILE_COLUMNS: dict[str, tuple[str, str, str]] = {
    ".csv": ("Component", "Event name", "Description"),
    ".json": ("component", "eventname", "description"),
}
"""Presets con los nombres predeterminados de columnas para la extracción de registros ."""

################################################################################
# Lógica de orquestación y procesado de fuentes
################################################################################


def instance_record_files(paths: Iterable[Path]) -> Iterator[RecordFile]:
    """Instancia y valida objetos RecordFile a partir de una secuencia de rutas."""
    for path in paths:
        suffix = path.suffix.lower()
        loader = RECORD_FILE_LOADERS.get(suffix)
        columns = RECORD_FILE_COLUMNS.get(suffix)

        if loader is None or columns is None:
            raise ValueError(
                f"File '{path.name}' is not a supported record file (must be CSV or JSON)."
            )

        yield RecordFile(path, loader, columns)


def instance_template_files(paths: Iterable[Path]) -> Iterator[TemplateFile]:
    """Instancia y valida objetos TemplateFile a partir de una secuencia de rutas."""
    for path in paths:
        suffix = path.suffix.lower()
        loader = TEMPLATE_FILE_LOADERS.get(suffix)

        if loader is None:
            raise ValueError(
                f"File '{path.name}' is not a supported template file type."
            )

        yield TemplateFile(path, loader)


def process_sources(
    config_file: Path,
    record_files: Iterable[Path] = (),
    template_files: Iterable[Path] = (),
) -> tuple[LogDict, LogDict]:
    """Construye el motor de inferencia, carga las fuentes de datos y devuelve los diccionarios acumulados.

    Raises:
        ValueError: Si algún archivo no tiene una extensión soportada.
    """

    builder = InferenceEngine.from_yaml(config_file).get_template_builder()
    master_success_dict = LogDict()
    master_failure_dict = LogDict()

    for source in (
        *instance_record_files(record_files),
        *instance_template_files(template_files),
    ):
        success_dict, failure_dict = source.extract_templates(builder)
        master_success_dict += success_dict
        master_failure_dict += failure_dict

    return master_success_dict, master_failure_dict
