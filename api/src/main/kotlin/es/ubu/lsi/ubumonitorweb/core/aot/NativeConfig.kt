/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb.core.aot

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(NativeProxyHintsRegistrar::class)
class NativeConfig
