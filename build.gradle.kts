import de.florianreuth.baseproject.core.unlockBuildErrors
import de.florianreuth.baseproject.integration.configureJarInJar
import de.florianreuth.baseproject.integration.configureTest
import de.florianreuth.baseproject.integration.fabricApiVersion
import de.florianreuth.baseproject.integration.includeTransitiveJijDependencies
import de.florianreuth.baseproject.integration.setupFabric
import de.florianreuth.baseproject.setupProject
import de.florianreuth.baseproject.setupViaPublishing

plugins {
    id("net.fabricmc.fabric-loom")
    id("de.florianreuth.baseproject")
}

allprojects {

    setupProject()
    setupFabric()
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

    tasks {
        runClient {
            enabled = false
        }
    }

}

configureTest().also {
    // Uncomment during Minecraft updates to update data diff files
    tasks.test.get().enabled = false
}
unlockBuildErrors()

val shade = configureJarInJar()

configureVVDependencies("jarInJar")

dependencies {
    shade(project(":viafabricplus-api")) {
        exclude("net.fabricmc", "fabric-loader")
    }
    shade(project(":viafabricplus-visuals")) {
        exclude("net.fabricmc", "fabric-loader")
    }

    shade(fabricApi.module("fabric-api-base", fabricApiVersion))
    shade(fabricApi.module("fabric-resource-loader-v1", fabricApiVersion))
    shade(fabricApi.module("fabric-resource-loader-v0", fabricApiVersion))
    shade(fabricApi.module("fabric-networking-api-v1", fabricApiVersion))
    shade(fabricApi.module("fabric-command-api-v2", fabricApiVersion))
    shade(fabricApi.module("fabric-lifecycle-events-v1", fabricApiVersion))
    shade(fabricApi.module("fabric-particles-v1", fabricApiVersion))
    shade(fabricApi.module("fabric-registry-sync-v0", fabricApiVersion))

    shade("net.lenni0451:Reflect:1.6.4")
    shade("de.florianreuth:classic4j:2.3.0")
    configureBedrockDependencies()

    compileOnly("com.terraformersmc:modmenu:20.0.0-beta.2")
}

includeTransitiveJijDependencies()

fun configureBedrockDependencies() {
    dependencies {
        shade("net.raphimc:MinecraftAuth:5.0.2") {
            exclude(group = "com.google.code.gson", module = "gson")
        }
        shade("dev.kastle.netty:netty-transport-raknet:1.7.0") {
            exclude(group = "io.netty")
        }
        shade("dev.kastle.netty:netty-transport-nethernet:1.7.0") {
            exclude(group = "io.netty")
        }
        arrayOf("windows-x86_64", "windows-aarch64", "linux-x86_64", "linux-aarch64", "macos-aarch64").forEach {
            shade("dev.kastle.webrtc:webrtc-java:1.0.3:$it")
        }
    }
}

fun Project.configureVVDependencies(configuration: String) {
    dependencies {
        configuration("com.viaversion:viaversion-common:5.12.0-20260819.184210-4")
        configuration("com.viaversion:viabackwards-common:5.12.0-20260805.160710-1")
        configuration("com.viaversion:viaaprilfools-common:4.2.3-20260820.140819-4")
        configuration("net.raphimc:ViaLegacy:3.1.0-20260821.100118-5")
        configuration("net.raphimc:ViaBedrock:0.0.29-20260720.172239-5") {
            exclude(group = "com.mojang", module = "brigadier")
            exclude(group = "at.yawk.lz4", module = "lz4-java")
            exclude(group = "io.netty")
        }
    }
}
