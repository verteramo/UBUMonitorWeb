package es.ubu.lsi.ubumonitorweb.core.moodle

import es.ubu.lsi.ubumonitorweb.feature.service.MoodleToken
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.lang.reflect.Method

/**
 * Proveedor para obtener el token de Moodle desde el contexto de seguridad.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class TokenParameterSupplier : (Method, Map<MethodParameter, Any?>) -> MoodleToken? {

  override fun invoke(method: Method, params: Map<MethodParameter, Any?>): MoodleToken? {
    return SecurityContextHolder.getContext().authentication?.principal as? MoodleToken
  }
}
