enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        // Keep them in sync with docs/DEVELOPER_API.md
        maven("https://repo.viaversion.com")
        maven("https://maven.lenni0451.net/everything")
        maven("https://maven.terraformersmc.com/releases")
        maven("https://jitpack.io") {
            content {
                includeGroup("com.github.oryxel1")
            }
        }
        mavenCentral()

        //mavenLocal() // Uncomment during Minecraft updates for preview VV/VB builds
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.florianreuth.de/releases")
        maven("https://maven.fabricmc.net/")
    }

    plugins {
        id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT"
        id("de.florianreuth.baseproject") version "2.0.0"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "viafabricplus"

include("viafabricplus-api")
include("viafabricplus-visuals")
