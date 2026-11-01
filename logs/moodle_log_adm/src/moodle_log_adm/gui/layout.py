from pathlib import Path

import FreeSimpleGUI as sg

################################################################################
# Funciones de utilidad
################################################################################


def separator() -> sg.HorizontalSeparator:
    """Crea una nueva instancia del separador cada vez que se invoca."""
    return sg.HorizontalSeparator(pad=(0, 15))


################################################################################
# Diálogo emergente de selección de ruta de guardado
################################################################################


def prompt_save_template(message: str) -> Path | None:
    """Muestra el selector estándar 'Guardar como' para definir la ruta de la plantilla."""
    path = sg.popup_get_file(
        message,
        title="Save results",
        save_as=True,
        default_extension=".yaml",
        file_types=(
            ("All files (*.*)", "*.*"),
            ("YAML files (*.yaml, *.yml)", "*.yaml *.yml"),
        ),
    )

    return Path(path) if path else None


################################################################################
# Layout principal
################################################################################


def get_main_layout() -> list:
    """Devuelve el layout principal unificado con configuración, fuentes y controles."""
    return [
        [sg.Text("Configuration file:")],
        [
            sg.Input(
                key="-CONFIG_FILE-",
                readonly=True,
                expand_x=True,
                enable_events=True,
            ),
            sg.FileBrowse(
                "Browse",
                target="-CONFIG_FILE-",
                file_types=(
                    ("YAML configuration files (*.yaml, *.yml)", "*.yaml *.yml"),
                ),
            ),
        ],
        [separator()],
        [sg.Text("Record files:")],
        [
            sg.Listbox(
                values=[],
                size=(None, 4),
                select_mode=sg.LISTBOX_SELECT_MODE_EXTENDED,
                key="-RECORD_FILES_LIST-",
                expand_x=True,
                expand_y=True,
                enable_events=True,
            ),
        ],
        [
            sg.Push(),
            sg.Input(visible=False, enable_events=True, key="-RECORD_FILES_NEW-"),
            sg.FilesBrowse(
                "Add files",
                target="-RECORD_FILES_NEW-",
                file_types=(
                    ("Log files (*.csv, *.json)", "*.csv *.json"),
                    ("CSV files (*.csv)", "*.csv"),
                    ("JSON files (*.json)", "*.json"),
                ),
            ),
            sg.Button("Delete selected", key="-RECORD_FILES_DEL-", disabled=True),
            sg.Button("Clear", key="-RECORD_FILES_CLEAR-", disabled=True),
        ],
        [separator()],
        [sg.Text("Template files:")],
        [
            sg.Listbox(
                values=[],
                size=(None, 4),
                select_mode=sg.LISTBOX_SELECT_MODE_EXTENDED,
                key="-TEMPLATE_FILES_LIST-",
                expand_x=True,
                expand_y=True,
                enable_events=True,
            ),
        ],
        [
            sg.Push(),
            sg.Input(visible=False, enable_events=True, key="-TEMPLATE_FILES_NEW-"),
            sg.FilesBrowse(
                "Add files",
                target="-TEMPLATE_FILES_NEW-",
                file_types=(
                    (
                        "Template files (*.yaml, *.yml, *.json)",
                        "*.yaml *.yml *.json",
                    ),
                    ("YAML files (*.yaml, *.yml)", "*.yaml *.yml"),
                    ("JSON files (*.json)", "*.json"),
                ),
            ),
            sg.Button("Delete selected", key="-TEMPLATE_FILES_DEL-", disabled=True),
            sg.Button("Clear", key="-TEMPLATE_FILES_CLEAR-", disabled=True),
        ],
        [separator()],
        [
            sg.Push(),
            sg.Button("Accept", bind_return_key=True, disabled=True),
            sg.Button("Cancel"),
        ],
    ]
