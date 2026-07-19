package es.ubu.lsi.ubumonitorweb.core.locale

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component

/**
 * Componente para la obtención de mensajes internacionalizados de acuerdo con el lenguaje de
 * preferencia utilizado en la cabecera `Accept-Language`; en caso de no estar presente, la
 * preferencia recae sobre la configuración `spring.web.locale` definida en el fichero
 * `application.yaml`. Permite el acceso mediante el operador `invoke` de su `companion object`, por
 * ejemplo:
 *
 * ```kotlin
 * MessageProvider("message.id", arg1, arg2, ...)
 * ```
 *
 * @param source Resolutor de mensajes localizados y parametrizados desde los ficheros de mensajes.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Component
class MessageProvider(private val source: MessageSource) {

  // Inicialización del singleton
  init {
    instance = this
  }

  companion object {
    /** Instancia singleton del proveedor */
    private lateinit var instance: MessageProvider

    /**
     * Obtiene un mensaje localizado y parametrizado desde los ficheros de mensajes.
     *
     * @param code Identificador del mensaje.
     * @param args Argumentos del mensaje.
     * @return Mensaje.
     */
    operator fun invoke(code: String, vararg args: Any): String {
      return instance.source.getMessage(code, args, LocaleContextHolder.getLocale())
    }
  }
}
