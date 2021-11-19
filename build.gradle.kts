import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    application
    kotlin("jvm")                              version "1.6.0"
    kotlin("plugin.spring")                    version "1.6.0"
    id("org.springframework.boot")             version "2.6.0"
    id("io.spring.dependency-management")      version "1.0.11.RELEASE"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-webflux")

    runtimeOnly("com.google.cloud:spring-cloud-gcp-starter-metrics:2.0.5")
    runtimeOnly("com.google.cloud:spring-cloud-gcp-logging:2.0.5")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.r2dbc:r2dbc-postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.testcontainers:postgresql:1.16.2")
    testImplementation("org.testcontainers:r2dbc:1.16.2")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

application {
    mainClass.set("kotlinbars.MainKt")
}

tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
    classpath += sourceSets["test"].runtimeClasspath
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        events(STARTED, PASSED, SKIPPED, FAILED)
    }
}
