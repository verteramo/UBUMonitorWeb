plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    version.set("26.7.0") // Ajusta esta versión a la que utilices en tu entorno
    download.set(true)
}

val buildFrontend by tasks.registering(com.github.gradle.node.npm.task.NpmTask::class) {
    description = "Compila la aplicación Angular para producción"
    dependsOn(tasks.npmInstall)

    args.set(listOf("run", "build"))

    // Declarar entradas y salidas mejora el rendimiento usando la caché de Gradle
    inputs.dir("src")
    inputs.file("package.json")
    inputs.file("package-lock.json")
    inputs.file("angular.json")
    inputs.file("tsconfig.json")

    outputs.dir("build")
}
