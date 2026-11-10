package es.ubu.lsi.ubumonitorweb.core.locale

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class I18nService(
  private val messageSource: MessageSource,
) {
  operator fun invoke(
    message: Message,
    vararg args: Any,
  ): String = messageSource.getMessage(message.code, args, LocaleContextHolder.getLocale())
}
