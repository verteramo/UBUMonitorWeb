package es.ubu.lsi.ubumonitorweb.core.client

/**
 * Anotación que indica cómo serializar una colección de parámetros para un servidor PHP.
 *
 * Al aplicarse sobre un argumento, indica al cliente HTTP que formatee los elementos
 * utilizando la notación de corchetes. Esto garantiza la compatibilidad de la petición con
 * el formato de decodificación nativo de PHP para rellenar variables como `$_REQUEST`, `$_GET` o
 * `$_POST`.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PhpCollection(
  val name: String = "",
  val keyName: String = "key",
  val valueName: String = "value",
)
