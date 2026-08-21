import de.florianreuth.baseproject.integration.fabricApiVersion

plugins {
    id("net.fabricmc.fabric-loom")
}

dependencies {
    compileOnly(fabricApi.module("fabric-lifecycle-events-v1", fabricApiVersion))
    compileOnly(project(":viafabricplus-api"))
}
