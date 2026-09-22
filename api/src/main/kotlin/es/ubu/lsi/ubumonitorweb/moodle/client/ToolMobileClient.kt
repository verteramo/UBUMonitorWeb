package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodlePublicConfig
import org.springframework.web.service.annotation.PostExchange

@Client("ajax-client")
interface ToolMobileClient {
  data class AjaxResponse<T>(
    val error: Boolean,
    val data: T,
  )

  @PostExchange
  fun getPublicConfig(): List<AjaxResponse<MoodlePublicConfig>>
}
