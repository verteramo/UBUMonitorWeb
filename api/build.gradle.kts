/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.spring)
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependencyManagement)
  alias(libs.plugins.graalvm.native)
  war
}

group = "es.ubu.lsi"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(25))
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(libs.kotlin.reflect)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.spring.boot.starter.webmvc)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.actuator)

  implementation(libs.jackson.module.kotlin)
  implementation(libs.jackson.dataformat.xml)
  implementation(libs.woodstox.core)
  implementation(libs.springdoc.openapi)
  implementation(libs.kotlin.logging)

  providedRuntime(libs.spring.boot.starter.tomcat.runtime)
  testImplementation(libs.kotlin.test.junit5)
  testImplementation(libs.spring.security.test)
  testImplementation(libs.spring.boot.starter.webmvc.test)
  testImplementation(libs.spring.boot.starter.test)
  developmentOnly(libs.spring.boot.devtools)
  testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
  compilerOptions {
    freeCompilerArgs.set(listOf("-Xjsr305=strict", "-Xannotation-default-target=param-property"))
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
