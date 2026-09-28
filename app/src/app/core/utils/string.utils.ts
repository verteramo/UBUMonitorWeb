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
 * Analiza un string HTML, resuelve las URLs relativas utilizando un host base
 * y añade target="_blank" para abrirlas de forma segura en una nueva pestaña.
 */
export function resolveRelativeLinks(baseUrl: string, html: string, target: string = '_self'): string {
  const cleanBaseUrl = baseUrl.endsWith('/') ? baseUrl.slice(0, -1) : baseUrl;
  const doc = new DOMParser().parseFromString(html, 'text/html');

  doc.querySelectorAll('a').forEach((anchor) => {
    const href = anchor.getAttribute('href');
    if (href && !href.startsWith('http://') && !href.startsWith('https://')) {
      const separator = href.startsWith('/') ? '' : '/';
      anchor.setAttribute('href', `${cleanBaseUrl}${separator}${href}`);
      anchor.setAttribute('target', target);
    }
  });

  return doc.body.innerHTML;
}
