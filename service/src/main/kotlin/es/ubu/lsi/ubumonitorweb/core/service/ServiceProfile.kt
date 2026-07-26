package es.ubu.lsi.ubumonitorweb.core.service

/**
 * Anotación para servicios en la que se indica el perfil que contiene sus propiedades dentro del
 * fichero de propiedades de la aplicación. Por ejemplo:
 *
 * ```yaml
 * service:
 *   profiles:
 *     my-profile:
 *       host-header: X-Host
 *       headers:
 *         Accept-Language: en
 *         Content-Type: application/x-www-form-urlencoded
 * ```
 *
 * ```kotlin
 * @ServiceProfile("my-profile")
 * ```
 *
 * @param profile Nombre del perfil de la configuración.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Target(AnnotationTarget.CLASS)
annotation class ServiceProfile(val profile: String)
