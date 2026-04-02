import de.florianreuth.baseproject.*

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

}

project.property("updating_minecraft").toString().toBoolean().let {
    configureTestTasks(it)
    if (it) {
        increaseVisibleBuildErrors()
    }
}

val jij = configureApiJij()

configureVVDependencies("jij")

includeFabricApiModules("fabric-resource-loader-v1", "fabric-resource-loader-v0", "fabric-networking-api-v1", "fabric-command-api-v2", "fabric-lifecycle-events-v1", "fabric-particles-v1", "fabric-registry-sync-v0")

dependencies {
    jij(project(":viafabricplus-api"))
    jij(project(":viafabricplus-visuals"))

    jij("net.lenni0451:Reflect:1.6.2")
    jij("de.florianreuth:classic4j:2.3.0")
    configureBedrockDependencies()

    testImplementation("net.fabricmc:fabric-loader-junit:${property("fabric_loader_version")}")
    compileOnly("com.terraformersmc:modmenu:18.0.0-alpha.8")
}

includeTransitiveJijDependencies()

fun configureBedrockDependencies() {
    dependencies {
        jij("net.raphimc:MinecraftAuth:5.0.1-20260217.194827-4") {
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
        configuration("com.viaversion:viaversion-common:5.9.0-20260402.141911-21")
        configuration("com.viaversion:viabackwards-common:5.9.0-20260402.131711-8")
        configuration("com.viaversion:viaaprilfools-common:4.2.0-20260402.183812-7")
        configuration("net.raphimc:ViaLegacy:3.0.14")
        configuration("net.raphimc:ViaBedrock:0.0.27-20260402.214449-5") {
            exclude(group = "com.mojang", module = "brigadier")
            exclude(group = "at.yawk.lz4", module = "lz4-java")
            exclude(group = "io.netty")
        }
    }
}
