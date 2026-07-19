package es.ubu.lsi.ubumonitorweb.core.interceptor

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component

/**
 * Interceptor que loguea el contenido de las solicitudes y respuestas de los servicios HTTP.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
@Profile("dev")
class LoggingServiceInterceptor : ClientHttpRequestInterceptor {

  /** Logger */
  private val logger = KotlinLogging.logger {}

  /**
   * Convierte el mapa de headers a una lista de cadenas compuestas por el header y sus valores.
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
