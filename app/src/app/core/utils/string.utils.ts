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
