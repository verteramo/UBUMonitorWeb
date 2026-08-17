package es.ubu.lsi.ubumonitorweb.feature.user.dto

import es.ubu.lsi.ubumonitorweb.core.moodle.ResourceUrlConverter
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
  val lastaccess: Int?,
  val lastcourseaccess: Int?,
  val description: String?,
  val descriptionformat: Int?,
  val city: String?,
  val country: String?,
  @JsonDeserialize(converter = ResourceUrlConverter::class) val profileimageurlsmall: String?,
  @JsonDeserialize(converter = ResourceUrlConverter::class) val profileimageurl: String?,
  val customfields: List<MoodleCustomField>?,
  val groups: List<MoodleGroup>?,
  val roles: List<MoodleRole>?,
  val preferences: List<MoodlePreference>?,
  val enrolledcourses: List<MoodleEnrolledCourse>?,
)
