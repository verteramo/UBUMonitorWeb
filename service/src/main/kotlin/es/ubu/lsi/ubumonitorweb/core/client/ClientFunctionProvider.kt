package es.ubu.lsi.ubumonitorweb.core.client

import org.springframework.stereotype.Component
import org.springframework.web.service.annotation.HttpExchange
import java.lang.reflect.Method

/**
 * Proveedor que obtiene el nombre de las funciones del webservice de Moodle, de acuerdo con la
 * [convención](https://docs.moodle.org/dev/Web_service_API_functions#Web_service_functions).
 *
 * Por ejemplo:
 * ```kotlin
 * @ClientProfile("my-profile")
 * interface CoreUserClient {
 *   @PostExchange
 *   fun getUserPreferences() {...}
 * }
 * ```
 *
 * El procesador del cliente utiliza el proveedor para transformar
 * `CoreUserClient.getUserPreferences` en `core_user_get_user_preferences` y, posteriormente,
 * inyecta en la solicitud saliente el parámetro `wsfunction` con el valor obtenido.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class ClientFunctionProvider : ClientPropertyProvider<String?> {
  companion object {
    /** Sufijo del nombre de las interfaces [HttpExchange]. */
    private const val SUFFIX = "Client"

    /** Expresión regular para la identificación de cambios de minúscula a mayúscula. */
    private val regex = Regex("(?<=[a-z])(?=[A-Z])")
  }

  /** Nombre del cliente sin sufijo. */
  private val Class<*>.client get() = simpleName.removeSuffix(SUFFIX)

  /**
   * Obtiene el nombre del método (cualificado con el nombre de su tipo) en formato snake case.
   *
   * @return Nombre cualificado del método en formato snake case.
   */
  private fun Method.toSnakeCase() = "${declaringClass.client}_$name".replace(regex, "_").lowercase()

  /**
   * Invocador del objeto proveedor que ejecuta el procesador para resolver el valor del parámetro.
   *
   * @return Nombre de la función del webservice de Moodle.
   */
  override fun invoke(context: ClientPropertyProvider.Context) = context.method.toSnakeCase()
}
