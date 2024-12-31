plugins {
    kotlin("jvm") version "2.0.21"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.vincie"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

javafx {
    version = "21"
    modules = listOf("javafx.controls")
}