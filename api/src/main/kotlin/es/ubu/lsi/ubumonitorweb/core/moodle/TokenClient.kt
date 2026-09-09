/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.moodle

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

/**
 * Cliente HTTP que obtiene las credenciales, se hidrata desde el perfil `token-client` definido en
 * el fichero de configuración de la aplicación.
 */
@ClientProfile
interface TokenClient {
  /** Solicitud de las credenciales. */
  @PostExchange
  fun getToken(
    @RequestParam username: String,
    @RequestParam password: String,
  ): MoodleToken
}
