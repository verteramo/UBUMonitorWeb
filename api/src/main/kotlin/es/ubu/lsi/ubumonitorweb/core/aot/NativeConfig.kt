/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.aot

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints

/**
 * Configuración AOT para compilación nativa con GraalVM.
 * Registra hints de reflexión y deshabilita proxies de beans para optimizar el análisis estático.
 */
@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(NativeProxyHintsRegistrar::class)
class NativeConfig
