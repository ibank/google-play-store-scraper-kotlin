plugins {
    kotlin("jvm") version "1.9.20"
    application
}

group = "com.example"
version = "1.0.0"

repositories {
    // Use local Maven repository for testing before Maven Central publish
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Google Play Store Scraper from local Maven
    implementation("io.github.ibank:play-store-scraper:1.0.0")

    // Kotlin Coroutines (required by the scraper)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Koin DI (required by the scraper)
    implementation("io.insert-koin:koin-core:3.5.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")
}

application {
    mainClass.set("com.example.TestScraperKt")
}

kotlin {
    jvmToolchain(17)
}
