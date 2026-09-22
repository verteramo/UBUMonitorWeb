/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.GetExchange

@Client("user-edit-client")
interface UserEditClient {
  @GetExchange
  fun getEditForm(
    @RequestHeader(HttpHeaders.COOKIE) cookie: String,
  ): ResponseEntity<String>
}
