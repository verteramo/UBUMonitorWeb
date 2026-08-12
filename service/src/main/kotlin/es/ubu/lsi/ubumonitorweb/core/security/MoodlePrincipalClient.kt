package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

/**
 * Cliente HTTP que obtiene los datos del principal, se hidrata desde el perfil `principal` definido
 * en el fichero de configuración de la aplicación.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@ClientProfile("principal")
interface MoodlePrincipalClient {
  /** Realiza la solicitud de los datos del principal. */
  @PostExchange
  fun getPrincipal(
    @RequestParam wstoken: String,
  ): MoodlePrincipal
}
