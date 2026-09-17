/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.logs

import org.springframework.stereotype.Component
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.ObjectMapper
import tools.jackson.dataformat.yaml.YAMLFactory
import java.io.File

/**
 * Registro que hace de fuente única de templates para parsear logs.
 *
 * Se nutre de 2 orígenes, del fichero local `logs-templates.yaml` depositado
 * en el directorio `resources`; y del fichero de igual nombre pero depositado
 * a la par del ejecutable de la aplicación.
 *
 * El fichero local es muy recomendable mantenerlo actualizado,
 * ya que provee de una batería de templates por defecto a la distribución;
 * por otro lado, la finalidad del fichero externo es servir de soporte para
 * correcciones puntuales sin requerir compilación, en un caso extremo un
 * usuario no tan avanzado podría construir sus propios templates de acuerdo
 * con los plugins que utilice en su distribución de Moodle particular.
 *
 * Se utiliza YAML por ser un formato muy poco verboso y, en consecuencia, los
 * ficheros menos pesados que otros formatos como JSON, además, a simple vista,
 * tanto usuarios como desarrolladores pueden comprender su estructura y sintaxis.
 */
@Component
class TemplateRegistry {
  private typealias TemplateMap<T> = Map<String, Map<String, T>>
  private typealias TemplateMMap = MutableMap<String, MutableSet<String>>

  /**
   * Nombre de los ficheros, se llaman igual para mantener una convención sólida.
   */
  private val fileName = "logs-templates.yaml"

  /**
   * Colección de templates que se hidrata desde ambos ficheros.
   */
  private val templates: TemplateMap<List<Template>> by lazy {
    val mapper = ObjectMapper(YAMLFactory())
    val typeReference = object : TypeReference<TemplateMap<List<String>>>() {}

    // Carga de la colección local disponible en resources, si está disponible
    val internalTemplatesCollection =
      javaClass.getResourceAsStream("/$fileName")?.use {
        mapper.readValue(it, typeReference)
      } ?: emptyMap()

    // Carga de la colección externa, si está disponible
    val externalFile = File(fileName)
    val externalTemplatesCollection =
      if (externalFile.exists()) {
        mapper.readValue(externalFile, typeReference)
      } else {
        emptyMap()
      }

    // Colección en la que se realiza la fusión
    val mergedTemplates = mutableMapOf<String, TemplateMMap>()

    // Procedimiento de fusión
    listOf(internalTemplatesCollection, externalTemplatesCollection).forEach { collection ->
      collection.forEach { (component, events) ->
        val componentMap = mergedTemplates.getOrPut(component) { mutableMapOf() }
        events.forEach { (event, templates) ->
          componentMap.getOrPut(event) { mutableSetOf() }.addAll(templates)
        }
      }
    }

    // Mapeo de plantillas de texto a plantillas pre-compiladas (listas para usar de forma eficiente)
    mergedTemplates.mapValues { (_, events) -> events.mapValues { (_, templates) -> templates.map { Template(it) } } }
  }

  /**
   * Permite acceder a las plantillas usando el operador get, es decir:
   * ```kotlin
   * templateRegistry[component, event]
   * ```
   */
  operator fun get(
    component: String,
    event: String,
  ): List<Template> = templates[component]?.get(event).orEmpty()
}
