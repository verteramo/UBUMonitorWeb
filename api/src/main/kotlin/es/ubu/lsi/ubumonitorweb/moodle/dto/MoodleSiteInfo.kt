/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.dto

import es.ubu.lsi.ubumonitorweb.core.resolver.ResourceUrlConverter
import es.ubu.lsi.ubumonitorweb.domain.Principal
import tools.jackson.databind.annotation.JsonDeserialize
import java.time.ZoneId

data class MoodleSiteInfo(
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
) {
  /**
   * Mapea los datos del Principal.
   */
  fun toPrincipal(
    timezone: ZoneId,
    siteTimezone: ZoneId,
  ) = Principal(
    id = userid,
    username = username,
    isAdmin = userissiteadmin == true,
    language = lang,
    firstName = firstname,
    lastName = lastname,
    fullName = fullname,
    picture = userpictureurl,
    timezone = timezone,
    siteUrl = siteurl,
    siteName = sitename,
    siteVersion = version,
    siteRelease = release,
    siteTimezone = siteTimezone,
  )
}
