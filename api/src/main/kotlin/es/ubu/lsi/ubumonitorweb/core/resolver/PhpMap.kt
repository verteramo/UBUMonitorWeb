/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.resolver

/**
 * Anotación que indica cómo serializar un mapa asociativo para servidores PHP.
 *
 * ```
 * @PhpMap(name = "criteria")
 * mmapOf("email" to "admin@ubu.es") --> criteria[0][key]=email&criteria[0][value]=admin@ubu.es
 * ```
 *
 * @property name Nombre explícito del parámetro en la petición HTTP.
 * @property keyName Nombre del campo que contendrá la clave del mapa en la consulta.
 * @property valueName Nombre del campo que contendrá el valor asociado a la clave en la consulta.
 *
 * @see PhpMapArgumentResolver
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PhpMap(
  val name: String = "",
  val keyName: String = "key",
  val valueName: String = "value",
)
