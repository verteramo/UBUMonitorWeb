/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.dto

data class MoodleRuleDetail(
  val rulename: String,
  val rulevalue: MoodleRuleValue,
)
