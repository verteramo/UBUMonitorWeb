/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.aot

import jakarta.servlet.http.HttpServletRequest
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference

/**
 * Implementación del registrador de hints (pistas).
 *
 * Indica al compilador nativo GraalVM qué proxies o reflexiones debe
 * construir de antemano para llevarlo al binario final.
 */
class NativeProxyHintsRegistrar : RuntimeHintsRegistrar {
  /**
   * Registra el proxies dinámicos y reglas de reflexión para clases generadas.
   *
   * @param hints Registro central de hints de ejecución.
   * @param classLoader Cargador de clases de la aplicación.
   */
  override fun registerHints(
    hints: RuntimeHints,
    classLoader: ClassLoader?,
  ) {
    /*
     * Registra el proxy dinámico de la interfaz para que GraalVM lo incluya en el binario.
     */
    hints.proxies().registerJdkProxy(HttpServletRequest::class.java)

    /*
     * Regla para el proxy CGLIB de Springdoc
     *
     * https://github.com/springdoc/springdoc-openapi/issues/3155
     * https://github.com/springdoc/springdoc-openapi/issues/3205
     */
    hints.reflection().registerType(
      TypeReference.of($$$"org.springdoc.core.providers.SpringWebProvider$$SpringCGLIB$$0"),
    ) { typeHint ->
      typeHint
        .withField($$"CGLIB$FACTORY_DATA")
        .withField($$"CGLIB$CALLBACK_FILTER")
        .withMembers(MemberCategory.INVOKE_DECLARED_METHODS)
    }
  }
}
