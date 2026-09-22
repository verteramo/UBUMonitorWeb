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
 * Proveedor que obtiene el host desde la cabecera `Moodle-Host.
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
   * Host presente en la cabecera.
   */
  private val host: String?
    get() = request.getHeader(header)?.takeIf { it.isNotBlank() }

  /**
   * Invocador del provider.
   */
  override fun invoke(): String? =
    host ?: sessionContext?.principal?.siteUrl ?: throw Message.ERROR_HTTP_MISSING_HEADER(
      HttpStatus.BAD_REQUEST,
      header,
    )
}
