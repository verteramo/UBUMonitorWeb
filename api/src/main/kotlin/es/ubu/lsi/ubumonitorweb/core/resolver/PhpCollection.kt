/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.resolver

/**
 * Anotación que indica cómo serializar una colección para servidores PHP.
 *
 * ```
 * @PhpCollection(name = "status")
 * listOf("activo", "pendiente") --> status[0]=activo&status[1]=pendiente
 * ```
 *
 * @property name Nombre explícito del parámetro en la petición HTTP.
 *
 * @see PhpCollectionArgumentResolver
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PhpCollection(
  val name: String = "",
)
