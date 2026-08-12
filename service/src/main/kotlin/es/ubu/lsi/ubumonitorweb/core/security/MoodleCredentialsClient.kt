package es.ubu.lsi.ubumonitorweb.core.security

import es.ubu.lsi.ubumonitorweb.core.client.ClientProfile
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.PostExchange

/**
 * Cliente HTTP que obtiene las credenciales, se hidrata desde el perfil `credentials` definido en
 * el fichero de configuración de la aplicación.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@ClientProfile("credentials")
interface MoodleCredentialsClient {
  /** Realiza la solicitud de las credenciales. */
  @PostExchange
  fun getCredentials(
    @RequestParam username: String,
    @RequestParam password: String,
  ): MoodleCredentials
}
