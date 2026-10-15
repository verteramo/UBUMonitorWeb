/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.provider

import es.ubu.lsi.ubumonitorweb.core.client.PropertyProvider
import es.ubu.lsi.ubumonitorweb.core.locale.Message
import es.ubu.lsi.ubumonitorweb.core.security.SecurityProperties
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher

/**
 * Proveedor que resuelve la URL del sitio, bien desde el contexto de la sesión o desde el header.
 */
@Component
class SiteUrlProvider(
  private val request: HttpServletRequest,
  private val properties: SecurityProperties,
) : PropertyProvider.Static<String?>() {
  /**
   * Matcher de rutas.
   */
  private val pathMatcher = AntPathMatcher()

  /**
   * Nombre del header que contiene la URL del sitio.
   */
  private val header = "Moodle-Site"

  /**
   * Determina si la ruta actual es pública o privada.
   */
  private val isPublicRoute: Boolean
    get() = properties.publicRoutes.any { pathMatcher.match(it, request.requestURI) }

  /**
   * URL del sitio presente en el header, si existe y no es una cadena vacía.
   */
  private val siteUrl: String?
    get() = request.getHeader(header)?.takeIf { it.isNotBlank() }

  /**
   * Si se trata de una ruta pública, se intenta obtener el site desde el header,
   * en caso contrario, desde los detalles del usuario autenticado.
   *
   * @return Sitio Moodle.
   */
  override fun invoke(): String? =
    siteUrl.takeIf { isPublicRoute } ?: user?.siteUrl
      ?: throw Message.ERROR_HTTP_MISSING_HEADER(HttpStatus.BAD_REQUEST, header)
}
