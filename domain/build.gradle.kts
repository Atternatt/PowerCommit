fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.6.10"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    api("io.arrow-kt:arrow-core:1.1.2")
    api("io.arrow-kt:arrow-fx-coroutines:1.1.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}