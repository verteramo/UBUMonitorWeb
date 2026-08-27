/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.data.dto

data class MoodleTag(
  val id: Int,
  val name: String,
  val rawname: String,
  val isstandard: Boolean,
  val tagcollid: Int,
  val taginstanceid: Int,
  val taginstancecontextid: Int,
  val itemid: Int,
  val ordering: Int,
  val flag: Boolean,
  val viewurl: String?,
)
