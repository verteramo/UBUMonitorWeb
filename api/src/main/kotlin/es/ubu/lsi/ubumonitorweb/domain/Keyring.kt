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
 * @property sesskey Token CSRF para operaciones de escritura (ej. POST( en el frontend de Moodle.
 */
data class Keyring(
  val token: String,
  val sesskey: String,
)
