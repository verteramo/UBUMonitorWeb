package es.ubu.lsi.ubumonitorweb.core.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.webmvc.error.ErrorAttributes
import org.springframework.boot.webmvc.error.ErrorController
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletWebRequest
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.convertValue
import java.net.URI

/**
 * ErrorAttributes
 * {
 *   "timestamp": "2026-07-31T19:08:42.919Z",
 *   "status": 403,
 *   "error": "Forbidden",
 *   "message": "Forbidden",
 *   "path": "/auth/logout"
 * }
 *
 * ProblemDetail
 * {
 *   "detail": "Forbidden",
 *   "instance": "/auth/logout",
 *   "status": 403,
 *   "title": "Forbidden"
 * }
 */
@RestController
class ErrorMapperController(
  private val mapper: ObjectMapper,
  private val errorAttributes: ErrorAttributes,
) : ErrorController {
  data class Attributes(
    val status: Int,
    val error: String?,
    val message: String?,
    val path: URI,
  )

  @RequestMapping("/error")
  fun handleError(request: HttpServletRequest): ProblemDetail {
    val attributes =
      mapper.convertValue<Attributes>(
        errorAttributes.getErrorAttributes(
          ServletWebRequest(request),
          ErrorAttributeOptions.defaults().including(
            ErrorAttributeOptions.Include.STATUS,
            ErrorAttributeOptions.Include.ERROR,
            ErrorAttributeOptions.Include.MESSAGE,
            ErrorAttributeOptions.Include.PATH,
          ),
        ),
      )

    return ProblemDetail.forStatus(attributes.status).apply {
      title = attributes.error
      detail = attributes.message
      instance = attributes.path
    }
  }
}
