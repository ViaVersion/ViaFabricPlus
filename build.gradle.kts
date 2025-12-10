import de.florianmichael.baseproject.*

plugins {
    id("net.fabricmc.fabric-loom-remap")
    id("de.florianmichael.baseproject.BaseProject")
}

allprojects {

    setupProject()
    setupFabricRemap()
    setupViaPublishing()

    repositories {
        maven("https://repo.viaversion.com")
        maven("https://maven.lenni0451.net/everything")
        maven("https://maven.terraformersmc.com/releases")
        maven("https://jitpack.io") {
            content {
                includeGroup("com.github.oryxel1")
            }
        }

        mavenLocal() // Uncomment during Minecraft updates for preview VV/VB builds
    }

}

subprojects {

    configureVVDependencies("compileOnlyApi")

}

base {
    archivesName.set("ViaFabricPlus") // Override the set name as it's lowercase for publishing
}

project.property("updating_minecraft").toString().toBoolean().apply {
    configureTestTasks(this)
    if (this) {
        increaseVisibleBuildErrors()
    }
}

val jij = configureJij()
configureVVDependencies("jij")

includeFabricApiModules("fabric-resource-loader-v1", "fabric-resource-loader-v0", "fabric-networking-api-v1", "fabric-command-api-v2", "fabric-lifecycle-events-v1", "fabric-particles-v1", "fabric-registry-sync-v0")
includeFabricSubmodule("viafabricplus-api")
includeFabricSubmodule("viafabricplus-visuals")

dependencies {
    testImplementation("net.fabricmc:fabric-loader-junit:${property("fabric_loader_version")}")
    modCompileOnly("com.terraformersmc:modmenu:15.0.0")

    jij("net.raphimc:MinecraftAuth:5.0.0") {
        exclude(group = "com.google.code.gson", module = "gson")
    }
    jij("net.lenni0451:Reflect:1.6.0")
    jij("dev.kastle.netty:netty-transport-raknet:1.4.0") {
        exclude(group = "io.netty")
    }
    jij("de.florianmichael:Classic4J:2.2.1")
}

includeTransitiveJijDependencies()

fun Project.configureVVDependencies(configuration: String) {
    dependencies {
        configuration("com.viaversion:viaversion-common:5.6.0-mc1.21.11-SNAPSHOT")
        configuration("com.viaversion:viabackwards-common:5.6.0-mc1.21.11-SNAPSHOT")
        configuration("com.viaversion:viaaprilfools-common:4.0.6-SNAPSHOT")
        configuration("com.viaversion:vialoader:4.0.6-SNAPSHOT") {
            exclude(group = "com.google.guava", module = "guava")
            exclude(group = "org.slf4j", module = "slf4j-api")
        }
        configuration("net.raphimc:ViaLegacy:3.0.11")
        configuration("net.raphimc:ViaBedrock:0.0.24-SNAPSHOT") {
            exclude(group = "com.mojang", module = "brigadier")
            exclude(group = "io.netty")
        }
    }
}
