/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.logs

/**
 * Permite utilizar el patrón Either para devolver el resultado de intentar mapear
 * un registro.
 *
 * Cuando se intenta mapear un registro pueden ocurrir dos situaciones:
 * 1. Que el mapeo sea exitoso, en cuyo caso se devolverá un mapa de atributos.
 * 2. Que el mapeo falle, en cuyo caso se devolverán todos los templates probados.
 *
 * Este enfoque puede resultar similar al patrón Result, cuya implementación existe en
 * Kotlin de manera nativa, sin embargo, el patrón Result obliga a manejar excepciones,
 * lo que en este caso de uso sería contraproducente porque se intentan mapear muchos
 * registros y resultaría muy pesado construir stacktraces para numerosos errores.
 * En sí, no poder mapear un registro es un resultado válido de negocio teniendo en
 * cuenta que los logs son cadenas crudas que pueden cambiar entre plugins o componentes.
 */
sealed interface MappingResult {
  data class Mapped(
    val attributes: Map<String, Any>,
  ) : MappingResult

  data class Unmapped(
    val description: String,
    val templates: List<Template>,
  ) : MappingResult
}
