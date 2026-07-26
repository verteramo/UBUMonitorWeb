package es.ubu.lsi.ubumonitorweb.core.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.Authentication

/**
 * Contrato para estrategias de autenticación.
 * Existen diversas maneras de transportar los datos de autenticación en mensajes HTTP, podría ser
 * en el cuerpo, en las cabeceras `Authorization` o `Set-Cookie`, etc., estas estrategias pueden ser
 * implementadas a partir de esta clase e indicar simplemente cómo se obtienen e inyectan los datos
 * de autenticación en los mensajes HTTP.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
abstract class AuthStrategy<T : Any>(
    private val serializer: AuthCodec<T>,
) : AuthExtractor<AuthStrategy.Wrapper<T>>, AuthResponder<T> {

  /**
   * Implementación de [Authentication].
   *
   * Envoltura para el objeto Principal.
   *
   * @param payload Principal.
   */
  class Wrapper<T>(
      private val payload: T,
  ) : AbstractAuthenticationToken(emptyList()) {

    init {
      /** El objeto se considera legítimamente autenticado desde su creación. */
      isAuthenticated = true
    }

    /**
     * Obtiene las credenciales, nulas por seguridad por diseño.
     *
     * @return Credenciales.
     */
    override fun getCredentials() = null

    /**
     * Obtiene el objeto Principal.
     *
     * @return Principal.
     */
    override fun getPrincipal() = payload
  }

  /** Datos crudos de autenticación presentes en la solicitud. */
  protected abstract val HttpServletRequest.data: String?

  /**
   * Inyecta los datos crudos de autenticación en la respuesta.
   *
   * @param data Datos crudos de autenticación.
   */
  protected abstract fun HttpServletResponse.doRespond(data: String): Any?

  /**
   * Contrato de lectura que realiza la decodificación.
   *
   * @param request Solicitud.
   * @return Principal envuelto en la implementación de [Authentication].
   */
  override fun extract(request: HttpServletRequest): Wrapper<T>? {
    return request.data?.let {
      runCatching { serializer.decode(it) }.getOrNull()
    }?.let { Wrapper(it) }
  }

  /**
   * Contrato para la escritura que realiza la codificación.
   *
   * @param response Respuesta.
   * @param payload Principal.
   */
  override fun respond(response: HttpServletResponse, payload: T): Any? {
    return response.doRespond(serializer.encode(payload))
  }
}
