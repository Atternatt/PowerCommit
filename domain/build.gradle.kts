fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
}

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    implementation("io.arrow-kt:arrow-core:1.1.2")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}