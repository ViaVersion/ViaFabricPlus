import de.florianreuth.baseproject.integration.setupFabric
import de.florianreuth.baseproject.setupProject
import de.florianreuth.baseproject.setupViaPublishing

plugins {
    id("java")
    id("net.fabricmc.fabric-loom")
    id("de.florianreuth.baseproject")
}

repositories {
    maven("https://repo.viaversion.com")
    //mavenLocal() // Uncomment during Minecraft updates for preview VV/VB builds
}

setupProject()
setupFabric()
setupViaPublishing()

dependencies {
    api("com.viaversion:viaversion-common:5.12.0-20260904.185715-7")
    api("com.viaversion:viabackwards-common:5.12.0-20260904.190714-4")
    api("com.viaversion:viaaprilfools-common:4.2.3-20260820.140819-4")
    api("net.raphimc:ViaLegacy:3.1.0-20260830.173114-8")
}

tasks {
    runClient {
        enabled = false
    }
}
