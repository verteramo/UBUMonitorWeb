package es.ubu.lsi.ubumonitorweb.core.rest.interceptor

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component

/**
 * Interceptor que loguea el contenido de las solicitudes y respuestas de los
 * servicios HTTP.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
@Profile("dev")
class LoggingServiceInterceptor : ClientHttpRequestInterceptor {

  private val logger = KotlinLogging.logger {}

  private fun headersAsString(headers: HttpHeaders) =
    headers.toSingleValueMap()
        .map { "${it.key}: ${it.value}" }
        .joinToString("\n")

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
      |${headersAsString(request.headers)}
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
      |${headersAsString(response.headers)}
      |
      |${response.body.readAllBytes().decodeToString()}
    """.trimMargin()
    }

    return response
  }
}
