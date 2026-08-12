package es.ubu.lsi.ubumonitorweb.core.client

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

/**
 * Proveedor que obtiene el host desde la cabecera Moodle-Host.
 * Este proveedor se utiliza en servicios de autenticación, ya que aún no hay un usuario logueado y
 * el host se obtiene desde algún sitio de la solicitud entrante.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class ClientHostHeaderProvider(
  private val request: HttpServletRequest,
) : ClientPropertyProvider<String?> {
  override fun invoke(context: ClientPropertyProvider.Context): String? = request.getHeader("Moodle-Host")
}
