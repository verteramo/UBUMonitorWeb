/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.logs

import es.ubu.lsi.ubumonitorweb.domain.LogEntry
import es.ubu.lsi.ubumonitorweb.moodle.client.LogClient
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleLogEntry
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices
import tools.jackson.dataformat.csv.CsvMapper
import tools.jackson.dataformat.csv.CsvSchema
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Servicio encargado de solicitar y transformar los logs para entregar al cliente.
 */
@Service
@ImportHttpServices(LogClient::class)
class LogService(
  private val logClient: LogClient,
  private val logMapper: LogMapper,
  private val csvMapper: CsvMapper,
) {
  data class UnmappedEventStat(
    val description: String,
    val templates: List<Template>,
    var occurrences: Int = 0,
  )

  companion object {
    /**
     * Formateador para parsear marcas de tiempo de los logs.
     */
    private val formatter = DateTimeFormatter.ofPattern("d/MM/yy, HH:mm[:ss]")
  }

  private val logger = KotlinLogging.logger {}

  /**
   * Mapper de línea de log de Moodle a línea de log de dominio.
   */
  private fun MoodleLogEntry.toLogEntry(
    zone: ZoneId,
    attributes: Map<String, Any>,
  ) = LogEntry(
    datetime = ZonedDateTime.parse(time, formatter.withZone(zone)),
    component = component,
    event = eventname,
    origin = origin,
    ipAddress = ipaddress,
    attributes = attributes,
  )

  /**
   * Convierte una respuesta CSV en una lista tipada,
   * remueve el carácter BOM si está presente.
   */
  private inline fun <reified T> String.toList(): List<T> =
    csvMapper
      .readerFor(T::class.java)
      .with(CsvSchema.emptySchema().withHeader())
      .readValues<T>(removePrefix("\uFEFF"))
      .asSequence()
      .toList()

  /**
   * Obtiene y mapea los logs al formato de entrega al cliente.
   */
  fun getLogs(
    id: Int,
    zone: ZoneId,
  ): List<LogEntry> {
    val unmappedEventStat = mutableMapOf<String, UnmappedEventStat>()

    val logs =
      logClient.getCsvLogs(id).toList<MoodleLogEntry>().map { entry ->
        val attributes =
          when (val result = logMapper.map(entry.component, entry.eventname, entry.description)) {
            is MappingResult.Mapped -> {
              result.attributes
            }

            is MappingResult.Unmapped -> {
              unmappedEventStat
                .getOrPut("${entry.component}.${entry.eventname}") {
                  UnmappedEventStat(result.description, result.templates)
                }.occurrences++

              emptyMap()
            }
          }

        entry.toLogEntry(zone, attributes)
      }

    if (unmappedEventStat.isNotEmpty()) {
      logger.warn {
        unmappedEventStat.entries.sortedByDescending { it.value.occurrences }.joinToString("\n") { (key, stat) ->
          """
          |
          | Event: [$key] (${stat.occurrences} occurrences)
          | - Description: ${stat.description}
          | - Templates (${stat.templates.size}):
          | ${stat.templates.joinToString("\n")}
          """.trimMargin()
        }
      }
    }

    return logs
  }
}
