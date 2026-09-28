/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.converter

import tools.jackson.databind.util.StdConverter

/**
 * Conversor de Jackson que reescribe URLs de avatares de Moodle hacia rutas internas de la API.
 */
class UrlConverter : StdConverter<String, String>() {
  /**
   * Patrón de expresión regular para capturar el identificador de usuario y el tamaño del icono.
   */
  private val pattern = Regex("""/(\d+)/user/icon/[^/]+/([^/?]+)""")

  /**
   * Extrae el identificador de usuario y el tamaño del icono para construir la ruta local del recurso.
   *
   * @param value URL original del avatar devuelta por Moodle.
   * @return Ruta local interna formateada o `null` si el texto es nulo, en blanco o no encaja con el patrón.
   */
  override fun convert(value: String?): String? =
    value?.takeIf { it.isNotBlank() }?.let {
      pattern.find(it)?.destructured?.let { (id, size) ->
        "/api/resources/user-icon/$id/$size"
      }
    }
}
