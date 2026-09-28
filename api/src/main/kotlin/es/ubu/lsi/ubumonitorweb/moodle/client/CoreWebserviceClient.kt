/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.moodle.client

import es.ubu.lsi.ubumonitorweb.core.client.Client
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleSiteInfo
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

/**
 * Cliente HTTP que obtiene los datos del user, se hidrata desde el perfil `user` definido
 * en el fichero de configuración de la aplicación.
 */
@Client("webservice-client")
interface CoreWebserviceClient {
  /**
   * Solicitud de los datos del user.
   */
  @Client
  fun getSiteInfo(
    @RequestParam wstoken: String,
  ): MoodleSiteInfo
}
