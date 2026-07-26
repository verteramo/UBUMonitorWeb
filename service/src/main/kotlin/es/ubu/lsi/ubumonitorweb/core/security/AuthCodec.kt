package es.ubu.lsi.ubumonitorweb.core.security


/**
 * Contrato para clases con la capacidad de codificar/decodificar objetos hacia/desde cadena, para
 * ser transportados en mensajes HTTP.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
interface AuthCodec<T : Any> {

  /**
   * Codifica objeto a cadena.
   *
   * @param payload Objeto.
   * @return Cadena.
   */
  fun encode(payload: T): String

  /**
   * Decodifica cadena a objeto.
   *
   * @param data Cadena.
   * @return Objeto.
   */
  fun decode(data: String): T
}
