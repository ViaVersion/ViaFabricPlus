import de.florianreuth.baseproject.integration.fabricApiVersion

plugins {
    id("net.fabricmc.fabric-loom")
}

dependencies {
    compileOnly(fabricApi.module("fabric-lifecycle-events-v1", fabricApiVersion))
    compileOnly("com.viaversion:viaversion-api:5.11.1-SNAPSHOT")
    compileOnly("net.raphimc:ViaLegacy:3.1.0-SNAPSHOT")
    compileOnly(project(":viafabricplus-api"))
}
