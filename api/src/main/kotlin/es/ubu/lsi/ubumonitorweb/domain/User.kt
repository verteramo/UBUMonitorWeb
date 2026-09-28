/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.domain

import java.time.ZoneId

/**
 * Información del usuario autenticado en la aplicación.
 *
 * @property id Identificador único.
 * @property username Nombre de usuario.
 * @property firstName Nombre de pila.
 * @property lastName Apellidos.
 * @property fullName Nombre completo.
 * @property picture URL de la imagen de perfil, si está disponible.
 * @property language Idioma configurado.
 * @property timezone Zona horaria del usuario.
 * @property isAdmin Indicador de privilegios de administración.
 * @property siteUrl URL del sitio Moodle.
 * @property siteName Nombre del sitio Moodle.
 * @property siteVersion Versión del sitio Moodle.
 * @property siteRelease Release del sitio Moodle.
 * @property siteTimezone Zona horaria configurada en el servidor de Moodle.
 */
data class User(
  val id: Int,
  val username: String,
  val firstName: String,
  val lastName: String,
  val fullName: String,
  val picture: String?,
  val language: String,
  val timezone: ZoneId,
  val isAdmin: Boolean,
  val siteUrl: String,
  val siteName: String,
  val siteVersion: String?,
  val siteRelease: String?,
  val siteTimezone: ZoneId,
)
