package es.ubu.lsi.ubumonitorweb.core.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.service.annotation.HttpExchange

/**
 * Interceptor que loguea el contenido de las solicitudes y respuestas de los clientes
 * [HttpExchange].
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
@Profile("dev")
class ClientLoggingInterceptor : ClientHttpRequestInterceptor {
  /** Logger */
  private val logger = KotlinLogging.logger {}

  /**
   * Convierte el mapa de headers en una cadena compuesta por los headers y sus valores en
   * el formato descrito en: https://datatracker.ietf.org/doc/html/rfc2616#section-4.2.
   *
   * ```http
   * message-header = field-name ":" [ field-value ]
   * ```
   *
   * @return Cadena de headers.
   */
  private fun HttpHeaders.joinToString(separator: CharSequence): String =
    toSingleValueMap().map { "${it.key}: ${it.value}" }.joinToString(separator)

  /**
   * Interceptor.
   *
   * @param request Solicitud.
   * @param body Cuerpo de la solicitud.
   * @param execution Ejecutor para delegar la solicitud a la cadena de intercetores.
   * @return Respuesta final.
   */
  override fun intercept(
    request: HttpRequest,
    body: ByteArray,
    execution: ClientHttpRequestExecution,
  ): ClientHttpResponse {
    // Logueo de la solicitud.
    logger.debug {
      """
      |
      |===HTTP Request===
      |${request.method} ${request.uri}
      |${request.headers.joinToString("\n")}
      |
      |${body.decodeToString()}
      """.trimMargin()
    }

    // Envío de la solicitud a través de la cadena de interceptores.
    val response = execution.execute(request, body)

    // Logueo de la respuesta final
    logger.debug {
      """
      |
      |===HTTP Response===
      |${response.statusCode}
      |${response.headers.joinToString("\n")}
      |
      |${response.body.readAllBytes().decodeToString()}
      """.trimMargin()
    }

    return response
  }
}
