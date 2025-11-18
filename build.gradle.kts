plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.9.10"
    application
}

group = "com.modern.playscraper"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")

    // Ktor Client
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-okhttp:2.3.5")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-client-logging:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // HTML Parsing
    implementation("org.jsoup:jsoup:1.16.2")

    // JSON Path
    implementation("com.jayway.jsonpath:json-path:2.8.0")

    // Gson (for JSON parsing)
    implementation("com.google.code.gson:gson:2.10.1")

    // Dependency Injection
    implementation("io.insert-koin:koin-core:3.5.0")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("io.ktor:ktor-client-mock:2.3.5")
    testImplementation("io.insert-koin:koin-test:3.5.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xjsr305=strict",
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview"
        )
        jvmTarget = "17"
    }
}

tasks.test {
    useJUnitPlatform()
}

// Application configuration for examples
application {
    mainClass.set(project.findProperty("mainClass") as String? ?: "com.modern.playscraper.examples.BasicUsageExampleKt")
}

// JavaExec task configuration
tasks.withType<JavaExec> {
    standardInput = System.`in`
}

// Dokka documentation
tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka/html"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.modern.playscraper"
            artifactId = "play-store-scraper"
            version = project.version.toString()

            from(components["java"])

            pom {
                name.set("Play Store Scraper Kotlin")
                description.set("Modern Kotlin library for scraping Google Play Store data with coroutines, Flow, and Clean Architecture")
                url.set("https://github.com/ibank/google-play-store-scraper-kotlin")
                inceptionYear.set("2025")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("ibank")
                        name.set("Benjamin Lee")
                        email.set("ibank@users.noreply.github.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/ibank/google-play-store-scraper-kotlin.git")
                    developerConnection.set("scm:git:ssh://github.com/ibank/google-play-store-scraper-kotlin.git")
                    url.set("https://github.com/ibank/google-play-store-scraper-kotlin")
                }

                issueManagement {
                    system.set("GitHub Issues")
                    url.set("https://github.com/ibank/google-play-store-scraper-kotlin/issues")
                }
            }
        }
    }

    repositories {
        maven {
            name = "CentralPortal"
            url = uri("https://central.sonatype.com/api/v1/publisher/upload")

            credentials {
                username = project.findProperty("centralPortalUsername") as String? ?: System.getenv("CENTRAL_PORTAL_USERNAME")
                password = project.findProperty("centralPortalPassword") as String? ?: System.getenv("CENTRAL_PORTAL_PASSWORD")
            }
        }

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ibank/google-play-store-scraper-kotlin")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

signing {
    // Only sign if publishing to Maven Central (not for local publishing)
    setRequired({
        gradle.taskGraph.allTasks.any { it.name.contains("publish") && !it.name.contains("ToMavenLocal") }
    })
    sign(publishing.publications["maven"])
}

// Task to print publication info
tasks.register("publicationInfo") {
    doLast {
        println("Publication Information:")
        println("  Group: ${project.group}")
        println("  Artifact: play-store-scraper")
        println("  Version: ${project.version}")
        println("  Full Coordinate: ${project.group}:play-store-scraper:${project.version}")
    }
}
