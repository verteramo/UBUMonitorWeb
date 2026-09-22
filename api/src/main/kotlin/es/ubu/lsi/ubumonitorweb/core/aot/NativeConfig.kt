/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.aot

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints

/**
 * Configuración para la compilación nativa con GraalVM.
 *
 * Se registra la clase como proveedora de beans, pero se deshabilita la creación
 * de proxies y se registran los hints (pistas sobre qué clases requieren reflexión)
 * durante el arranque para incluir todo el código al que normalmente la JVM llega
 * mediante reflexión y el binario solo puede llegar mediante análisis estático.
 */
@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(NativeProxyHintsRegistrar::class)
class NativeConfig
