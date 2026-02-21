import de.florianreuth.baseproject.*

plugins {
    id("net.fabricmc.fabric-loom-remap")
    id("de.florianreuth.baseproject")
}

allprojects {

    setupProject()
    setupFabricRemap()
    setupViaPublishing()

}

project.property("updating_minecraft").toString().toBoolean().let {
    configureTestTasks(it)
    if (it) {
        increaseVisibleBuildErrors()
    }
}

val jij = configureJij()

includeFabricApiModules(
    "fabric-resource-loader-v1",
    "fabric-resource-loader-v0",
    "fabric-networking-api-v1",
    "fabric-command-api-v2",
    "fabric-lifecycle-events-v1",
    "fabric-particles-v1",
    "fabric-registry-sync-v0"
)
includeFabricSubmodule("viafabricplus-api")
includeFabricSubmodule("viafabricplus-visuals")

dependencies {
    jij(libs.viaversion.common)
    jij(libs.viabackwards.common)
    jij(libs.viaaprilfools.common)
    jij(libs.vialegacy)
    jij(libs.viabedrock) {
        exclude(group = "com.mojang", module = "brigadier")
        exclude(group = "at.yawk.lz4", module = "lz4-java")
        exclude(group = "io.netty")
    }
    jij(libs.reflect)
    jij(libs.classic4j)
    jij(libs.minecraftauth) {
        exclude(group = "com.google.code.gson", module = "gson")
    }
    jij(libs.netty.raknet) {
        exclude(group = "io.netty")
    }
    jij(libs.netty.nethernet) {
        exclude(group = "io.netty")
    }
    jij(libs.webrtc.windows.x64)
    jij(libs.webrtc.windows.arm64)
    jij(libs.webrtc.linux.x64)
    jij(libs.webrtc.linux.arm64)
    jij(libs.webrtc.macos.arm64)
    modCompileOnly(libs.modmenu)

    testImplementation("net.fabricmc:fabric-loader-junit:${property("fabric_loader_version")}")
}

includeTransitiveJijDependencies()
