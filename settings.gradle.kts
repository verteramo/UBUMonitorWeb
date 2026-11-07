/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

rootProject.name = "UBUMonitorWeb"
include("api", "app")

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
  plugins {
    kotlin("plugin.jpa") version "2.3.21"
  }
}
