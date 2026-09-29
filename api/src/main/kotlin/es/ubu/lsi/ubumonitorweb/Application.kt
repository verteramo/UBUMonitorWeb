/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

package es.ubu.lsi.ubumonitorweb

import es.ubu.lsi.ubumonitorweb.core.system.NativeProxyHintsRegistrar
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ImportRuntimeHints

@SpringBootApplication(proxyBeanMethods = false)
@ImportRuntimeHints(NativeProxyHintsRegistrar::class)
class Application

fun main(args: Array<String>) {
  runApplication<Application>(*args)
}
