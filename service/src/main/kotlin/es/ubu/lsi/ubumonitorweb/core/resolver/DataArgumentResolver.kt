package es.ubu.lsi.ubumonitorweb.core.resolver

import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.service.invoker.HttpServiceArgumentResolver
import java.lang.reflect.Array
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Codificador de data classes como parámetros de solicitud, Spring no incluye un resolutor de
 * argumentos que resuelva data classes (DTO/POJO), en caso de intentar enviar un objeto anotado con
 * [RequestParam], se recibe una excepción como la siguiente:
 *
 * `ConverterNotFoundException: No converter found capable of converting from type [@Credentials] to
 * type [String]`
 *
 * Viéndose el usuario obligado a segregar la solicitud en tipos primitivos:
 *
 * ```kotlin *
 * @MoodleService("profile")
 * interface MyService {
 *   @PostMapping
 *   fun auth(
 *     @RequestParam username: String,
 *     @RequestParam password: String,
 *   ): Any
 * }
 * ```
 *
 * Este resolutor pretende enviar objetos anotados con [RequestParam]:
 *
 * ```kotlin
 * data class Credentials(val username: String, val password: String)
 *
 * @MoodleService("profile")
 * interface MyService {
 *   @PostMapping
 *   fun auth(@RequestParam credentials: Credentials): Any
 * }
 * ```
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class DataArgumentResolver : HttpServiceArgumentResolver {

  /** Caché para evitar reflexiones costosas recurrentes */
  private val cache = ConcurrentHashMap<KClass<*>, Collection<KProperty1<*, *>>>()

  /** Nombre del parámetro de solicitud. */
  private val RequestParam.encodedName: String?
    get() = value.ifBlank { null } ?: name.ifBlank { null }

  /** Nombre de la propiedad resolviendo el posible nombre de la anotación [RequestParam]. */
  private val KProperty1<*, *>.encodedName: String
    get() = findAnnotation<RequestParam>()?.encodedName ?: name

  /**
   * Determina si un parámetro es candidato a ser interceptado y procesado por este resolutor de
   * argumentos. Se debe evitar interceptar tipos primitivos ([Int], [String], [Boolean], etc.) para
   * que sea Spring quien procese dichos argumentos, una buena forma de discriminarlos es conociendo
   * si están anotados con [T] y si fueron declarados con la keyword `data`.
   *
   * @param T Anotación discriminante.
   * @return `true` si el parámetro es candidato, `false` en caso contrario.
   */
  private inline fun <reified T : Annotation> MethodParameter.isCandidate(): Boolean {
    return parameterType.kotlin.isData && AnnotatedElementUtils.hasAnnotation(
      parameter, T::class.java,
    )
  }

  /**
   * Convierte un [Array] nativo en una lista.
   *
   * @return Lista con los elementos del array original.
   */
  private fun Array.toList(): List<Any?> {
    return (0 until Array.getLength(this)).map { index ->
      Array.get(this, index)
    }
  }

  /**
   * Método resolutor.
   *
   * @param argument Valor pasado al método de la interfaz [HttpExchange].
   * @param parameter Reflexión encapsulada con los metadatos del método.
   * @param requestValues Builder de la solicitud saliente.
   * @return `true` si el parámetro fue resuelto, `false` en caso contrario.
   */
  override fun resolve(
      argument: Any?,
      parameter: MethodParameter,
      requestValues: HttpRequestValues.Builder,
  ): Boolean {
    if (parameter.isCandidate<RequestParam>() && argument != null) {
      val type = argument.javaClass.kotlin

      // Se obtienen las propiedades por reflexión siempre que las propiedades clase no hayan sido
      // extraídas con anterioridad
      cache.computeIfAbsent(type) { type.memberProperties }.forEach { prop ->
        prop.getter.call(argument)?.let { value ->
          when (value) { // Conversión de los elementos del iterable a cadena
            is Iterable<*> -> {
              value.filterNotNull().map(Any::toString)
            }

            // Conversión del array a lista y de sus elementos a cadena
            is Array -> {
              value.toList().filterNotNull().map(Any::toString)
            }

            // Si solo hay un elemento, se encapsula en una lista
            else -> {
              listOf(value.toString())
            }
          }.forEach {

            // Finalmente, se añaden los parámetros con todos sus valores
            requestValues.addRequestParameter(prop.encodedName, it)
          }
        }
      }

      // Si se ha procesado el argumento se devuelve true para que Spring no siga buscando un
      // resolutor compatible
      return true
    }

    // Al devolver falso, se le indica a Spring que debe seguir buscando un resolutor para el
    // argumento actual
    return false
  }
}
