package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.http.HttpServletResponse

/**
 * Contrato para clases con la capacidad de responder con objetos de autenticación.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
fun interface AuthResponder<T : Any> {

  /**
   * Responde con un objeto de autenticación en la respuesta.
   *
   * @param response Respuesta.
   * @param payload Objeto.
   * @return Cuerpo, si existe.
   */
  fun respond(response: HttpServletResponse, payload: T): Any?
}
