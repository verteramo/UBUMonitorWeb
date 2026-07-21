package es.ubu.lsi.ubumonitorweb.core.http

/**
 * Anotación para servicios en la que se debe indicar el perfil que contiene sus propiedades en el
 * fichero de propiedades de la aplicación. Por ejemplo:
 *
 * ```yaml
 * services:
 *   profiles:
 *     profile1:
 *       host-header: X-Host
 *       headers:
 *         Accept-Language: en
 *         Content-Type: application/x-www-form-urlencoded
 * ```
 *
 * ```kotlin
 * @ServiceProfile("profile1")
 * ```
 *
 * @param profile Nombre del perfil de la configuración.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Target(AnnotationTarget.CLASS)
annotation class ServiceProfile(val profile: String = ServiceProperties.DEFAULT_PROFILE)
