/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.system

import jakarta.servlet.http.HttpServletRequest
import org.springframework.aop.SpringProxy
import org.springframework.aop.framework.Advised
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.MessageSourceAware
import org.springframework.core.DecoratingProxy
import org.springframework.security.authentication.AuthenticationManager

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
     * Registra los proxies dinámicos de las interfaces para que GraalVM los incluya en el binario.
     */
    hints.proxies().registerJdkProxy(HttpServletRequest::class.java)

    hints.proxies().registerJdkProxy(
      AuthenticationManager::class.java,
      MessageSourceAware::class.java,
      InitializingBean::class.java,
      SpringProxy::class.java,
      Advised::class.java,
      DecoratingProxy::class.java,
    )

    hints.reflection().let {
      it.registerType(
        TypeReference.of("kotlin.collections.EmptyMap"),
        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
      )

      it.registerType(
        TypeReference.of("kotlin.collections.EmptyList"),
        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
      )
    }

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
