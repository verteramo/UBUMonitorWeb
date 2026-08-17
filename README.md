# UBUMonitorWeb

## Construir WAR
Construye un paquete WAR embebiendo la aplicación Angular en el directorio de recursos estáticos de la API Springboot:
```shell
./gradlew :bootPackage
```
![bootPackage-build](docs/img/bootPackage-build.png)
Una vez ejecutado el distribuible (ubicado en el directorio `UBUMonitorWeb\api\build\libs`)`:
```shell
java -jar api-version.war
```
![bootPackage-run](docs/img/bootPackage-run.png)
Se accede a `http://localhost:8080` y se encuentra la aplicación lista para su uso:
![bootPackage-browser](docs/img/bootPackage-browser.png)

## Construir ejecutable nativo (GraalVM)
```shell
./gradlew :nativePackage
```
![nativePackage-build](docs/img/nativePackage-build.png)
En este caso, se ejecuta el distribuible nativo (ubicado en el directorio `UBUMonitorWeb\api\build\native\nativeCompile`):
![nativePackage-run](docs/img/nativePackage-run.png)
De igual manera, al acceder a `http://localhost:8080` se encuentra la aplicación en ejecución:
![nativePackage-browser](docs/img/nativePackage-browser.png)

## Comparativa de tiempos de construcción
El paquete WAR se construye en 1 segundo.
Por su parte, el ejecutable nativo se construye en mucho más tiempo, por ejemplo, esta construcción particular necesitó 2 minutos 56 segundos:
![nativePackage-buildTime](docs/img/nativePackage-buildTime.png)

## Comparativa de tiempos de inicio
El paquete WAR requiere una JVM en ejecución y se pone en funcionamiento en 4.013 segundos:
![bootPackage-startupTime](docs/img/bootPackage-startupTime.png)
El paquete nativo tiene un tiempo de inicio muy inferior, de 0.111 segundos:
![nativePackage-startupTime](docs/img/nativePackage-startupTime.png)

La conclusión final es que el paquete WAR (tarea `:api:bootWar`) es ideal para desarrollo por su bajo tiempo de construcción (1 segundo), y el paquete nativo (tarea `:api:nativeCompile`) es ideal para producción, ya que a pesar de su elevado tiempo de construcción (2 minutos 56 segundos), su tiempo de inicio y ejecución es muy inferior, gracias a GraalVM.
