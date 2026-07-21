package es.ubu.lsi.ubumonitorweb.core.locale

/**
 * Enumeración mapeada a los mensajes definidos en los ficheros `messages_XX.properties`; permiten
 * acceso con tipado fuerte y sirven como fuente única de verdad.
 *
 * @param code Identificador del mensaje.
 *
 * @author Marcelo Verteramo Pérsico (mvp1011@alu.ubu.es)
 */
enum class Message(private val code: String) {
  ERROR_HTTP_MISSING_HEADER("error.http.missing_header"),
  ERROR_NET_INVALID_URI("error.net.invalid_uri"),
  ERROR_SEC_JWE("error.sec.jwe"),
  ;


  /**
   * Obtiene un mensaje localizado y parametrizado desde el proveedor de mensajes.
   *
   * @param args Argumentos del mensaje.
   * @return Mensaje.
   */
  operator fun invoke(vararg args: Any): String {
    return MessageProvider(code, *args)
  }
}
