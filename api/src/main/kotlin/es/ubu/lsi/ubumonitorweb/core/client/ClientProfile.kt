/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

/**
 * Anotación para clientes en la que se indica el perfil que contiene sus propiedades y metadatos
 * en el fichero de configuración de la aplicación. Por ejemplo:
 *
 * ```yaml
 * clients:
 *   profiles:
 *     my-profile:
 *       host: ...
 *       headers:
 *         Accept-Language: en
 *         Content-Type: application/x-www-form-urlencoded
 * ```
 *
 * ```kotlin
 * @ClientProfile("my-profile")
 * ```
 */
@Target(AnnotationTarget.CLASS)
annotation class ClientProfile(
  val profile: String = "",
)
