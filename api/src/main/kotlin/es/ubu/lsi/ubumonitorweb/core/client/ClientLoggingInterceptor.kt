/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component

/**
 * Interceptor que loguea el contenido de las solicitudes y respuestas de los clientes HTTP.
 */
@Component
@Profile("dev")
class ClientLoggingInterceptor : ClientHttpRequestInterceptor {
  /** Logger */
  private val logger = KotlinLogging.logger {}

  /**
   * Convierte el mapa de headers en una cadena compuesta por los headers y sus valores en
   * el formato descrito en: https://datatracker.ietf.org/doc/html/rfc2616#section-4.2.
   */
  private val HttpHeaders.string: String
    get() = toSingleValueMap().map { "${it.key}: ${it.value}" }.joinToString("\n")

  /** Invocador del interceptor. */
  override fun intercept(
    request: HttpRequest,
    requestBody: ByteArray,
    requestExecution: ClientHttpRequestExecution,
  ): ClientHttpResponse {
    logger.debug {
      """
      |
      |===HTTP Request===
      |${request.method} ${request.uri}
      |${request.headers.string}
      |
      |${requestBody.decodeToString()}
      """.trimMargin()
    }

    // Envío de la solicitud a través de la cadena de interceptores
    val response = requestExecution.execute(request, requestBody)

    logger.debug {
      """
      |
      |===HTTP Response===
      |${response.statusCode}
      |${response.headers.string}
      |
      |${response.body.readAllBytes().decodeToString()}
      """.trimMargin()
    }

    return response
  }
}
