/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.locale

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

/**
 * Enumeración mapeada a los mensajes definidos en los ficheros `messages_XX.properties`; permiten
 * acceso ccon tipado fuerte a mensajes internacionalizados de acuerdo con el lenguaje de
 * preferencia utilizado en la cabecera `Accept-Language`; en caso de no estar presente, la
 * preferencia recae sobre la configuración `spring.web.locale` definida en el fichero
 * `application.yaml`. Si se invocan con un código de estado HTTP, generan una excepción:
 *
 * ```kotlin
 * print(Message.WELCOME(username))
 * throw Message.ERROR(400, ex.message)
 * ```
 *
 * @param code Identificador del mensaje.
 */
enum class Message(
  private val code: String,
) {
  ERROR_HTTP_MISSING_HEADER("error.http.missing_header"),
  ERROR_PROFILE_INHERIT("error.profile_inherit"),
  ERROR_PROFILE_NOT_FOUND("error.profile_not_found"), ;

  /**
   * Obtiene un mensaje localizado y parametrizado desde el proveedor de mensajes.
   *
   * @param args Argumentos del mensaje.
   * @return Mensaje.
   */
  operator fun invoke(vararg args: Any): String = Provider(code, *args)

  /**
   * Obtiene una excepción con estado HTTP y mensaje localizado y parametrizado desde el proveedor
   * de mensajes.
   *
   * @param status Estado HTTP.
   * @param args Argumentos del mensaje.
   * @return Excepción HTTP.
   */
  operator fun invoke(
    status: HttpStatus,
    vararg args: Any,
  ): ResponseStatusException = ResponseStatusException(status, this(*args))

  /**
   * Sobrecarga con código de estado como número entero.
   *
   * @param code Código de estado.
   * @param args Argumentos del mensaje.
   * @return Excepción HTTP.
   */
  operator fun invoke(
    code: Int,
    vararg args: Any,
  ): ResponseStatusException = ResponseStatusException(HttpStatusCode.valueOf(code), this(*args))

  /**
   * Componente para la obtención de mensajes internacionalizados.
   *
   * @param source Proveedor de mensajes localizados desde los ficheros de mensajes.
   */
  @Component
  class Provider(
    private val source: MessageSource,
  ) {
    // Inicialización del singleton.
    init {
      instance = this
    }

    companion object {
      /** Instancia singleton del proveedor */
      private lateinit var instance: Provider

      /**
       * Obtiene un mensaje localizado y parametrizado desde los ficheros de mensajes.
       *
       * @param code Identificador del mensaje.
       * @param args Argumentos del mensaje.
       * @return Mensaje.
       */
      operator fun invoke(
        code: String,
        vararg args: Any,
      ): String = instance.source.getMessage(code, args, LocaleContextHolder.getLocale())
    }
  }
}
