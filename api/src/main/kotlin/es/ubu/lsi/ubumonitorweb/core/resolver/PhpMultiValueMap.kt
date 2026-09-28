/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.resolver

/**
 * Anotación que indica cómo serializar un mapa de colecciones para servidores PHP.
 *
 * ```
 * @PhpMultiValueMap(name = "filters")
 * mapOf("roles" to listOf(3, 5)) --> filters[roles][0]=3&filters[roles][1]=5
 * ```
 *
 * @property name Nombre explícito del parámetro en la petición HTTP.
 *
 * @see PhpMultiValueMapArgumentResolver
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PhpMultiValueMap(
  val name: String = "",
)
