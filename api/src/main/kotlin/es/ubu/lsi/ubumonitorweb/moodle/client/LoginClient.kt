package es.ubu.lsi.ubumonitorweb.core.moodle

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

/**
 * Cliente encargado de las llamadas al formulario de login de Moodle `/login/index.php`.
 */
@ClientProfile
interface LoginClient {
  /**
   * Llamada GET al formulario,
   * en la respuesta se incluyen la cookie `MoodleSession`, el token CSRF `logintoken` y la `sesskey`.
   */
  @GetExchange
  fun getCall(): ResponseEntity<String>

  /**
   * Llamada POST al formulario incluyendo la cookie, el token CSRF y el usuario/contraseña,
   * en la respuesta se incluye la cookie `MoodleSession` definitiva.
   */
  @PostExchange(contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  fun postCall(
    @RequestHeader(HttpHeaders.COOKIE) cookie: String,
    @RequestParam username: String,
    @RequestParam password: String,
    @RequestParam logintoken: String,
  ): ResponseEntity<String>
}
