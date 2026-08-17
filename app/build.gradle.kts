import com.github.gradle.node.npm.task.NpmTask

plugins {
  alias(libs.plugins.node)
}

node {
  version.set(libs.versions.nodeVersion.get())
  download.set(true)
}

 /*
  * Construcción de la aplicación
  * https://github.com/node-gradle/gradle-node-plugin/blob/main/docs/usage.md#executing-npm-tasks
  */
tasks.register<NpmTask>("build") {
  description = "Compila la aplicación Angular"
  dependsOn("npmInstall")
  args.set(listOf("run", "build"))
  inputs.files(
    "package.json",
    "package-lock.json",
    "angular.json",
    "tsconfig.json",
    "tsconfig.app.json",
  )
  inputs.dir("src")
  inputs.dir(fileTree("node_modules") { exclude(".cache") })
  outputs.dir("dist/UBUMonitorWeb/browser")
}
