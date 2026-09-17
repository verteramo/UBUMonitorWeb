/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.logs

import org.springframework.stereotype.Component

/**
 * Mapper que extrae atributos de la descripción de los logs de Moodle.
 */
@Component
class LogMapper(
  private val templateRegistry: TemplateRegistry,
) {
  /**
   * Expresión regular para identificar caracteres blancos (\s, \t, \n, \r).
   */
  private val whiteChars = Regex("""\s+""")

  /**
   * Parsea atributos desde la descripción de un log.
   */
  fun map(
    component: String,
    event: String,
    description: String,
  ): MappingResult =
    description.replace(whiteChars, " ").let { description ->
      templateRegistry[component, event].let { templates ->
        templates.firstNotNullOfOrNull { template ->
          template.extract(description)?.let {
            MappingResult.Mapped(
              it.mapValues { (key, value) ->
                when {
                  key.startsWith("is") || key.startsWith("has") -> value == "1"
                  else -> value.toIntOrNull() ?: value.toDoubleOrNull() ?: value
                }
              },
            )
          }
        }
          ?: MappingResult.Unmapped(description, templates)
      }
    }
}
