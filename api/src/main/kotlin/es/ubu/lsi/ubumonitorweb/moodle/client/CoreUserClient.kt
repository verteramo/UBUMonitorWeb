/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import es.ubu.lsi.ubumonitorweb.core.resolver.PhpCollection
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleUser
import org.springframework.web.bind.annotation.RequestParam

@Client("webservice-client")
interface CoreUserClient {
  @Client
  fun getUsersByField(
    @RequestParam wstoken: String,
    @RequestParam field: String,
    @PhpCollection values: Collection<Any>,
  ): List<MoodleUser>
}
