/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

/**
 * Tipo de fichero para el dataset.
 */
export const UmwDatasetType: FilePickerAcceptType = {
  description: 'UBU Monitor Web Dataset',
  accept: { 'application/octet-stream': ['.umw'] },
};

/**
 * Abre una ventana de selección de fichero y escribe la información en él.
 *
 * @param options Opciones de la ventana.
 * @param data Información a escribir en el fichero.
 */
export async function saveFilePicker(
  options: SaveFilePickerOptions,
  data: FileSystemWriteChunkType,
): Promise<void> {
  const handle = await window.showSaveFilePicker(options);
  const writable = await handle.createWritable();
  await writable.write(data);
  await writable.close();
}

/**
 * Abre una ventana de selección de fichero y lee su información para retornarla.
 *
 * @param options Opciones de la ventana.
 * @returns Información leída del fichero.
 */
export async function openFilePicker(options: OpenFilePickerOptions): Promise<string> {
  const [handle] = await window.showOpenFilePicker({ ...options, multiple: false });
  const file = await handle.getFile();
  return await file.text();
}

