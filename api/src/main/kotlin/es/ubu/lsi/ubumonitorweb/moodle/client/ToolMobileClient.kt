package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleAjaxResponse
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleAutologinKey
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodlePublicConfig
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange
import java.net.URI

interface ToolMobileClient {
  @Client("ajax-client")
  fun getPublicConfig(): List<MoodleAjaxResponse<MoodlePublicConfig>>

  @Client("autologin-client")
  fun getAutologinKey(
    @RequestParam wstoken: String,
    @RequestParam privatetoken: String,
  ): MoodleAutologinKey

  @GetExchange
  fun autologin(
    url: URI,
    @RequestParam key: String,
    @RequestParam userid: Int,
  ): ResponseEntity<String>
}
