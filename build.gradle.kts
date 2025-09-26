plugins {
    kotlin("jvm") version "2.2.10"
    id("com.vanniktech.maven.publish") version "0.33.0"
}

group = "tech.aliorpse"
version = System.getenv("GITHUB_REF_NAME") ?: "local"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    api(kotlin("reflect"))

    api("io.ktor:ktor-client-core:3.3.0")

    api("org.jetbrains.skiko:skiko-awt:0.9.24")

    api("com.github.librepdf:openpdf:3.0.0")
    api("org.bouncycastle:bcprov-jdk15on:1.70")

    api("com.fleeksoft.ksoup:ksoup:0.2.5")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-client-cio:3.3.0")
    testImplementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.9.9")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
    explicitApi()
}

configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
    publishToMavenCentral()
    signAllPublications()
    coordinates("tech.aliorpse", "jmutils", version.toString())

    pom {
        name = "jmutils"
        description = "Kotlin library for JMComic operations"
        url = "https://github.com/Aliorpse/jmutils/"
        inceptionYear = "2025"

        licenses {
            license {
                name = "MIT"
                url = "https://opensource.org/licenses/MIT"
            }
        }

        developers {
            developer {
                id = "aliorpse"
                name = "Aliorpse"
                url = "https://github.com/Aliorpse/"
            }
        }

        scm {
            url = "https://github.com/Aliorpse/jmutils/"
            connection = "scm:git:git://github.com/Aliorpse/jmutils.git"
            developerConnection = "scm:git:ssh://git@github.com/Aliorpse/jmutils.git"
        }
    }
}
