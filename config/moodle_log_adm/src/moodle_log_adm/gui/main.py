import sys
from pathlib import Path
from typing import Iterable

import FreeSimpleGUI as sg
from moodle_log_adm.core.loading import process_sources
from moodle_log_adm.gui.layout import (
    get_main_layout,
    prompt_save_template,
)

################################################################################
# Constantes
################################################################################

# Theme por defecto de la ventana.
# https://freesimplegui.readthedocs.io/en/latest/#themes-automatic-coloring-of-your-windows
DEFAULT_THEME = "Default1"

DEFAULT_SIZE = (600, 500)


################################################################################
# Ventana principal
################################################################################


class MainWindow:

    def __init__(self, theme: str, size: tuple[int, int]):
        sg.theme(theme)
        self.window = sg.Window(
            "Moodle log template generator",
            layout=get_main_layout(),
            size=size,
            resizable=True,
            finalize=True,
        )

        self.config_file: Path | None = None
        self.record_files: Iterable[Path] = []
        self.template_files: Iterable[Path] = []

    def start(self):
        while True:
            event, values = self.window.read()

            ####################################################################
            # Botón cancelar
            ####################################################################

            if event in (sg.WIN_CLOSED, "Cancel"):
                break

            ####################################################################
            # Botón aceptar
            ####################################################################

            if event == "Accept":
                self._process_sources()

            ####################################################################
            # Selección del fichero de configuración
            ####################################################################

            if event == "-CONFIG_FILE-":
                raw_path = values["-CONFIG_FILE-"]
                self.config_file = Path(raw_path) if raw_path else None
                self._update_accept_button()

            ####################################################################
            # Selección de ficheros de registros y templates
            ####################################################################

            if event == "-RECORD_FILES_NEW-":
                raw_paths = values["-RECORD_FILES_NEW-"]
                if raw_paths:
                    for p in (Path(path) for path in raw_paths.split(";") if path):
                        if p not in self.record_files:
                            self.record_files.append(p)
                    self._update_record_list()

            if event == "-TEMPLATE_FILES_NEW-":
                raw_paths = values["-TEMPLATE_FILES_NEW-"]
                if raw_paths:
                    for p in (Path(path) for path in raw_paths.split(";") if path):
                        if p not in self.template_files:
                            self.template_files.append(p)
                    self._update_template_list()

            ####################################################################
            # Eliminar seleccionados y limpiar listas
            ####################################################################

            if event == "-RECORD_FILES_DEL-":
                selected_paths = set(values["-RECORD_FILES_LIST-"])
                self.record_files = [
                    p for p in self.record_files if str(p) not in selected_paths
                ]
                self._update_record_list()

            if event == "-RECORD_FILES_CLEAR-":
                self.record_files.clear()
                self._update_record_list()

            if event == "-TEMPLATE_FILES_DEL-":
                selected_paths = set(values["-TEMPLATE_FILES_LIST-"])
                self.template_files = [
                    p for p in self.template_files if str(p) not in selected_paths
                ]
                self._update_template_list()

            if event == "-TEMPLATE_FILES_CLEAR-":
                self.template_files.clear()
                self._update_template_list()

            ####################################################################
            # Estado de los botones de eliminación
            ####################################################################

            if event == "-TEMPLATE_FILES_LIST-":
                self.window["-TEMPLATE_FILES_DEL-"].update(
                    disabled=not bool(values["-TEMPLATE_FILES_LIST-"])
                )

            if event == "-RECORD_FILES_LIST-":
                self.window["-RECORD_FILES_DEL-"].update(
                    disabled=not bool(values["-RECORD_FILES_LIST-"])
                )

        self.window.close()

    def _update_template_list(self):
        paths_str = [str(p) for p in self.template_files]
        self.window["-TEMPLATE_FILES_LIST-"].update(values=paths_str)
        self.window["-TEMPLATE_FILES_CLEAR-"].update(disabled=not self.template_files)
        self.window["-TEMPLATE_FILES_DEL-"].update(disabled=True)
        self._update_accept_button()

    def _update_record_list(self):
        paths_str = [str(p) for p in self.record_files]
        self.window["-RECORD_FILES_LIST-"].update(values=paths_str)
        self.window["-RECORD_FILES_CLEAR-"].update(disabled=not self.record_files)
        self.window["-RECORD_FILES_DEL-"].update(disabled=True)
        self._update_accept_button()

    def _update_accept_button(self):
        has_config = self.config_file is not None and self.config_file.exists()
        has_sources = bool(self.template_files or self.record_files)
        self.window["Accept"].update(disabled=not (has_config and has_sources))

    def _process_sources(self) -> bool:
        """Invoca el procesador del core y coordina el guardado directo en formato YAML."""
        if not self.config_file:
            return False

        try:
            success_dict, failure_dict = process_sources(
                self.config_file,
                self.record_files,
                self.template_files,
            )

            results = (
                f"Results:\n"
                f"Success count: {len(success_dict)}\n"
                f"Failure count: {len(failure_dict)}"
            )

            success_path = prompt_save_template(results)
            if not success_path:
                return False

            success_path.write_text(str(success_dict), encoding="utf-8")

            if failure_dict:
                failure_path = success_path.with_name(
                    f"{success_path.stem}_failures.yaml"
                )
                failure_path.write_text(str(failure_dict), encoding="utf-8")

            return True

        except Exception as ex:
            sg.popup_error(f"Error:\n{ex}")
            return False


def main():
    app = MainWindow(DEFAULT_THEME, DEFAULT_SIZE)
    app.start()


if __name__ == "__main__":
    main()
