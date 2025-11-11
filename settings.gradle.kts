pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }

    plugins {
        id("fabric-loom") version "1.13-SNAPSHOT"
        id("de.florianmichael.baseproject.BaseProject") version "1.2.4"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "viafabricplus"

include("viafabricplus-api")
include("viafabricplus-visuals")
