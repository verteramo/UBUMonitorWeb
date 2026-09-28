/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.logs

import java.util.regex.Pattern

/**
 * Representa un template pre-compilado.
 * Se cargan y compilan desde ficheros durante el arranque de la aplicación.
 *
 * @param patternString Cadena de texto con la regex.
 */
class Template(
  patternString: String,
) {
  /**
   * Patrón compilado en memoria para optimizar las evaluaciones sobre las líneas de log.
   */
  private val pattern = Pattern.compile(patternString)

  /**
   * Evalúa la descripción frente al patrón y extrae las capturas de los grupos con nombre.
   *
   * @param text Texto sobre el que se realiza la búsqueda.
   * @return Mapa asociando el nombre de cada grupo capturado con su valor.
   */
  fun match(text: String): Map<String, String>? =
    pattern.matcher(text).takeIf { it.find() }?.let { matcher ->
      matcher
        .namedGroups()
        .keys
        .mapNotNull { name -> matcher.group(name)?.let { name to it } }
        .toMap()
    }

  /**
   * Representación textual de la expresión regular asociada a un template.
   */
  override fun toString(): String = pattern.pattern()
}
