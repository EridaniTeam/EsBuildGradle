plugins {
    kotlin("jvm") version "1.5.31"
    `maven-publish`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.14.0"
}

group = "club.eridani"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
}

gradlePlugin {
    plugins {
        create("esbuild-gradle") {
            id = "club.eridani.esbuild-gradle"
            displayName = "Esbuild Gradle"
            description = "Esbuild support for Kotlin/JS through Gradle"
            implementationClass = "club.eridani.esbuild.EsbuildGradlePlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/SexyTeam/EsBuildGradle"
    vcsUrl = "https://github.com/SexyTeam/EsBuildGradle"
    tags = listOf("Kotlin", "Kotlin/JS", "javascript", "esbuild")
}

publishing {
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri("file://${System.getProperty("user.home")}/eskid_maven")
        }
    }
}