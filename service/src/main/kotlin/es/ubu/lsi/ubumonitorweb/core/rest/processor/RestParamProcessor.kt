package es.ubu.lsi.ubumonitorweb.core.rest.processor

import es.ubu.lsi.ubumonitorweb.core.base.SnakeCaseResolver
import es.ubu.lsi.ubumonitorweb.core.base.annotations
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.invoker.HttpRequestValues
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Anotación que permite incluir parámetros en las solicitudes salientes de
 * servicios [HttpExchange], el valor de los parámetros se obtiene a partir de
 * un Resolver indicado en el parámetro `resolver` de la anotación, este
 * resolver debe responder a la firma `(Method) -> String`, consultar
 * [SnakeCaseResolver] como referencia. Ejemplo de utilización:
 *
 * ```kotlin
 * @HttpExchange
 * @RestParam("param1", MyResolver1::class)
 * @RestParam("param2", MyResolver2::class)
 * @RestParam("param3", MyResolver3::class)
 * interface ServicioRest {...}
 * ```
 *
 * Este código añadiría los parámetros a la solicitud:
 * ```
 * value1 = MyResolver1.invoke(method)
 * value2 = MyResolver2.invoke(method)
 * value3 = MyResolver3.invoke(method)
 * param1=value1&param2=value2&param3=value3
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Repeatable
annotation class RestParam(
    val name: String,
    val resolver: KClass<out (Method) -> String>,
)

/**
 * Procesador de la anotación que resuelve los valores de los parámetros y los
 * inyecta en la solicitud saliente.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class RestParamProcessor : HttpRequestValues.Processor {

  override fun process(
      method: Method,
      parameters: Array<out MethodParameter>,
      arguments: Array<out Any?>,
      requestValues: HttpRequestValues.Builder,
  ) {

    // Recorrido por todas las anotaciones disponibles (son @Repeatable)
    method.annotations<RestParam>()

        // Se crea un mapa Map<String, String>
        .associate {
          val resolver = it.resolver.run { objectInstance ?: createInstance() }
          it.name to resolver(method)
        }

        // El mapa se pasa como BiConsumer<String, String>
        // compatible con addRequestParameter del builder
        .forEach(requestValues::addRequestParameter)
  }
}
