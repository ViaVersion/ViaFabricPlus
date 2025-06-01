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
    archivesName.set("ViaFabricPlus")
}

configureTestTasks(project.findProperty("updating_minecraft") as? Boolean ?: false)

val jij = configureJij()
configureVVDependencies("jij")

includeFabricApiModules("fabric-resource-loader-v0", "fabric-networking-api-v1", "fabric-command-api-v2", "fabric-lifecycle-events-v1", "fabric-particles-v1", "fabric-registry-sync-v0")
includeFabricSubmodule("viafabricplus-api")
includeFabricSubmodule("viafabricplus-visuals")

dependencies {
    testImplementation("net.fabricmc:fabric-loader-junit:${property("fabric_loader_version")}")
    modCompileOnly("com.terraformersmc:modmenu:14.0.0-rc.2")

    jij("net.raphimc:MinecraftAuth:4.1.1") {
        exclude(group = "com.google.code.gson", module = "gson")
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    jij("net.lenni0451:Reflect:1.5.0")
    jij("org.cloudburstmc.netty:netty-transport-raknet:1.0.0.CR3-SNAPSHOT") {
        exclude(group = "io.netty")
    }
    jij("de.florianmichael:Classic4J:2.1.1-SNAPSHOT")
}

includeTransitiveJijDependencies()

fun Project.configureVVDependencies(configuration: String) {
    dependencies {
        configuration("com.viaversion:viaversion-common:5.4.0-20250528.054211-12")
        configuration("com.viaversion:viabackwards-common:5.4.0-20250531.004611-7")
        configuration("com.viaversion:viaaprilfools-common:4.0.2-20250522.150521-27")
        configuration("com.viaversion:vialoader:4.0.3-20250522.150416-15") {
            exclude(group = "com.google.guava", module = "guava")
            exclude(group = "org.slf4j", module = "slf4j-api")
        }
        configuration("net.raphimc:ViaLegacy:3.0.10-20250527.181609-19")
        configuration("net.raphimc:ViaBedrock:0.0.17-20250531.210304-6") {
            exclude(group = "io.jsonwebtoken")
            exclude(group = "com.mojang", module = "brigadier")
        }
    }
}
