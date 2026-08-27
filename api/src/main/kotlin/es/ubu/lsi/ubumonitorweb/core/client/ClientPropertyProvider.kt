/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import org.springframework.core.MethodParameter
import java.lang.reflect.Method

/**
 * Contrato de los beans que resuelven propiedades de configuración de los clientes.
 */
fun interface ClientPropertyProvider<out T> {
  /**
   * Contexto de los providers.
   *
   * @param method Método del cliente.
   * @param params Mapa de parámetros con sus valores correspondientes.
   */
  data class Context(
    val method: Method,
    val params: Map<MethodParameter, Any?>,
  )

  /**
   * Función proveedora.
   *
   * @param context Contexto de la llamada al cliente.
   * @return Valor resuelto que se lleva a la configuración del cliente.
   */
  fun invoke(context: Context): T
}
