/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.provider

import es.ubu.lsi.ubumonitorweb.core.client.PropertyProvider
import es.ubu.lsi.ubumonitorweb.core.locale.Message
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

/**
 * Proveedor que resuelve el host, bien desde el contexto de la sesión o desde el header.
 */
@Component
class HostProvider(
  private val request: HttpServletRequest,
) : PropertyProvider.Static<String?>() {
  /**
   * Nombre del header que contiene el host.
   */
  private val header = "Moodle-Host"

  /**
   * Host presente en el header, si existe y no es una cadena vacía.
   */
  private val host: String?
    get() = request.getHeader(header)?.takeIf { it.isNotBlank() }

  /**
   * Intenta obtener el host desde el contexto de sesión, que será nulo si se accede a un endpoint público,
   * en tal caso se obtiene desde el header, obligatorio en endpoints públicos.
   *
   * @return Host de la plataforma Moodle.
   */
  override fun invoke(): String? = user?.siteUrl ?: host ?: throw Message.ERROR_HTTP_MISSING_HEADER(HttpStatus.BAD_REQUEST, header)
}
