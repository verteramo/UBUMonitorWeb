package es.ubu.lsi.ubumonitorweb.core.service

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
 * Interceptor que loguea el contenido de las solicitudes y respuestas de los servicios
 * [HttpExchange].
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
@Profile("dev")
class ServiceLoggingInterceptor : ClientHttpRequestInterceptor {

  /** Logger */
  private val logger = KotlinLogging.logger {}

  /**
   * Convierte el mapa de headers en una lista de cadenas compuestas por el header y sus valores en
   * el formato descrito en: https://datatracker.ietf.org/doc/html/rfc2616#section-4.2.
   *
   * ```http
   * message-header = field-name ":" [ field-value ]
   * ```
   *
   * @return Lista de headers con valores.
   */
  private fun HttpHeaders.toList(): List<String> {
    return toSingleValueMap().map { "${it.key}: ${it.value}" }
  }

  override fun intercept(
      request: HttpRequest,
      body: ByteArray,
      execution: ClientHttpRequestExecution,
  ): ClientHttpResponse {
    logger.debug {
      """
      |
      |===HTTP Request===
      |${request.method} ${request.uri}
      |${request.headers.toList().joinToString("\n")}
      |
      |${body.decodeToString()}
    """.trimMargin()
    }

    val response = execution.execute(request, body)

    logger.debug {
      """
      |
      |===HTTP Response===
      |${response.statusCode}
      |${response.headers.toList().joinToString("\n")}
      |
      |${response.body.readAllBytes().decodeToString()}
    """.trimMargin()
    }

    return response
  }
}
