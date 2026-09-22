/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.provider

import es.ubu.lsi.ubumonitorweb.core.client.PropertyProvider
import org.springframework.stereotype.Component

/**
 * Proveedor que extrae y entrega la cookie de sesión de Moodle desde el contexto de seguridad.
 */
@Component
class CookieProvider : PropertyProvider.Static<String?>() {
  override fun invoke(): String? = sessionContext?.credentials?.sessionCookie
}
