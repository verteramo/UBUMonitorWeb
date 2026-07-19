package es.ubu.lsi.ubumonitorweb.core.rest

import org.springframework.stereotype.Component
import java.lang.reflect.Method

/**
 * Resolutor para obtener el nombre de las funciones del webservice de Moodle,
 * de acuerdo con la
 * [convención](https://docs.moodle.org/dev/Web_service_API_functions#Web_service_functions).
 *
 * Por ejemplo:
 * ```kotlin
 * @MoodleService("config.section")
 * interface CoreUserService {
 *   @PostExchange
 *   fun getUserPreferences() {...}
 * }
 * ```
 *
 * El procesador del servicio utiliza el resolutor para transformar
 * `CoreUserService.getUserPreferences` en `core_user_get_user_preferences` y,
 * posteriormente, inyecta en la solicitud saliente el parámetro `wsfunction`
 * con el valor obtenido.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component("snakeCaseStrategy")
class SnakeCaseStrategy : (Method) -> String {
  companion object {
    private const val SUFFIX = "Service"
    private val regex = Regex("(?<=[a-z])(?=[A-Z])")
  }

  fun Method.toSnakeCase() =
      "${declaringClass.simpleName.removeSuffix(SUFFIX)}_${name}".replace(
        regex, "_",
      ).lowercase()

  override fun invoke(method: Method) =
      method.toSnakeCase()
}