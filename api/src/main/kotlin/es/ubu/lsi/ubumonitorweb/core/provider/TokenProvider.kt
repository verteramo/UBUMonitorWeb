/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.provider

import es.ubu.lsi.ubumonitorweb.core.client.PropertyProvider
import es.ubu.lsi.ubumonitorweb.domain.Credentials
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 * Proveedor que extrae y entrega el token necesario para el parámetro `wstoken` desde el
 * contexto de seguridad.
 */
@Component
class TokenProvider : PropertyProvider.Static<String?>() {
  /**
   * Invocador del provider.
   */
  override fun invoke(): String? = sessionContext?.credentials?.token
}
