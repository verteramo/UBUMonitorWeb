/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.feature.course

import es.ubu.lsi.ubumonitorweb.core.mapping.LogMapper
import es.ubu.lsi.ubumonitorweb.domain.LogEntry
import es.ubu.lsi.ubumonitorweb.moodle.client.ReportLogClient
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleLogEntry
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Servicio encargado de solicitar y transformar los logs para entregar al cliente.
 */
@Service
@ImportHttpServices(ReportLogClient::class)
class LogsService(
  private val logMapper: LogMapper,
  private val reportLogClient: ReportLogClient,
) {
  /**
   * Expresión regular para extraer números enteros de la descripción de los logs.
   */
  private val intRegex = Regex("-?\\d+")

  /**
   * Formateador para parsear marcas de tiempo de los logs.
   */
  val baseDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/MM/yy, HH:mm[:ss]")

  /**
   * Mapper de línea de log de Moodle a línea de log de dominio.
   */
  private fun MoodleLogEntry.toLogEntry(dateTimeFormatter: DateTimeFormatter) =
    LogEntry(
      datetime = ZonedDateTime.parse(time, dateTimeFormatter),
      component = component,
      event = eventname,
      origin = origin,
      ipAddress = ipaddress,
    )

  /**
   * Obtiene y mapea los logs al formato de entrega al cliente.
   */
  fun getLogs(
    id: Int,
    zone: ZoneId,
  ): List<LogEntry> {
    val dateTimeFormatter = baseDateTimeFormatter.withZone(zone)

    return reportLogClient.getLogs(id).first().map { entry ->
      entry.toLogEntry(dateTimeFormatter).also { logMapper.compose(it, entry.description) }
    }
  }
}
