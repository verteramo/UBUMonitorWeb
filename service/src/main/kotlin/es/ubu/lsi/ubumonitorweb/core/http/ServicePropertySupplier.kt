package es.ubu.lsi.ubumonitorweb.core.http

import es.ubu.lsi.ubumonitorweb.moodle.supplier.FunctionPropertySupplier
import org.springframework.core.MethodParameter
import org.springframework.web.service.invoker.HttpRequestValues
import java.lang.reflect.Method

/**
 * Proveedor de valor para propiedades de servicios. Permite crear proveedores como
 * [FunctionPropertySupplier]. Pone el contexto del [HttpRequestValues.Processor] a disposición del
 * proveedor.
 */
fun interface ServicePropertySupplier<out T> {

  /**
   * Context
   */
  data class Context(
      val method: Method,
      val params: Map<MethodParameter, Any?>,
  )

  fun Context.get(): T
}
