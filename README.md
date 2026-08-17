# UBUMonitorWeb

## Construir WAR
Construye un paquete WAR embebiendo la aplicación Angular en el directorio de recursos estáticos de la API Springboot:
```shell
./gradlew :bootPackage
```
![img_2.png](docs/img/bootPackage-build.png)
Una vez ejecutado el distribuible (ubicado en el directorio `UBUMonitorWeb\api\build\libs`)`:
```shell
java -jar api-version.war
```
![img.png](docs/img/bootPackage-run.png)
Se accede a `http://localhost:8080` y se encuentra la aplicación lista para su uso:
![img_1.png](docs/img/bootPackage-browser.png)

## Construir ejecutable nativo (GraalVM)
```shell
./gradlew :nativePackage
```
![img.png](docs/img/nativePackage-build.png)
En este caso, se ejecuta el distribuible nativo (ubicado en el directorio `UBUMonitorWeb\api\build\native\nativeCompile`):
![img.png](docs/img/nativePackage-run.png)
De igual manera, al acceder a `http://localhost:8080` se encuentra la aplicación en ejecución:
![img.png](docs/img/nativePackage-browser.png)

## Comparativa de tiempos de construcción
El paquete WAR se construye en poco tiempo.
Por su parte, el ejecutable nativo se construye en mucho más tiempo, por ejemplo, esta construcción particular necesitó 2 minutos 56 segundos:
![img_1.png](docs/img/nativePackage-buildTime.png)

## Comparativa de tiempos de inicio
El paquete WAR requiere una JVM en ejecución y se pone en funcionamiento en 4.013 segundos:
![img_3.png](docs/img/bootPackage-startupTime.png)
El paquete nativo tiene un tiempo de inicio muy inferior, de 0.111 segundos:
![img.png](docs/img/nativePackage-startupTime.png)

La conclusión final es que el paquete WAR (tarea `:api:bootWar`) es ideal para desarrollo por su bajo tiempo de construcción, y el paquete nativo es ideal para producción, ya que a pesar de su elevado tiempo de construcción, su tiempo de inicio y ejecución es muy inferior, gracias a GraalVM.
