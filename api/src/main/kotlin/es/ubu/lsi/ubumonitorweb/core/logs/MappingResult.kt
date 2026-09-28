/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.logs

/**
 * Permite utilizar el patrón Either para devolver el resultado de intentar mapear un log.
 *
 * Cuando se intenta mapear un log pueden ocurrir dos situaciones:
 * 1. Que el mapeo sea exitoso, en cuyo caso se devolverá un mapa de atributos.
 * 2. Que el mapeo falle, en cuyo caso se devolverán todos los templates probados.
 *
 * Este enfoque puede resultar similar al patrón Result, cuya implementación existe en
 * Kotlin de manera nativa, sin embargo, el patrón Result obliga a manejar excepciones,
 * lo que en este caso de uso sería contraproducente porque se intentan mapear muchos
 * logs y resultaría muy pesado construir stacktraces para numerosos errores.
 * En sí, no poder mapear un log es un resultado de negocio válido teniendo en
 * cuenta que los logs son cadenas crudas que pueden cambiar entre plugins o componentes.
 *
 * https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/
 */
sealed interface MappingResult {
  /**
   * DTO en caso de éxito.
   *
   * @property attributes Atributos extraídos del log.
   */
  data class Mapped(
    val attributes: Map<String, Any>,
  ) : MappingResult

  /**
   * DTO en caso de fallo.
   *
   * @property description Descripción que no se ha podido mapear.
   * @property templates Lista de templates disponibles.
   */
  data class Unmapped(
    val description: String,
    val templates: List<Template>,
  ) : MappingResult
}
