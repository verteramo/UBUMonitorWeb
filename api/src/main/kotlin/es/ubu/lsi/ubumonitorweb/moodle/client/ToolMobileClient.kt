package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleAjaxResponse
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleAutologinKey
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodlePublicConfig
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

@Client("webservice-client")
interface ToolMobileClient {
  @Client("ajax-client")
  @PostExchange
  fun getPublicConfig(): List<MoodleAjaxResponse<MoodlePublicConfig>>

  @PostExchange
  fun getAutologinKey(
    @RequestParam wstoken: String,
    @RequestParam privatetoken: String,
  ): MoodleAutologinKey
}
