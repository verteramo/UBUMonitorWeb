/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.provider

import es.ubu.lsi.ubumonitorweb.core.client.PropertyProvider
import org.springframework.stereotype.Component

/**
 * Proveedor que extrae y entrega el token necesario para el parámetro `wstoken` desde el contexto de la sesión.
 */
@Component
class TokenProvider : PropertyProvider.Static<String?>() {
  /**
   * Obtiene el token de los webservices de Moodle desde el keychain del contexto de la sesión.
   *
   * @return Token de los webservices de Moodle.
   */
  override fun invoke(): String? = keychain?.token
}
