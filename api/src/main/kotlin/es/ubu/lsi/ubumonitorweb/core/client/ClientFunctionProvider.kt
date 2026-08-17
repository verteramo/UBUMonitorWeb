package es.ubu.lsi.ubumonitorweb.core.client

import org.springframework.stereotype.Component
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
 * @author Marcelo Verteramo Pérsico
 */
@Component
class ClientFunctionProvider : ClientPropertyProvider<String?> {
  /** Sufijo del nombre de los clientes HTTP. */
  private val suffix = "Client"

  /** Expresión regular para la identificación de cambios de minúscula a mayúscula. */
  private val regex = Regex("(?<=[a-z])(?=[A-Z])")

  /** Nombre del cliente sin sufijo. */
  private val Class<*>.clientName: String
    get() = simpleName.removeSuffix(suffix)

  /**  Nombre cualificado de la función en formato snake_case. */
  private val Method.functionName: String
    get() = "${declaringClass.clientName}_$name".replace(regex, "_").lowercase()

  /** Invocador del provider. */
  override fun invoke(context: ClientPropertyProvider.Context) = context.method.functionName
}
