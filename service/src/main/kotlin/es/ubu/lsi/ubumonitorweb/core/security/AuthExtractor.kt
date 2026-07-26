package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication

/**
 * Contrato para clases con la capacidad de extraer objetos [Authentication] desde la solicitud.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
fun interface AuthExtractor<A : Authentication> {

  /**
   * Extrae un objeto [Authentication] desde la solicitud, si está presente.
   *
   * @param request Solicitud.
   * @return Objeto [Authentication] si está presente, `null` en caso contrario.
   */
  fun extract(request: HttpServletRequest): A?
}
