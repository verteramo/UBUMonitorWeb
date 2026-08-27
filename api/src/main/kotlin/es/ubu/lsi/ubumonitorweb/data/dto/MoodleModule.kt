/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.data.dto

import es.ubu.lsi.ubumonitorweb.data.api.Module

data class MoodleModule(
  val id: Int,
  val url: String?,
  val name: String,
  val instance: Int?,
  val contextid: Int?,
  val description: String?,
  val visible: Int?,
  val uservisible: Boolean?,
  val availabilityinfo: String?,
  val visibleoncoursepage: Int?,
  val modicon: String,
  val modname: String,
  val purpose: String,
  val branded: Boolean?,
  val modplural: String,
  val availability: String?,
  val indent: Int,
  val onclick: String?,
  val afterlink: String?,
  val activitybadge: Any?,
  val customdata: String?,
  val noviewlink: Boolean?,
  val candisplay: Boolean?,
  val completion: Int?,
  val completiondata: MoodleCompletionData?,
  val downloadcontent: Int?,
  val dates: List<MoodleDate>,
  val groupmode: Int?,
  val contents: List<MoodleContent>?,
  val contentsinfo: MoodleContentsInfo?,
) {
  fun toModule() =
    Module(
      id = id,
      url = url,
      name = name,
      visible = visible == 1,
      userVisible = uservisible == true,
      type = modname,
      picture = modicon,
      purpose = purpose,
      plural = modplural,
      completion = completion ?: 0,
      since = dates.getOrNull(0)?.sumTimestamps() ?: 0,
      until = dates.getOrNull(1)?.sumTimestamps() ?: 0,
    )
}
