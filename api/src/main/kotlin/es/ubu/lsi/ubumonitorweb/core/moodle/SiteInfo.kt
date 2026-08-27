/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.moodle

import es.ubu.lsi.ubumonitorweb.feature.resource.client.ResourceUrlConverter
import tools.jackson.databind.annotation.JsonDeserialize

data class SiteInfo(
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
