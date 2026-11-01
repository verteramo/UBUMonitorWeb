import logging
from pathlib import Path
from typing import Iterable

import click
from moodle_log_adm.core.loading import process_sources

################################################################################
# Configuración del logging
################################################################################

LOGGING_FORMAT = "%(asctime)s [%(levelname)s] %(name)s: %(message)s"
LOGGING_LEVELS = [logging.WARNING, logging.INFO, logging.DEBUG]

################################################################################
# Tipos de utilidad
################################################################################

ClickFile = click.Path(exists=True, dir_okay=False, readable=True, path_type=Path)

################################################################################
# Comando principal
# ------------------------------------------------------------------------------
# Permite generar templates de forma ágil desde la línea de comandos.
#
# Ejemplo de uso:
# - `moodle-log-adm-cli CONFIG_FILE -r RECORD_FILE -t TEMPLATE_FILE`
#
# Se pueden incluir múltiples opcions -r y -t, tantas como sean necesarias; la
# salida se entrega en dos ficheros:
# - stdout: Templates correctamente inferidos.
# - stderr: Logs que no se han podido inferir en absoluto.
#
# De esta manera no es necesario complicar la utilización de la herramienta, si
# el usuario quisiera guardar los resultados en su sistema de archivos, solo
# debería redirigir ambas salidas a ficheros físicos:
# - `moodle-log-adm-cli ... 1> success.yaml 2> failure.yaml`
################################################################################


@click.command(help="CLI utility to create Moodle log templates.")
@click.argument(
    "config_file",
    type=ClickFile,
    help="YAML configuration file.",
)
@click.option(
    "-r",
    "--record-file",
    "record_files",
    type=ClickFile,
    multiple=True,
    help="Record files downloaded from Moodle (CSV/JSON).",
)
@click.option(
    "-t",
    "--template-file",
    "template_files",
    type=ClickFile,
    multiple=True,
    help="Template files like UBUMonitor desktop database (JSON/YAML).",
)
@click.option(
    "-v",
    "--verbose",
    count=True,
    help="Logging level: -v (INFO), -vv (DEBUG).",
)
def main(
    config_file: Path,
    record_files: Iterable[Path],
    template_files: Iterable[Path],
    verbose: int,
) -> None:
    """Punto de entrada de la interfaz de línea de comandos (CLI)."""

    # Se requiere al menos una fuente de logs
    if not record_files and not template_files:
        raise click.UsageError(
            "At least one --record-file or --template-file must be provided."
        )

    # Configuración del nivel de logging
    logging.basicConfig(level=LOGGING_LEVELS[verbose], format=LOGGING_FORMAT)

    try:
        # Ejecución del procedimiento
        success_dict, failure_dict = process_sources(
            config_file, record_files, template_files
        )

        # Impresión de resultados
        click.echo(str(success_dict), nl=False)
        click.echo(str(failure_dict), err=True, nl=False)

    except ValueError as err:
        raise click.BadParameter(str(err))


if __name__ == "__main__":
    main()
