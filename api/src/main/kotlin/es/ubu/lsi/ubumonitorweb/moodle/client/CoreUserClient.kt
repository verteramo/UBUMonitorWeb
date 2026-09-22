/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import es.ubu.lsi.ubumonitorweb.core.resolver.PhpArray
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleUser
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

@Client("webservice-client")
interface CoreUserClient {
  @PostExchange
  fun getUsersByField(
    @RequestParam wstoken: String,
    @RequestParam field: String,
    @PhpArray values: List<String>,
  ): List<MoodleUser>
}
