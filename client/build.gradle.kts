import com.github.gradle.node.npm.task.NpmTask

plugins {
  id("com.github.node-gradle.node") version "7.1.0"
}

node {
  version.set("24.18.0")
  download.set(true)
}

tasks.register<NpmTask>("buildFrontend") {
  description = "Construcción del frontend"
  dependsOn(tasks.npmInstall)
  args.set(listOf("run", "build"))
  inputs.dir("src")
  outputs.dir("build")
}
