/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.crypto

import es.ubu.lsi.ubumonitorweb.core.security.SecurityProperties
import org.springframework.stereotype.Service
import java.security.MessageDigest

/**
 * Servicio encargado de calcular hashes de datos utilizando el algoritmo configurado en las propiedades de seguridad.
 *
 * @property properties Propiedades de configuración de seguridad.
 */
@Service
class DigestService(
  private val properties: SecurityProperties,
) {
  /**
   * Calcula el hash de un arreglo de bytes.
   *
   * @param data Datos de entrada.
   * @return Hash resultante.
   */
  fun digest(data: ByteArray): ByteArray = MessageDigest.getInstance(properties.digestAlgorithm).digest(data)

  /**
   * Calcula el hash de una cadena de texto.
   *
   * @param data Cadena de texto de entrada.
   * @return Hash resultante.
   */
  fun digest(data: String): ByteArray = digest(data.toByteArray())

  companion object {
    /**
     * Convierte el array de bytes en una cadena formateada, aplicando por defecto una representación hexadecimal en minúsculas.
     *
     * @param byteFormat Patrón de formato aplicado a cada byte.
     * @return Cadena resultante tras concatenar los bytes formateados.
     */
    fun ByteArray.asString(byteFormat: String = "%02x"): String = joinToString("") { byteFormat.format(it) }
  }
}
