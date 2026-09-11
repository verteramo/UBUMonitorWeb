/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.security.Credentials
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 * Proveedor que extrae y entrega la cookie de sesión de Moodle desde el contexto de seguridad.
 */
@Component
class ClientCookieProvider : ClientPropertyProvider<String?> {
  /**
   * Credenciales presentes en el contexto de seguridad.
   */
  private val credentials: Credentials?
    get() = SecurityContextHolder.getContext().authentication?.credentials as? Credentials

  /**
   * Invocador del provider.
   */
  override fun invoke(context: ClientPropertyProvider.Context): String? = credentials?.sessionCookie?.substringAfter("=")
}
