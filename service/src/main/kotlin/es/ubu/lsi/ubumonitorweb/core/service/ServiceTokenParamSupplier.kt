package es.ubu.lsi.ubumonitorweb.core.service

import es.ubu.lsi.ubumonitorweb.core.security.MoodleAuthenticationToken
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
class ServiceTokenParamSupplier : ServiceParamSupplier<String?> {
  override fun invoke(
    method: Method,
    params: Map<MethodParameter, Any?>,
  ): String? {
    val authentication =
      SecurityContextHolder.getContext().authentication as? MoodleAuthenticationToken

    return authentication?.credentials?.token
  }
}
