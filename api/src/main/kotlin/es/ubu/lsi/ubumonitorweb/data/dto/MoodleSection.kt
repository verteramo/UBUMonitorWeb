package es.ubu.lsi.ubumonitorweb.feature.course.dto

data class MoodleSection(
  val id: Int,
  val name: String?,
  val visible: Int,
  val summary: String,
  val summaryformat: Int,
  val section: Int?,
  val hiddenbynumsections: Int?,
  val uservisible: Boolean?,
  val availabilityinfo: String?,
  val component: String?,
  val itemid: Int?,
  val modules: List<MoodleModule>,
)
