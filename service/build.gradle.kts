plugins {
	kotlin("jvm") version "2.3.21"
	kotlin("plugin.spring") version "2.3.21"
	war
	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "es.ubu.lsi"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("tools.jackson.module:jackson-module-kotlin")
	implementation("tools.jackson.dataformat:jackson-dataformat-xml")
	implementation("com.fasterxml.woodstox:woodstox-core")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat-runtime")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("com.nimbusds:nimbus-jose-jwt:10.9.1")
	implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val copyFrontendToBuild = tasks.register<Copy>("copyFrontendToBuild") {
	description = "Copia del frontend a los recursos estáticos"
  dependsOn(project(":client").tasks.named("buildFrontend"))
	from(project(":client").layout.projectDirectory.dir("build"))
	into(layout.buildDirectory.dir("resources/main/static"))
}

tasks.named("processResources") {
	dependsOn(copyFrontendToBuild)
}
