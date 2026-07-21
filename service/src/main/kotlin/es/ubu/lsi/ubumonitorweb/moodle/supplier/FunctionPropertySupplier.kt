package es.ubu.lsi.ubumonitorweb.moodle.supplier

import es.ubu.lsi.ubumonitorweb.core.http.ServiceProfileProcessor
import es.ubu.lsi.ubumonitorweb.core.http.ServicePropertySupplier
import org.springframework.stereotype.Component
import java.lang.reflect.Method

/**
 * Proveedor para obtener el nombre de las funciones del webservice de Moodle, de acuerdo con la
 * [convención](https://docs.moodle.org/dev/Web_service_API_functions#Web_service_functions).
 *
 * Por ejemplo:
 * ```kotlin
 * @ServiceProfile("profile")
 * interface CoreUserService {
 *   @PostExchange
 *   fun getUserPreferences() {...}
 * }
 * ```
 *
 * El procesador del servicio utiliza el proveedor para transformar
 * `CoreUserService.getUserPreferences` en `core_user_get_user_preferences` y, posteriormente,
 * inyecta en la solicitud saliente el parámetro `wsfunction` con el valor obtenido.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component("functionPropertySupplier")
class FunctionPropertySupplier : ServicePropertySupplier<String> {

  companion object {
    /** Sufijo del nombre de las interfaces [org.springframework.web.service.annotation.HttpExchange]. */
    private const val SUFFIX = "Service"

    /** Expresión regular para la identificación de cambios de minúscula a mayúscula. */
    private val regex = Regex("(?<=[a-z])(?=[A-Z])")
  }

  /** Nombre del servicio sin sufijo. */
  private val Class<*>.serviceName: String
    get() = simpleName.removeSuffix(SUFFIX)

  /**
   * Obtiene el nombre del método (cualificado con el nombre de su tipo) en formato snake case.
   *
   * @return Nombre cualificado del método en formato snake case.
   */
  private fun Method.toSnakeCase(): String {
    return "${declaringClass.serviceName}_${name}".replace(regex, "_").lowercase()
  }

  /**
   * Invocador del objeto proveedor que invoca el procesador [ServiceProfileProcessor] para resolver
   * el valor del parámetro.
   *
   * @return Nombre cualificado del método en formato snake case.
   */
  override fun ServicePropertySupplier.Context.get(): String {
    return method.toSnakeCase()
  }
}
