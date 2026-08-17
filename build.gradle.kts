plugins {
  alias(libs.plugins.ktlint)
}

ktlint {
  android.set(false)
  ignoreFailures.set(false)
}

val appOutputDir = project(":app").file("dist/UBUMonitorWeb/browser")
val apiStaticDir = project(":api").file("src/main/resources/static")

tasks.register<Copy>("copyApp") {
  group = "build"
  description = "Copies Angular application to API's static directory."
  dependsOn(":app:build")
  from(appOutputDir)
  into(apiStaticDir)
}

tasks.register("cleanApi") {
  group = "build"
  description = "Cleans Angular files from API's static directory."

  val source = appOutputDir
  val target = apiStaticDir

  doLast {
    source.takeIf { it.exists() }?.let { dir ->
      dir
        .walkBottomUp()
        .map { it.relativeTo(dir).path }
        .filter { it.isNotBlank() }
        .map { File(target, it) }
        .filter { it.exists() }
        .forEach(File::delete)
    }
  }
}

/**
 * Función para crear la acción de copia y limpieza de recursos estáticos.
 * Se unifica la lógica, ya que es la misma para la construcción del paquete WAR o nativo.
 */
fun Task.configureAngularEmbedding(
  targetProjectName: String,
  targetTaskName: String,
) {
  group = "build"
  dependsOn(":copyApp", ":$targetProjectName:$targetTaskName")

  val targetProject = project.project(":$targetProjectName")
  targetProject.tasks.named("processResources").configure { mustRunAfter(":copyApp") }
  targetProject.tasks.named(targetTaskName).configure { mustRunAfter(":copyApp") }

  finalizedBy(":cleanApi")
}

tasks.register("bootPackage") {
  description = "Runs :api:bootWar with Angular application embedded."
  configureAngularEmbedding("api", "bootWar")
}

tasks.register("nativePackage") {
  description = "Runs :api:nativeCompile with Angular application embedded."
  configureAngularEmbedding("api", "nativeCompile")
}
