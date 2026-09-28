/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.provider

import es.ubu.lsi.ubumonitorweb.core.client.PropertyProvider
import org.springframework.stereotype.Component
import java.lang.reflect.Method

/**
 * Provider que resuelve el nombre de las funciones del webservice de Moodle de acuerdo con la
 * [convención](https://docs.moodle.org/dev/Web_service_API_functions#Web_service_functions).
 *
 * Por ejemplo:
 * ```kotlin
 * @Client("my-profile")
 * interface CoreUserClient {
 *   @PostExchange
 *   fun getUserPreferences() {...}
 * }
 * ```
 *
 * El ClientProcessor utiliza este provider para transformar `CoreUserClient.getUserPreferences` en
 * `core_user_get_user_preferences` y, posteriormente, inyectar en la solicitud saliente el parámetro
 * `wsfunction` con el valor obtenido.
 *
 * @see es.ubu.lsi.ubumonitorweb.core.client.ClientProcessor
 */
@Component
class FunctionProvider : PropertyProvider.MethodAware<String?>() {
  /**
   * Sufijo del nombre de las interfaces HTTP.
   */
  private val suffix = "Client"

  /**
   * Expresión regular para la identificación de cambios de minúscula a mayúscula.
   */
  private val regex = Regex("(?<=[a-z])(?=[A-Z])")

  /**
   * Nombre del cliente sin sufijo.
   */
  private val Class<*>.clientName: String
    get() = simpleName.removeSuffix(suffix)

  /**
   * Nombre cualificado de la función en formato snake_case.
   */
  private val Method.functionName: String
    get() = "${declaringClass.clientName}_$name".replace(regex, "_").lowercase()

  /**
   * Resuelve el nombre de la función del webservice de Moodle.
   *
   * @param methodContext Contexto del método cliente HTTP ejecutado.
   * @return Nombre de la función del webservice de Moodle.
   */
  override fun invoke(methodContext: MethodContext) = methodContext.method.functionName
}
