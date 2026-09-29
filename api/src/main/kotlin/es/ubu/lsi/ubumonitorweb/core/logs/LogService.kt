/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.logs

import es.ubu.lsi.ubumonitorweb.domain.LogEntry
import es.ubu.lsi.ubumonitorweb.moodle.dto.MoodleLogEntry
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.annotation.RegisterReflection
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.service.registry.ImportHttpServices
import tools.jackson.dataformat.csv.CsvMapper
import tools.jackson.dataformat.csv.CsvSchema
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Servicio encargado de solicitar y transformar los logs para entregar al cliente.
 *
 * @property logClient Cliente HTTP para obtener los logs.
 * @property logMapper Servicio de mapping de logs.
 * @property csvMapper Servicio de mapping de text CSV.
 */
@Service
@RegisterReflection(
  classes = [MoodleLogEntry::class],
  memberCategories = [MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS],
)
@ImportHttpServices(LogClient::class)
class LogService(
  private val logClient: LogClient,
  private val logMapper: LogMapper,
  private val csvMapper: CsvMapper,
) {
  /**
   * Registro de ocurrencias de logs no mapeados.
   *
   * @property description Descripción de ejemplo.
   * @property templates Templates disponibles para el par componente/evento.
   * @property occurrences Recuento de ocurrencias.
   */
  private data class UnmappedEventStat(
    val description: String,
    val templates: List<Template>,
    var occurrences: Int = 0,
  )

  /**
   * Tipo para el mapa de estadísticas de logs no mapeados.
   */
  private typealias StatsMap = Map<Pair<String, String>, UnmappedEventStat>

  /**
   * Tipo para el mapa mutable de estadísticas de logs no mapeados.
   */
  private typealias MutableStatsMap = MutableMap<Pair<String, String>, UnmappedEventStat>

  /**
   * Instancia del logger.
   */
  private val logger = KotlinLogging.logger {}

  /**
   * Formateador para parsear los timestamps de los logs.
   */
  private val formatter = DateTimeFormatter.ofPattern("d/MM/yy, HH:mm[:ss]")

  /**
   * Resuelve los atributos de la entrada registrando estadísticas de eventos no mapeados.
   *
   * @param stats Mapa acumulador de estadísticas de eventos no mapeados.
   * @return Mapa de atributos extraídos o vacío si no se pudo mapear.
   */
  private fun MoodleLogEntry.resolveAttributes(stats: MutableStatsMap): Map<String, Any> =
    when (val result = logMapper.map(component, eventname, description)) {
      is MappingResult.Mapped -> {
        /*
         * Si se pueden extraer los atributos, se retornan.
         */
        result.attributes
      }

      is MappingResult.Unmapped -> {
        /*
         * Si no se puede mapear el log,
         * se suma una ocurrencia y se devuelve un mapa vacío.
         */
        stats
          .getOrPut(Pair(component, eventname)) {
            UnmappedEventStat(result.description, result.templates)
          }.occurrences++

        emptyMap()
      }
    }

  /**
   * Mapea una línea de log de Moodle a línea de log de dominio.
   *
   * @param zone Zona horaria.
   * @param attributes Mapa de atributos.
   * @return Entrada de log de dominio.
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
   * Emite en el logger el reporte de eventos no mapeados, si existen registros.
   *
   * @param stats Mapa con las estadísticas de eventos no mapeados.
   */
  private fun logReport(stats: StatsMap) {
    if (stats.isNotEmpty()) {
      logger.warn {
        stats.entries.sortedByDescending { it.value.occurrences }.joinToString("\n") { (key, stat) ->
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
  }

  /**
   * Convierte una respuesta de texto CSV en una secuencia tipada, remueve el carácter BOM si está presente.
   *
   * @return Secuencia de objetos de tipo [T].
   */
  private inline fun <reified T> ResponseEntity<String>.csvToSequence(): Sequence<T> =
    csvMapper
      .readerFor(T::class.java)
      .with(CsvSchema.emptySchema().withHeader())
      .readValues<T>(body?.removePrefix("\uFEFF"))
      .asSequence()

  /**
   * Obtiene y mapea los logs al formato de entrega al cliente.
   * Recopila ocurrencias de [MappingResult.Unmapped] y loggea un reporte.
   *
   * @param id Id del curso.
   * @param zone Zona horaria para interpretar los timestamps.
   * @return Lista de entradas de log mapeadas.
   */
  fun getLogs(
    id: Int,
    zone: ZoneId,
  ): List<LogEntry> {
    val stats: MutableStatsMap = mutableMapOf()

    val logs =
      logClient
        .getLogs(id)
        .csvToSequence<MoodleLogEntry>()
        .map { it.toLogEntry(zone, it.resolveAttributes(stats)) }
        .toList()

    logReport(stats)

    return logs
  }
}
