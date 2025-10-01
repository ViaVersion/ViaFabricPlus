import de.florianmichael.baseproject.*

plugins {
    id("fabric-loom")
    id("de.florianmichael.baseproject.BaseProject")
}

allprojects {

    setupProject()
    setupFabric(yarnMapped())
    setupViaPublishing()

    repositories {
        mavenLocal()
        maven("https://repo.viaversion.com")
        maven("https://maven.lenni0451.net/everything")
        maven("https://repo.opencollab.dev/maven-snapshots")
        maven("https://maven.terraformersmc.com/releases")
        maven("https://jitpack.io") {
            content {
                includeGroup("com.github.Oryxel")
            }
        }
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

includeFabricApiModules("fabric-resource-loader-v0", "fabric-networking-api-v1", "fabric-command-api-v2", "fabric-lifecycle-events-v1", "fabric-particles-v1", "fabric-registry-sync-v0")
includeFabricSubmodule("viafabricplus-api")
includeFabricSubmodule("viafabricplus-visuals")

dependencies {
    testImplementation("net.fabricmc:fabric-loader-junit:${property("fabric_loader_version")}")
    modCompileOnly("com.terraformersmc:modmenu:15.0.0")

    jij("net.raphimc:MinecraftAuth:4.1.2") {
        exclude(group = "com.google.code.gson", module = "gson")
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    jij("net.lenni0451:Reflect:1.5.0")
    jij("org.cloudburstmc.netty:netty-transport-raknet:1.0.0.CR3-SNAPSHOT") {
        exclude(group = "io.netty")
    }
    jij("de.florianmichael:Classic4J:2.1.0")
}

includeTransitiveJijDependencies()

fun Project.configureVVDependencies(configuration: String) {
    dependencies {
        configuration("com.viaversion:viaversion-common:5.5.1-20251003.180311-1")
        configuration("com.viaversion:viabackwards-common:5.5.1-20251003.194311-1")
        configuration("com.viaversion:viaaprilfools-common:4.0.4")
        configuration("com.viaversion:vialoader:4.0.5") {
            exclude(group = "com.google.guava", module = "guava")
            exclude(group = "org.slf4j", module = "slf4j-api")
        }
        configuration("net.raphimc:ViaLegacy:3.0.11")
        configuration("net.raphimc:ViaBedrock:0.0.22-20251003.200622-1") {
            exclude(group = "io.jsonwebtoken")
            exclude(group = "com.mojang", module = "brigadier")
        }
    }
}
