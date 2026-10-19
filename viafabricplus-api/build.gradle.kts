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
    api("com.viaversion:viaversion-common:5.12.2-20261019.183311-8")
    api("com.viaversion:viabackwards-common:5.12.2-20261018.201611-8")
    api("com.viaversion:viaaprilfools-common:4.2.4-20261001.112613-2")
    api("net.raphimc:ViaLegacy:3.1.1-20261019.113714-10")
}

tasks {
    runClient {
        enabled = false
    }
}
