package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.security.MoodlePrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 * Proveedor que obtiene el host desde la propiedad `siteurl` del usuario logueado.
 * Este proveedor se utiliza en servicios REST, ya que para utilizarlos debe existir un
 * usuario logueado.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class ClientHostSessionProvider : ClientPropertyProvider<String?> {
  override fun invoke(context: ClientPropertyProvider.Context): String? {
    val principal = SecurityContextHolder.getContext().authentication?.principal as? MoodlePrincipal
    return principal?.siteurl
  }
}
