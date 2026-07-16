package es.ubu.lsi.ubumonitorweb.core.base

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

/**
 * Componente para la obtención de mensajes internacionalizados de acuerdo con
 * el lenguaje de preferencia utilizado en la cabecera `Accept-Language`; en
 * caso de no estar presente, la preferencia recae sobre la configuración
 * `spring.web.locale` definida en el fichero `application.yaml`. Permite el
 * acceso mediante el operador `invoke` de su `companion object`, por ejemplo:
 *
 * ```kotlin
 * MessageProvider("id.mensaje", arg1, arg2, ...)
 * ```
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class MessageProvider(private val source: MessageSource) {

  init {
    instance = this
  }

  companion object {
    private lateinit var instance: MessageProvider

    operator fun invoke(code: String, vararg args: Any) =
      instance.source.getMessage(code, args, LocaleContextHolder.getLocale())
  }
}

/**
 * Enumeración mapeada a los mensajes definidos en los ficheros
 * `messages_XX.properties`; permiten acceso con tipado fuerte y sirven como
 * fuente única de verdad.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
enum class Message(private val code: String) {
  ERROR_HTTP_MISSING_HEADER("error.http.missing_header"),
  ERROR_HTTP_BLANK_HEADER("error.http.blank_header"),
  ERROR_HTTP_INVALID_HEADER("error.http.invalid_header"),
  ;

  operator fun invoke(vararg args: Any) =
    MessageProvider(code, *args)
}

/**
 * Enumeración que mapea códigos de estado [HttpStatus] con mensajes [Message]
 * de la enumeración anterior, su propósito es proporcionar una fábrica de
 * excepciones recurrentes preparadas para ser lanzadas con throw, por ejemplo:
 *
 * ```kotlin
 * throw ErrorResponse.HTTP_MISSING_HEADER("X-My-Header")
 * ```
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
enum class ErrorResponse(
    private val status: HttpStatus, private val message: Message,
) {
  HTTP_MISSING_HEADER(
    HttpStatus.BAD_REQUEST, Message.ERROR_HTTP_MISSING_HEADER,
  ),

  HTTP_INVALID_HEADER(
    HttpStatus.BAD_REQUEST, Message.ERROR_HTTP_INVALID_HEADER,
  ),

  HTTP_BLANK_HEADER(
    HttpStatus.BAD_REQUEST, Message.ERROR_HTTP_BLANK_HEADER,
  ),
  ;

  operator fun invoke(vararg args: Any) =
    ResponseStatusException(status, message(*args))

  operator fun invoke(cause: Throwable, vararg args: Any) =
    ResponseStatusException(status, message(*args), cause)
}
