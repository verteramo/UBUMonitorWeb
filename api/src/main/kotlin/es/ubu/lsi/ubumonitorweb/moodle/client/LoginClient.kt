/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleToken
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestParam

/**
 * Cliente encargado de las llamadas al formulario de login de Moodle `/login/index.php`.
 */
interface LoginClient {
  /**
   * Solicitud de las credenciales.
   */
  @Client("login-token-client")
  fun getToken(
    @RequestParam username: String,
    @RequestParam password: String,
  ): MoodleToken

  /**
   * Llamada GET al formulario,
   * en la respuesta se incluyen la cookie `MoodleSession`, el token CSRF `logintoken` y la `sesskey`.
   */
  @Client("login-index-get-client")
  fun getIndex(): ResponseEntity<String>

  /**
   * Llamada POST al formulario incluyendo la cookie, el token CSRF y el usuario/contraseña,
   * en la respuesta se incluye la cookie `MoodleSession` definitiva.
   */
  @Client("login-index-post-client")
  fun postIndex(
    @RequestParam username: String,
    @RequestParam password: String,
    @RequestParam logintoken: String,
  ): ResponseEntity<String>

  @Client("user-edit-client")
  fun getUserEditForm(): ResponseEntity<String>
}
