/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import * as CryptoJS from 'crypto-js';

/**
 * Función de utilidad para el algoritmo SHA256.
 *
 * @param value Valor a hashear.
 * @returns Hash.
 */
export function sha256(value: string): string {
  return CryptoJS.SHA256(value).toString(CryptoJS.enc.Hex);
}
