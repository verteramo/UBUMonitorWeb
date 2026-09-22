/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.domain.Credentials
import es.ubu.lsi.ubumonitorweb.domain.Principal
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import java.lang.reflect.Method

/**
 * Contrato de los beans que resuelven propiedades de configuración.
 */
sealed class PropertyProvider<out T> {
  /**
   * Contrato para beans que no dependen del método HttpExchange que se ejecuta,
   * incluso podría usarse este bean fuera de un método HttpExchange.
   */
  abstract class Static<out T> : PropertyProvider<T>() {
    abstract fun invoke(): T
  }

  /**
   * Contrato para beans que se llaman exclusivamente desde un método HttpExchange,
   * por ejemplo, el FunctionProvider, ya que depende del nombre del método y su clase.
   */
  abstract class MethodAware<out T> : PropertyProvider<T>() {
    data class MethodContext(
      val method: Method,
      val params: Map<MethodParameter, Any?>,
    )

    abstract fun invoke(methodContext: MethodContext): T
  }

  interface SessionContext {
    val principal: Principal?
    val credentials: Credentials?
  }

  /**
   * Contexto de la sesión actual, si existe, null en caso contrario.
   */
  protected val sessionContext
    get() =
      SecurityContextHolder.getContext().authentication?.let {
        object : SessionContext {
          override val principal get() = it.principal as? Principal
          override val credentials get() = it.credentials as? Credentials
        }
      }
}
