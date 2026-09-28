pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("base.settings")
    id("base.fabric_settings")
}

dependencyResolutionManagement {
    repositories {
        maven("https://repo.viaversion.com")
        maven("https://maven.terraformersmc.com/releases")
        //mavenLocal() // Uncomment during Minecraft updates for preview VV/VB builds
    }
}

rootProject.name = "viafabricplus"

include("viafabricplus-api")
