import de.florianreuth.baseproject.loadFabricApiModules

dependencies {
    loadFabricApiModules("fabric-lifecycle-events-v1")
    compileOnly(project(":viafabricplus-api"))
}
