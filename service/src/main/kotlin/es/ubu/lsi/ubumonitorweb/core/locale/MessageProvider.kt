package es.ubu.lsi.ubumonitorweb.core.locale

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component

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
