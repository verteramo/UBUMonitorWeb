/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

import org.springframework.web.service.annotation.HttpExchange

/**
 * Vincula una interfaz o método de cliente HTTP declarativo con un perfil de configuración.
 *
 * Configuración en YAML:
 * ```yaml
 * clients:
 *   profiles:
 *     my-profile:
 *       host: { ... }
 *       headers:
 *         Accept-Language: en
 *         Content-Type: application/x-www-form-urlencoded
 * ```
 *
 * Uso en clientes:
 * ```kotlin
 * @Client("my-profile")
 * interface MyClient
 * ```
 *
 * Anotada con [HttpExchange] para poder ser interceptada por interceptors, resolvers y processors.
 * @see ClientProcessor
 *
 * @property profile Nombre del perfil.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@HttpExchange
annotation class Client(
  val profile: String = "",
)
