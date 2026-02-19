import de.florianreuth.baseproject.*

plugins {
    id("net.fabricmc.fabric-loom-remap")
    id("de.florianreuth.baseproject")
}

allprojects {

    setupProject()
    setupFabricRemap()
    setupViaPublishing()

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

        //mavenLocal() // Uncomment during Minecraft updates for preview VV/VB builds
    }

}

subprojects {

    configureVVDependencies("api")

}

base {
    archivesName.set("ViaFabricPlus") // Override the set name as it's lowercase for publishing
}

project.property("updating_minecraft").toString().toBoolean().let {
    configureTestTasks(it)
    if (it) {
        increaseVisibleBuildErrors()
    }
}

val jij = configureJij()
configureVVDependencies("jij")

includeFabricApiModules("fabric-resource-loader-v1", "fabric-resource-loader-v0", "fabric-networking-api-v1", "fabric-command-api-v2", "fabric-lifecycle-events-v1", "fabric-particles-v1", "fabric-registry-sync-v0")
includeFabricSubmodule("viafabricplus-api")
includeFabricSubmodule("viafabricplus-visuals")

dependencies {
    jij("net.lenni0451:Reflect:1.6.1")
    jij("de.florianreuth:classic4j:2.3.0")
    configureBedrockDependencies()

    testImplementation("net.fabricmc:fabric-loader-junit:${property("fabric_loader_version")}")
    modCompileOnly("com.terraformersmc:modmenu:16.0.0")
}

includeTransitiveJijDependencies()

fun configureBedrockDependencies() {
    dependencies {
        jij("net.raphimc:MinecraftAuth:5.0.1-SNAPSHOT") {
            exclude(group = "com.google.code.gson", module = "gson")
        }
        jij("dev.kastle.netty:netty-transport-raknet:1.7.0") {
            exclude(group = "io.netty")
        }
        jij("dev.kastle.netty:netty-transport-nethernet:1.7.0") {
            exclude(group = "io.netty")
        }
        arrayOf("windows-x86_64", "windows-aarch64", "linux-x86_64", "linux-aarch64", "macos-aarch64").forEach {
            jij("dev.kastle.webrtc:webrtc-java:1.0.3:$it")
        }
    }
}

fun Project.configureVVDependencies(configuration: String) {
    dependencies {
        configuration("com.viaversion:viaversion-common:5.7.2-SNAPSHOT")
        configuration("com.viaversion:viabackwards-common:5.7.2-SNAPSHOT")
        configuration("com.viaversion:viaaprilfools-common:4.0.8")
        configuration("net.raphimc:ViaLegacy:3.0.13")
        configuration("net.raphimc:ViaBedrock:0.0.26-SNAPSHOT") {
            exclude(group = "com.mojang", module = "brigadier")
            exclude(group = "at.yawk.lz4", module = "lz4-java")
            exclude(group = "io.netty")
        }
    }
}
