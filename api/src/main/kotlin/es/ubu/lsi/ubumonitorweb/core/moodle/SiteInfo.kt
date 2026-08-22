package es.ubu.lsi.ubumonitorweb.core.moodle

import tools.jackson.databind.annotation.JsonDeserialize

/**
 * Objeto usuario de Moodle.
 *
 * @author Marcelo Verteramo Pérsico
 */
data class Principal(
  val sitename: String,
  val username: String,
  val firstname: String,
  val lastname: String,
  val fullname: String,
  val lang: String,
  val userid: Int,
  val siteurl: String,
  @JsonDeserialize(converter = ResourceUrlConverter::class) val userpictureurl: String?,
  val userissiteadmin: Boolean?,
  val version: String?,
  val release: String?,
)
