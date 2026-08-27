/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.data.dto

import es.ubu.lsi.ubumonitorweb.data.api.Section

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
) {
  fun toSection() =
    Section(
      id = id,
      name = name,
      visible = visible == 1,
      order = section,
      userVisible = uservisible == true,
      modules = modules.map { it.toModule() },
    )
}
