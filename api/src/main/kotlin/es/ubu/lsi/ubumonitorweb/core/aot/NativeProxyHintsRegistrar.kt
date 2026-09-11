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

class NativeProxyHintsRegistrar : RuntimeHintsRegistrar {
  override fun registerHints(
    hints: RuntimeHints,
    classLoader: ClassLoader?,
  ) {
    // Registra el proxy dinámico de la interfaz para que GraalVM lo incluya en el binario
    hints.proxies().registerJdkProxy(HttpServletRequest::class.java)

    // La nueva regla para el proxy CGLIB de Springdoc
    hints.reflection().registerType(
      TypeReference.of($$$"org.springdoc.core.providers.SpringWebProvider$$SpringCGLIB$$0"),
    ) { typeHint ->
      typeHint.withField($$"CGLIB$FACTORY_DATA")
      typeHint.withField($$"CGLIB$CALLBACK_FILTER")
      typeHint.withMembers(MemberCategory.INVOKE_DECLARED_METHODS)
    }
  }
}
