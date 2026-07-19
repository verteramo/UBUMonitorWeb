package es.ubu.lsi.ubumonitorweb.core.rest

/**
 * Anotación para servicios en la que se debe indicar el bean que contiene sus
 * propiedades, en este momento hay 2 bloques de configuración: `moodle.auth` y
 * `moodle.rest`.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Target(AnnotationTarget.CLASS)
annotation class MoodleService(val profile: String = "")
