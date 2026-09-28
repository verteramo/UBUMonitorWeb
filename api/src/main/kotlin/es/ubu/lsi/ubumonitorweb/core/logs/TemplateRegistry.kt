/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.logs

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.readValue
import java.io.File

/**
 * Registro que hace de fuente única de templates para parsear logs.
 *
 * Se nutre de 2 orígenes, del fichero local depositado en `resources`;
 * y del fichero de igual nombre pero depositado a la par del ejecutable de la aplicación.
 * El nombre de ambos ficheros se puede configurar con la propiedad `logs.filename`.
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
 *
 * @param properties Propiedades de configuración de los logs.
 * @param yamlMapper Mapper YAML.
 */
@Component
@EnableConfigurationProperties(LogsProperties::class)
class TemplateRegistry(
  properties: LogsProperties,
  yamlMapper: YAMLMapper,
) {
  /**
   * Tipo para el mapa de templates.
   */
  private typealias TemplateMap<T> = Map<String, Map<String, T>>

  /**
   * Tipo para el mapa mutable de templates.
   */
  private typealias TemplateMMap = MutableMap<String, MutableSet<String>>

  /**
   * Colección de templates que se hidrata desde ambos ficheros (recurso local y externo).
   */
  private val templates: TemplateMap<List<Template>> by lazy {
    // Carga de la colección local disponible en resources
    val internalCollection =
      javaClass
        .getResourceAsStream("/${properties.filename}")
        ?.use { yamlMapper.readValue<TemplateMap<List<String>>>(it) } ?: emptyMap()

    // Carga de la colección externa, si está disponible
    val externalFile = File(properties.filename)
    val externalCollection =
      if (externalFile.exists()) yamlMapper.readValue<TemplateMap<List<String>>>(externalFile) else emptyMap()

    // Colección en la que se realiza la fusión
    val mergedTemplates = mutableMapOf<String, TemplateMMap>()

    // Procedimiento de fusión
    listOf(internalCollection, externalCollection).forEach { collection ->
      collection.forEach { (component, events) ->
        val componentMap = mergedTemplates.getOrPut(component) { mutableMapOf() }
        events.forEach { (event, templates) ->
          componentMap.getOrPut(event) { mutableSetOf() }.addAll(templates)
        }
      }
    }

    // Mapeo de templates de texto a templates pre-compilados (listas para usar de forma eficiente)
    mergedTemplates.mapValues { (_, events) -> events.mapValues { (_, templates) -> templates.map { Template(it) } } }
  }

  /**
   * Permite acceder a las templates usando el operador get:
   *
   * ```kotlin
   * templateRegistry[component, event]
   * ```
   *
   * @param component Nombre del componente.
   * @param event Nombre del evento.
   * @return Lista de emplates disponibles.
   */
  operator fun get(
    component: String,
    event: String,
  ): List<Template> = templates[component]?.get(event).orEmpty()
}
