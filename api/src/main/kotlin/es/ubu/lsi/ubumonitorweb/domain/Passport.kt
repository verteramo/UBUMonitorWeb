/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.domain

import java.security.MessageDigest
import java.util.UUID

/**
 * Modelo de dominio que crea un passport necesario para identificar solicitudes de login SSO.
 */
data class Passport(
  val url: String,
  val uuid: String = UUID.randomUUID().toString(),
) {
  /**
   * Moodle utiliza el algoritmo MD5 para construir el hash.
   */
  private val messageDigest: MessageDigest = MessageDigest.getInstance("MD5")

  /**
   * Transformación intermedia a ByteArray.
   */
  private val bytes: ByteArray = (url + uuid).toByteArray(Charsets.UTF_8)

  /**
   * Hash MD5 en formato ByteArray.
   */
  private val digest: ByteArray = messageDigest.digest(bytes)

  /**
   * Hash MD5 en formato String.
   */
  val hash: String get() = digest.joinToString("") { "%02x".format(it) }
}
