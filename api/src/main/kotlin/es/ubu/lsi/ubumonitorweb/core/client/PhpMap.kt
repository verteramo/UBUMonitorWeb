/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.client

/**
 * Anotación que indica cómo serializar un mapa de colecciones para un servidor PHP.
 *
 * Transforma un Map donde los valores son colecciones utilizando la notación
 * de corchetes anidados. Genera parámetros del tipo `paramName[mapKey][index]`.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PhpMap(
  val name: String = "",
)
