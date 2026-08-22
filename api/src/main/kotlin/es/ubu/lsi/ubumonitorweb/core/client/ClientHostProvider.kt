package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.locale.Message
import es.ubu.lsi.ubumonitorweb.core.moodle.SiteInfo
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 * Proveedor que obtiene el host desde la cabecera `Moodle-Host`, si la cabecera está
 * vacía o no existe, busca el host en el usuario autenticado del contexto de seguridad.
 *
 * @author Marcelo Verteramo Pérsico
 */
@Component
class ClientHostProvider(
  private val request: HttpServletRequest,
) : ClientPropertyProvider<String?> {
  /** Nombre del header que contiene el host. */
  private val header = "Moodle-Host"

  /** Host presente en la cabecera. */
  private val host: String?
    get() = request.getHeader(header)?.takeIf { it.isNotBlank() }

  /** Usuario presente en el contexto de seguridad. */
  private val siteInfo: SiteInfo?
    get() = SecurityContextHolder.getContext().authentication?.principal as? SiteInfo

  /** Invocador del provider */
  override fun invoke(context: ClientPropertyProvider.Context): String? =
    siteInfo?.siteurl ?: host ?: throw Message.ERROR_HTTP_MISSING_HEADER(
      HttpStatus.BAD_REQUEST,
      header,
    )
}
