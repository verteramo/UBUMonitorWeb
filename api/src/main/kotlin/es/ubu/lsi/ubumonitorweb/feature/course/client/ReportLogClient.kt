package es.ubu.lsi.ubumonitorweb.feature.course.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.data.dto.MoodleLogEntry
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

@ClientProfile
interface ReportLogClient {
  @GetExchange
  fun getLogs(
    @RequestHeader(HttpHeaders.COOKIE) cookie: String,
    @RequestParam id: Int,
    @RequestParam date: Long? = null,
    @RequestParam origin: String? = null,
  ): List<List<MoodleLogEntry>>
}
