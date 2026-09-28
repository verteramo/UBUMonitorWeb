/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.system

import org.intellij.lang.annotations.Language
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.springframework.http.ResponseEntity
import java.io.InputStream
import java.nio.charset.Charset
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Objeto de utilidad que centraliza las funciones de extensión encargadas de extraer
 * información estructural y de estado (cookies, tokens, mensajes de error y zonas horarias)
 * analizando el HTML de las respuestas de Moodle mediante Jsoup y expresiones regulares.
 */
object ScrapingExtensions {
  /**
   * Busca un patrón Regex en la cadena.
   *
   * @receiver Cadena de texto base.
   * @param pattern Expresión regular a buscar.
   * @return Resultado de la coincidencia o nulo.
   */
  fun String.find(
    @Language("RegExp") pattern: String,
  ): MatchResult? = Regex(pattern).find(this)

  /**
   * Busca un patrón Regex en el cuerpo de la respuesta.
   *
   * @receiver Respuesta HTTP con contenido en texto.
   * @param pattern Expresión regular a buscar.
   * @return Resultado de la coincidencia o nulo.
   */
  fun ResponseEntity<String>.find(
    @Language("RegExp") pattern: String,
  ): MatchResult? = body?.find(pattern)

  /**
   * Extrae elementos HTML desde la cadena.
   *
   * @receiver HTML en formato texto.
   * @param query Selector CSS.
   * @return Colección de elementos Jsoup encontrados (vacía si no hay coincidencias).
   */
  fun String.select(query: String): Elements = Jsoup.parse(this).select(query)

  /**
   * Extrae elementos HTML desde el cuerpo de la respuesta.
   *
   * @receiver Respuesta HTTP con contenido HTML.
   * @param query Selector CSS.
   * @return Colección de elementos Jsoup encontrados (vacía si no hay cuerpo o coincidencias).
   */
  fun ResponseEntity<String>.select(query: String): Elements = body?.select(query) ?: Elements()

  /**
   * Extrae elementos HTML desde un flujo de entrada.
   *
   * @receiver Flujo de entrada con contenido HTML.
   * @param query Selector CSS.
   * @param charset Codificación de caracteres.
   * @param baseUri URI base para resolver recursos relativos.
   * @return Colección de elementos Jsoup encontrados (vacía si no hay coincidencias).
   */
  fun InputStream.select(
    query: String,
    charset: Charset = Charsets.UTF_8,
    baseUri: String = "",
  ): Elements = Jsoup.parse(this, charset.name(), baseUri).select(query)

  /**
   * Extrae el token CSRF `logintoken` desde el HTML del formulario de login de Moodle.
   *
   * @receiver Respuesta HTTP devuelta por el cliente.
   * @return La cadena del token de seguridad inyectado en el formulario.
   */
  val ResponseEntity<String>.loginToken: String? get() = select("input[name=logintoken]").first()?.attr("value")

  /**
   * Extrae el token CSRF `sesskey` desde el HTML de varias páginas de Moodle.
   *
   * @receiver Respuesta HTTP devuelta por el cliente.
   * @return La cadena del token de seguridad inyectado en el formulario.
   */
  val ResponseEntity<String>.sessionKey: String? get() = find("\"sesskey\":\"(\\w+)\"")?.groupValues?.getOrNull(1)

  /**
   * Extrae la zona horaria del sitio desde el HTML del formulario de edición del perfil de Moodle.
   *
   * @receiver Respuesta HTTP devuelta por el cliente.
   * @return El identificador de la zona horaria del sitio.
   */
  val ResponseEntity<String>.siteTimezone: String?
    get() =
      select("select#id_timezone option[value=\"99\"]")
        .first()
        ?.text()
        ?.find("\\((.+?)\\)")
        ?.groupValues
        ?.getOrNull(1)

  /**
   * Convierte una cadena en una zona horaria;
   * Moodle utiliza "99" como valor especial en la zona horaria
   * del usuario que indica que se utiliza la del servidor.
   *
   * @receiver Cadena de texto a evaluar, que puede representar un ID de zona o un offset horario numérico.
   * @param fallback Identificador de zona horaria por defecto.
   * @return Una instancia válida de [ZoneId].
   */
  fun String?.toZoneId(fallback: String): ZoneId =
    if (!isNullOrBlank() && !equals("99")) {
      runCatching {
        // Parseo tal cual
        ZoneId.of(this)
      }.getOrNull() ?: runCatching {
        // Podría ser un offset: +1, -2, ...
        ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds(toDouble().times(3600).toInt()))
      }.getOrNull()
    } else {
      null
    }
      // No se ha podido determinar
      ?: ZoneId.of(fallback)
}
