/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.system

import org.apache.hc.client5.http.cookie.Cookie
import org.apache.hc.client5.http.cookie.CookieStore

/**
 * Utilidades y funciones de extensión generales para el procesamiento de peticiones y respuestas HTTP.
 */
object HttpExtensions {
  /**
   * Filtra las cookies cuyo nombre comienza con el prefijo indicado.
   *
   * @param name Prefijo del nombre de la cookie.
   * @param ignoreCase Indica si se deben ignorar mayúsculas y minúsculas (por defecto `true`).
   * @return Lista con las cookies que coinciden.
   */
  fun CookieStore.filter(
    name: String,
    ignoreCase: Boolean = true,
  ): List<Cookie> = cookies.filter { it.name.startsWith(name, ignoreCase) }

  /**
   * Obtiene la primera cookie cuyo nombre comienza con el prefijo indicado.
   *
   * @param name Prefijo del nombre de la cookie.
   * @param ignoreCase Indica si se deben ignorar mayúsculas y minúsculas (por defecto `true`).
   * @return Cookie encontrada o nula si no existe.
   */
  fun CookieStore.first(
    name: String,
    ignoreCase: Boolean = true,
  ): Cookie? = cookies.firstOrNull { it.name.startsWith(name, ignoreCase) }
}
