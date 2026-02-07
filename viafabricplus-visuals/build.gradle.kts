import de.florianreuth.baseproject.loadFabricApiModules

plugins {
    id("net.fabricmc.fabric-loom-remap")
}

dependencies {
    loadFabricApiModules("fabric-lifecycle-events-v1")
    compileOnly(project(":viafabricplus-api"))
}
