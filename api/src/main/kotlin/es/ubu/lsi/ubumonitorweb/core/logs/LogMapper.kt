/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.logs

import org.springframework.stereotype.Component

/**
 * Mapper que extrae atributos de la descripción de los logs de Moodle.
 *
 * @property properties Propiedades de configuración de los logs.
 * @property templateRegistry Registro de templates cargados en memoria y compilados.
 */
@Component
class LogMapper(
  private val properties: LogsProperties,
  private val templateRegistry: TemplateRegistry,
) {
  /**
   * Expresión regular para identificar caracteres blancos (\s, \t, \n, \r).
   */
  private val whiteChars = Regex("""\s+""")

  /**
   * Mapea atributos desde la descripción de un log.
   *
   * @param component Nombre del componente.
   * @param event Nombre del evento.
   * @param description Descripción de log.
   * @return Resultado del mapping.
   */
  fun map(
    component: String,
    event: String,
    description: String,
  ): MappingResult =
    /*
     * 1. Normalización de caracteres blancos.
     * 2. Obtención de la lista de templates correspondiente al par componente/evento.
     * 3. En caso de match se obtiene un MappingResult de tipo Mapped,
     *    si se agota la lista de templates, se obtiene un MappingResult de tipo Unmapped,
     *    que incluye la descripción y los templates disponibles.
     */
    description.replace(whiteChars, " ").let { description ->
      templateRegistry[component, event].let { templates ->
        templates.firstNotNullOfOrNull { template ->
          template.match(description)?.let { map ->
            MappingResult.Mapped(
              map.mapValues { (key, value) ->
                /*
                 * Se pueden definir prefijos para atributos booleanos en la configuración,
                 * por ejemplo: is, has, etc.
                 *
                 * Si hay algún atributo con algún prefijo compatible, por ejemplo:
                 * ...'(?<isCompleted>-?\d+)?'..., se interpreta como booleano.
                 *
                 * En otro caso, se interpreta en cascada: Int o Double o String.
                 */
                when {
                  properties.booleanPrefixes.any { key.startsWith(it) } -> value == "1"
                  else -> value.toIntOrNull() ?: value.toDoubleOrNull() ?: value
                }
              },
            )
          }
        } ?: MappingResult.Unmapped(description, templates)
      }
    }
}
