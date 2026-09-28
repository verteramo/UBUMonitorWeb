/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.domain

/**
 * Llavero.
 * Contenedor de dominio que agrupa los elementos de seguridad y sesión del usuario.
 *
 * @property token Token de acceso a los servicios web.
 * @property privateToken Token privado complementario para operaciones específicas.
 * @property sessionKey Token CSRF para operaciones POST en el frontend (`sesskey`).
 */
data class Keychain(
  val token: String,
  val privateToken: String,
  val sessionKey: String,
)
