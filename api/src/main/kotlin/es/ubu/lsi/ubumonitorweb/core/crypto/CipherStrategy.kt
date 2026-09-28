/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.crypto

/**
 * Contrato para estrategias de cifrado.
 */
interface CipherStrategy {
  /**
   * Cifra datos.
   *
   * @param data Datos.
   * @param key Clave simétrica.
   * @return Datos cifrados.
   */
  fun encrypt(
    data: ByteArray,
    key: ByteArray,
  ): ByteArray

  /**
   * Descifra datos.
   *
   * @param encryptedData Datos cifrados.
   * @param key Clave simétrica.
   * @return Datos descifrados.
   */
  fun decrypt(
    encryptedData: ByteArray,
    key: ByteArray,
  ): ByteArray
}
