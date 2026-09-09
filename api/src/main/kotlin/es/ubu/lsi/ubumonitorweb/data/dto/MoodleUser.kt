/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.data.dto

import es.ubu.lsi.ubumonitorweb.data.api.User
import es.ubu.lsi.ubumonitorweb.feature.resource.client.ResourceUrlConverter
import tools.jackson.databind.annotation.JsonDeserialize

data class MoodleUser(
  val id: Int,
  val fullname: String,
  val username: String?,
  val firstname: String?,
  val lastname: String?,
  val initials: String?,
  val email: String?,
  val address: String?,
  val phone1: String?,
  val phone2: String?,
  val department: String?,
  val institution: String?,
  val idnumber: String?,
  val interests: String?,
  val firstaccess: Long?,
  val lastaccess: Long?,
  val lastcourseaccess: Long?,
  val description: String?,
  val descriptionformat: Int?,
  val city: String?,
  val country: String?,
  @JsonDeserialize(converter = ResourceUrlConverter::class) val profileimageurl: String?,
  val customfields: List<MoodleCustomField>?,
  val groups: List<MoodleGroup>?,
  val roles: List<MoodleRole>?,
  val preferences: List<MoodlePreference>?,
  val enrolledcourses: List<MoodleEnrolledCourse>?,
) {
  fun toUser() =
    User(
      id = id,
      username = username,
      email = email,
      fullName = fullname,
      picture = profileimageurl,
      firstAccessTs = firstaccess,
      lastAccessTs = lastaccess,
      lastCourseAccessTs = lastcourseaccess,
      country = country,
      phones = setOfNotNull(phone1, phone2),
      groups = groups?.map { it.name } ?: mutableListOf(),
      roles = roles?.map { it.shortname } ?: mutableListOf(),
      courses = enrolledcourses?.map { it.fullname } ?: mutableListOf(),
    )
}
