package es.ubu.lsi.ubumonitorweb.core.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component

// @Profile("dev")
// @Component
class ClientLoggerInterceptor : ClientHttpRequestInterceptor {
  private val logger = KotlinLogging.logger {}

  override fun intercept(
    request: org.springframework.http.HttpRequest,
    body: ByteArray,
    execution: ClientHttpRequestExecution,
  ): ClientHttpResponse {
    logger.debug {
      """
      |
      |${body.decodeToString()}
      """.trimMargin()
    }

    val response = execution.execute(request, body)

    logger.debug {
      """
      |
      |${response.body.readAllBytes().decodeToString()}
      """.trimMargin()
    }

    return response
  }
}
