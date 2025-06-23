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

configureTestTasks(project.property("updating_minecraft").toString().toBoolean())

val jij = configureJij()
configureVVDependencies("jij")

includeFabricApiModules("fabric-resource-loader-v0", "fabric-networking-api-v1", "fabric-command-api-v2", "fabric-lifecycle-events-v1", "fabric-particles-v1", "fabric-registry-sync-v0")
includeFabricSubmodule("viafabricplus-api")
includeFabricSubmodule("viafabricplus-visuals")

dependencies {
    testImplementation("net.fabricmc:fabric-loader-junit:${property("fabric_loader_version")}")
    modCompileOnly("com.terraformersmc:modmenu:15.0.0-beta.3")

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
        configuration("com.viaversion:viaversion-common:5.4.1-20250621.171704-5")
        configuration("com.viaversion:viabackwards-common:5.4.0")
        configuration("com.viaversion:viaaprilfools-common:4.0.2")
        configuration("com.viaversion:vialoader:4.0.3") {
            exclude(group = "com.google.guava", module = "guava")
            exclude(group = "org.slf4j", module = "slf4j-api")
        }
        configuration("net.raphimc:ViaLegacy:3.0.10")
        configuration("net.raphimc:ViaBedrock:0.0.18-20250620.232050-1") {
            exclude(group = "io.jsonwebtoken")
            exclude(group = "com.mojang", module = "brigadier")
        }
    }
}
