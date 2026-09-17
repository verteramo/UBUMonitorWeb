/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.logs

import java.util.regex.Pattern

/**
 * Representa un template compilado
 */
class Template(
  patternString: String,
) {
  private val pattern = Pattern.compile(patternString)

  fun extract(description: String): Map<String, String>? =
    pattern.matcher(description).takeIf { it.find() }?.let { matcher ->
      matcher
        .namedGroups()
        .keys
        .mapNotNull { name -> matcher.group(name)?.let { name to it } }
        .toMap()
    }

  override fun toString(): String = pattern.pattern()
}
