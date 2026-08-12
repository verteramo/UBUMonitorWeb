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
 * Controlador para normalizar y renderizar errores.
 * Convierte cualquier error en un objeto [ProblemDetail] normalizado.
 *
 * Objeto inicial:
 *   ErrorAttributes
 *   {
 *     "timestamp": "2026-07-31T19:08:42.919Z",
 *     "status": 403,
 *     "error": "Forbidden",
 *     "message": "Forbidden",
 *     "path": "/auth/logout"
 *   }
 *
 * Objeto final:
 *   ProblemDetail
 *   {
 *     "detail": "Forbidden",
 *     "instance": "/auth/logout",
 *     "status": 403,
 *     "title": "Forbidden"
 *   }
 *
 * @param mapper Mapper para extraer los atributos del error.
 * @param errorAttributes Atributos del error ocurrido.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@RestController
class ErrorMapperController(
  private val mapper: ObjectMapper,
  private val errorAttributes: ErrorAttributes,
) : ErrorController {
  /** Estructura para extraer el mapa de atributos del error ocurrido. */
  data class Attributes(
    val status: Int,
    val error: String?,
    val message: String?,
    val path: URI,
  )

  /**
   * Handler del endpoint `/error`.
   *
   * @param request Inyección de la solicitud.
   * @return Objeto de tipo [ProblemDetail] normalizado.
   */
  @RequestMapping("/error")
  fun handleError(request: HttpServletRequest): ProblemDetail {
    // Extracción de los atributos
    val attributes =
      mapper.convertValue<Attributes>(
        errorAttributes.getErrorAttributes(
          ServletWebRequest(request),
          ErrorAttributeOptions.of(
            ErrorAttributeOptions.Include.STATUS,
            ErrorAttributeOptions.Include.ERROR,
            ErrorAttributeOptions.Include.MESSAGE,
            ErrorAttributeOptions.Include.PATH,
          ),
        ),
      )

    // Construcción y devolución del error normalizado
    return ProblemDetail.forStatus(attributes.status).apply {
      title = attributes.error
      detail = attributes.message
      instance = attributes.path
    }
  }
}
