/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import es.ubu.lsi.ubumonitorweb.domain.Keychain
import es.ubu.lsi.ubumonitorweb.domain.User
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import java.lang.reflect.Method

/**
 * Contrato de los beans que resuelven propiedades de configuración.
 *
 * @see es.ubu.lsi.ubumonitorweb.core.provider.ArgsProvider
 * @see es.ubu.lsi.ubumonitorweb.core.provider.FunctionProvider
 * @see es.ubu.lsi.ubumonitorweb.core.provider.HostProvider
 * @see es.ubu.lsi.ubumonitorweb.core.provider.TokenProvider
 */
sealed class PropertyProvider<out T> {
  /**
   * Contrato para beans que no dependen del método HttpExchange que se ejecuta.
   */
  abstract class Static<out T> : PropertyProvider<T>() {
    /**
     * Invocador del provider.
     *
     * @return Valor resuelto.
     */
    abstract fun invoke(): T
  }

  /**
   * Contrato para beans que se llaman exclusivamente desde un método HttpExchange,
   * por ejemplo, el FunctionProvider, ya que depende del nombre del método y su clase.
   *
   * @see es.ubu.lsi.ubumonitorweb.core.provider.FunctionProvider
   */
  abstract class MethodAware<out T> : PropertyProvider<T>() {
    /**
     * Contexto del método cliente HTTP.
     *
     * @property method Reflexión del método.
     * @property params Reflexión de los parámetros emparejados con los argumentos de la llamada.
     */
    data class MethodContext(
      val method: Method,
      val params: Map<MethodParameter, Any?>,
    )

    /**
     * Invocador del provider.
     *
     * @param methodContext Contexto del método cliente HTTP ejecutado.
     * @return Valor resuelto.
     */
    abstract fun invoke(methodContext: MethodContext): T
  }

  /**
   * Usuario autenticado.
   */
  protected val user: User?
    get() = SecurityContextHolder.getContext().authentication?.principal as? User

  /**
   * Llavero del usuario autenticado.
   */
  protected val keychain: Keychain?
    get() = SecurityContextHolder.getContext().authentication?.credentials as? Keychain

  /**
   * Resuelve un valor determinando el invocador correcto de acuerdo con el tipo del provider.
   *
   * @param methodContext Contexto del método cliente HTTP ejecutado.
   * @return Valor resuelto.
   */
  fun resolve(methodContext: MethodAware.MethodContext): Any? =
    when (this) {
      is Static<*> -> invoke()
      is MethodAware<*> -> invoke(methodContext)
    }
}
