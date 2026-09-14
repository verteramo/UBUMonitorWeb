/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.mapping

import es.ubu.lsi.ubumonitorweb.domain.LogEntry
import org.springframework.stereotype.Service
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.ObjectMapper
import tools.jackson.dataformat.yaml.YAMLFactory

/**
 * Mapper que, a partir de un fichero de configuración YAML, es capaz de parsear y construir objetos de log
 * listos para su entrega al cliente.
 *
 * El fichero de configuración YAML tiene la siguiente estructura:
 * ```yaml
 * Logs:
 *   Log report viewed: [userId, courseId]
 * ```
 *
 * Suponiendo el siguiente registro de log en JSON:
 * ```json
 * {
 *   "time": "11/09/26, 16:27:10",
 *   "userfullname": "Jeffrey Sanders",
 *   "affecteduser": "-",
 *   "eventcontext": "Course: Effective Memory Techniques",
 *   "component": "Logs",
 *   "eventname": "Log report viewed",
 *   "description": "The user with id '13' viewed the log report for the course with id '72'.",
 *   "origin": "web",
 *   "ipaddress": "2a0c:5a87:d90b:1000:3594:4220:5272:9dc8"
 * }
 * ```
 *
 * De esta manera, se extraen números enteros en el orden de aparición en la descripción y se construye un mapa de
 * atributos como el siguiente:
 * ```json
 * {
 *   "datetime": "2026-09-11T16:27:10+00:00",
 *   "component": "Logs",
 *   "event": "Log report viewed",
 *   "origin": "web",
 *   "ipaddress": "2a0c:5a87:d90b:1000:3594:4220:5272:9dc8",
 *   "attributes": {
 *     "userId": 13,
 *     "courseId": 72
 *   }
 * }
 * ```
 */
@Service
class LogMapper {
  /**
   * Configuración de mappings del fichero YAML.
   */
  private val mappings: Map<String, Map<String, List<List<String>>>> by lazy {
    val mapper = ObjectMapper(YAMLFactory())

    javaClass.getResourceAsStream("/logs-mappings.yaml").use { stream ->
      mapper.readValue(stream, object : TypeReference<Map<String, Map<String, List<List<String>>>>>() {})
    }
  }

  /**
   * Compone un log con sus atributos.
   */
  fun compose(
    entry: LogEntry,
    values: List<Int>,
  ) {
    // Se selecciona la lista de campos que coincide
    // en longitud con el número real de enteros extraídos
    val fields =
      mappings[entry.component]
        ?.get(entry.event)
        ?.find { it.size == values.size } ?: emptyList()

    entry.attributes.putAll(fields zip values)
  }
}
