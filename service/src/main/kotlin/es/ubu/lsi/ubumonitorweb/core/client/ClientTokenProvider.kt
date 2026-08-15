package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.core.moodle.Credentials
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 * Proveedor que extrae y entrega el token necesario para el parámetro `wstoken` desde el
 * contexto de seguridad.
 *
 * @author Marcelo Verteramo Pérsico
 */
@Component
class ClientTokenProvider : ClientPropertyProvider<String?> {
  /** Credenciales presentes en el contexto de seguridad. */
  private val credentials: Credentials?
    get() = SecurityContextHolder.getContext().authentication?.credentials as? Credentials

  /** Invocador del provider. */
  override fun invoke(context: ClientPropertyProvider.Context): String? = credentials?.token
}
