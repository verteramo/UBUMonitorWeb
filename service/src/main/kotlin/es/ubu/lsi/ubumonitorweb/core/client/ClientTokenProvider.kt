package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.security.MoodleCredentials
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 * Proveedor que extrae y entrega el token necesario para el parámetro `wstoken` desde el
 * contexto de seguridad.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class ClientTokenProvider : ClientPropertyProvider<String?> {
  override fun invoke(context: ClientPropertyProvider.Context): String? {
    val credentials =
      SecurityContextHolder.getContext().authentication?.credentials as? MoodleCredentials
    return credentials?.token
  }
}
