/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

/**
 * Convierte un string a PascalCase.
 *
 * Ejemplos:
 * ```text
 * double-barrel = DoubleBarrel
 * DOUBLE-BARREL = DoubleBarrel
 * DoUbLE-BaRRel = DoubleBarrel
 * double barrel = DoubleBarrel
 * ```
 *
 * @author Kobi
 * @see https://stackoverflow.com/a/4068586/9687629
 * @license https://creativecommons.org/licenses/by-sa/2.5/
 */
export function toPascalCase(value: string): string {
  return value.replace(/(\w)(\w*)/g, function (_, g1: string, g2: string) {
    return g1.toUpperCase() + g2.toLowerCase();
  });
}

/**
 * Elimina la barra final de una URL, si existe.
 *
 * @param url URL que se desea limpiar.
 * @returns URL limpia sin barra final.
 */
export function cleanUrl(url: string): string {
  return url.replace(/\/$/, '');
}

/**
 * Analiza un string HTML, resuelve las URLs relativas utilizando un host base
 * y añade el atributo target especificado para abrirlas de forma segura.
 *
 * @param html Contenido HTML que se va a procesar.
 * @param baseUrl URL base para resolver las rutas relativas.
 * @param target Destino de los enlaces.
 * @returns HTML procesado con las URLs relativas resueltas.
 */
export function resolveRelativeLinks(
  html: string,
  baseUrl: string,
  target: string = '_self',
): string {
  const doc = new DOMParser().parseFromString(html, 'text/html');

  doc.querySelectorAll('a').forEach((anchor) => {
    const href = anchor.getAttribute('href');
    if (href && !href.startsWith('http://') && !href.startsWith('https://')) {
      const separator = href.startsWith('/') ? '' : '/';
      anchor.setAttribute('href', `${cleanUrl(baseUrl)}${separator}${href}`);
      anchor.setAttribute('target', target);
    }
  });

  return doc.body.innerHTML;
}

/**
 * Obtiene el valor de un parámetro determinado de una URI,
 * por ejemplo: 'param1=value1&param2=value2&...' -> value.
 *
 * @param uri URI.
 * @param paramName Nombre del parámetro.
 * @returns Valor del parámetro si existe.
 */
export function getURIParam(uri: string, paramName: string): string | undefined {
  return decodeURIComponent(uri).match(new RegExp(`${paramName}=([^&/]+)`))?.[1];
}
