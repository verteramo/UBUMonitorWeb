plugins {
  id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

repositories {
  mavenCentral()
}

ktlint {
  android.set(false)
  ignoreFailures.set(false)
}

val copyFrontend =
  tasks.register<Copy>("copyFrontend") {
    description = "Copy client static build into service static resources"
    dependsOn(project(":client").tasks.named("buildFrontend"))
    from(project(":client").layout.projectDirectory.dir("build"))
    into(project(":service").layout.buildDirectory.dir("resources/main/static"))
  }

project(":service").tasks.matching { it.name == "bootWar" }.configureEach {
  dependsOn(copyFrontend)
}
