rootProject.name = rootDir.name

val client = "client-ng"

include("service", "client")
project(":client").projectDir = file(client)
