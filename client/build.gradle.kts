import com.github.gradle.node.npm.task.NpmTask

plugins {
  base
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
  inputs.dir(layout.projectDirectory.dir("src"))
  inputs.dir(layout.projectDirectory.dir("static"))
  inputs.file(layout.projectDirectory.file("package.json"))
  inputs.file(layout.projectDirectory.file("svelte.config.js"))
  inputs.file(layout.projectDirectory.file("vite.config.ts"))

  outputs.dir(layout.projectDirectory.dir("build"))
}

tasks.assemble {
  dependsOn("buildFrontend")
}
