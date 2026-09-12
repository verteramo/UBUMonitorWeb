/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleSiteInfo
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

/**
 * Cliente HTTP que obtiene los datos del principal, se hidrata desde el perfil `principal` definido
 * en el fichero de configuración de la aplicación.
 */
@ClientProfile("webservice-client")
interface CoreWebserviceClient {
  /** Solicitud de los datos del principal. */
  @PostExchange
  fun getSiteInfo(
    @RequestParam wstoken: String,
  ): MoodleSiteInfo
}
