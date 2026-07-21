package es.ubu.lsi.ubumonitorweb.moodle.supplier

import es.ubu.lsi.ubumonitorweb.core.http.ServicePropertySupplier
import es.ubu.lsi.ubumonitorweb.feature.token.MoodleToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component("tokenPropertySupplier")
class TokenPropertySupplier : ServicePropertySupplier<MoodleToken?> {

  override fun ServicePropertySupplier.Context.get(): MoodleToken? {
    return SecurityContextHolder.getContext().authentication?.principal as? MoodleToken
  }
}
