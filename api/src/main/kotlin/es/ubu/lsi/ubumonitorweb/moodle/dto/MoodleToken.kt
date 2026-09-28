/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.dto

/**
 * Representa las credenciales de acceso devueltas por los webservices de Moodle.
 *
 * @property token Token principal para autorizar las peticiones a la API.
 * @property privatetoken Token secundario para operaciones específicas como el autologin.
 */
data class MoodleToken(
  val token: String,
  val privatetoken: String,
)
