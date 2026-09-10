/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import CryptoJS from 'crypto-js';

/**
 * Función de utilidad para hashear una cadena con SHA256.
 *
 * @param value Valor a hashear.
 * @returns Hash.
 */
export function sha256(value: string): string {
  return CryptoJS.SHA256(value).toString(CryptoJS.enc.Hex);
}

/**
 * Cifra una cadena utilizando AES y un hash como clave secreta.
 *
 * @param data Información.
 * @param hash Hash.
 * @returns Información cifrada.
 */
export function encryptAes(data: string, hash: string): string {
  return CryptoJS.AES.encrypt(data, hash).toString();
}

/**
 * Descifra una cadena utilizando AES y un hash como clave secreta.
 *
 * @param data Información cifrada.
 * @param hash Hash.
 * @returns Información en claro.
 */
export function decryptAes(data: string, hash: string): string {
  return CryptoJS.AES.decrypt(data, hash).toString(CryptoJS.enc.Utf8);
}
