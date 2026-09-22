/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.dto

import es.ubu.lsi.ubumonitorweb.domain.Credentials

/**
 * Objeto de credenciales de Moodle.
 */
data class MoodleToken(
  val token: String,
  val privatetoken: String,
) {
  /**
   * Mapea un token de Moodle, junto con la 'sesskey' y la cookie de sesión a un objeto Credentials.
   */
  fun toCredentials(
    key: String,
    cookie: String,
  ) = Credentials(
    token = token,
    privateToken = privatetoken,
    sessionKey = key,
    sessionCookie = cookie,
  )
}
