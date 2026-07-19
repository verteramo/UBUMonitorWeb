package es.ubu.lsi.ubumonitorweb.core.rest

/**
 * Anotación para servicios en la que se debe indicar el perfil que contiene sus propiedades en el
 * fichero de propiedades de la aplicación. Por ejemplo:
 *
 * ```yaml
 * moodle:
 *   profiles:
 *     profile1:
 *       ...
 * ```
 *
 * ```kotlin
 * @MoodleService("profile1")
 * ```
 *
 * @param profile Nombre del perfil de la configuración.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Target(AnnotationTarget.CLASS)
annotation class MoodleService(val profile: String = MoodleProperties.DEFAULT_PROFILE)
