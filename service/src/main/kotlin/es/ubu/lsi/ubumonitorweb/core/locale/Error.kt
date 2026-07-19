package es.ubu.lsi.ubumonitorweb.core.locale

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

/**
 * Enumeración que mapea códigos de estado [HttpStatus] con mensajes de la
 * enumeración [Message], su propósito es proporcionar una fábrica de
 * excepciones recurrentes preparadas para ser lanzadas con `throw`, por
 * ejemplo:
 *
 * ```kotlin
 * throw Error.HTTP_MISSING_HEADER("X-My-Header")
 * ```
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
enum class Error(private val status: HttpStatus, private val message: Message) {
  HTTP_MISSING_HEADER(
    HttpStatus.BAD_REQUEST, Message.ERROR_HTTP_MISSING_HEADER,
  ),

  NET_INVALID_URI(
    HttpStatus.BAD_REQUEST, Message.ERROR_NET_INVALID_URI,
  ),
  ;

  operator fun invoke(vararg args: Any) =
      ResponseStatusException(status, message(*args))

  operator fun invoke(cause: Throwable, vararg args: Any) =
      ResponseStatusException(status, message(*args), cause)
}
