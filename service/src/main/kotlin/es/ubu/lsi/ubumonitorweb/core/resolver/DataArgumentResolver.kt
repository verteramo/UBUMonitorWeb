package es.ubu.lsi.ubumonitorweb.core.resolver

import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.service.invoker.HttpServiceArgumentResolver
import java.lang.reflect.Array
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Codificador de data classes como parámetros de solicitud, Spring no incluye
 * un resolutor de argumentos que resuelva data classes (DTOs o POJOs), en caso
 * de intentar enviar un objeto anotado con [RequestParam], se recibe una
 * excepción como la siguiente:
 *
 * `ConverterNotFoundException: No converter found capable of converting from
 * type [@Credentials] to type [String]`
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
 */
@Component
class DataArgumentResolver : HttpServiceArgumentResolver {

  private val cache =
    ConcurrentHashMap<KClass<*>, Collection<KProperty1<*, *>>>()

  private inline fun <reified T : Annotation> MethodParameter.isCandidate() =
    parameterType.kotlin.isData && AnnotatedElementUtils.isAnnotated(
      this.parameter, T::class.java,
    )

  private val RequestParam.encodedName
    get() = value.ifBlank { null } ?: name.ifBlank { null }

  private val KProperty1<*, *>.encodedName
    get() = findAnnotation<RequestParam>()?.encodedName ?: name

  private fun Any.toArray(): List<Any?> =
    (0 until Array.getLength(this)).map { index ->
      Array.get(this, index)
    }

  override fun resolve(
      argument: Any?,
      parameter: MethodParameter,
      requestValues: HttpRequestValues.Builder,
  ): Boolean {
    if (parameter.isCandidate<RequestParam>() && argument != null) {
      val type = argument.javaClass.kotlin

      cache.computeIfAbsent(type) { type.memberProperties }.forEach { prop ->
        prop.getter.call(argument)?.let { value ->
          when (value) {
            is Iterable<*> -> {
              value.filterNotNull().map(Any::toString)
            }

            is Array,
            is IntArray, is LongArray, is DoubleArray, is FloatArray,
            is BooleanArray, is ByteArray, is CharArray, is ShortArray,
              -> {
              value.toArray().filterNotNull().map(Any::toString)
            }

            else -> {
              listOf(value.toString())
            }
          }.forEach {
            requestValues.addRequestParameter(prop.encodedName, it)
          }
        }
      }

      return true
    }

    return false
  }
}
