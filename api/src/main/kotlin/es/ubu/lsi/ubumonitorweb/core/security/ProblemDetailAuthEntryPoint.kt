package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.net.URI

/**
 * Punto de entrada de autenticación que intercepta fallos de seguridad y emite una respuesta
 * estructurada bajo el estándar RFC 7807 (`ProblemDetail`).
 *
 * https://www.rfc-editor.org/info/rfc9457/
 *
 * @property jsonMapper Mapper JSON.
 */
@Component
class ProblemDetailAuthEntryPoint(
  private val jsonMapper: ObjectMapper,
) : AuthenticationEntryPoint {
  /**
   * Escribe el error de autenticación en la respuesta HTTP.
   *
   * @param request Petición HTTP.
   * @param response Respuesta HTTP.
   * @param authException Excepción de autenticación capturada.
   */
  override fun commence(
    request: HttpServletRequest,
    response: HttpServletResponse,
    authException: AuthenticationException,
  ) {
    HttpStatus.UNAUTHORIZED.let { status ->
      response.status = status.value()
      response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE

      val problemDetail =
        ProblemDetail.forStatusAndDetail(status, authException.message).apply {
          title = status.reasonPhrase
          instance = URI.create(request.requestURI)
        }

      jsonMapper.writeValue(response.writer, problemDetail)
    }
  }
}
