package es.ubu.lsi.ubumonitorweb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
class Application

fun main(args: Array<String>) {
  runApplication<Application>(*args)
}
