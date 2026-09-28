/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.logs

import es.ubu.lsi.ubumonitorweb.core.client.Client
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestParam

/**
 * Cliente HTTP que consume en endpoint de los logs.
 */
interface LogClient {
  /**
   * Obtiene la respuesta del endpoint de logs.
   *
   * Valores conocidos de [origin]: cli, restore, web, ws.
   *
   * @param id Id del curso.
   * @param date Fecha (opcional).
   * @param origin Origen (opcional).
   */
  @Client("log-client")
  fun getLogs(
    @RequestParam id: Int,
    @RequestParam date: Long? = null,
    @RequestParam origin: String? = null,
  ): ResponseEntity<String>
}
