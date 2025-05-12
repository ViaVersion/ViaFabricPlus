import de.florianmichael.baseproject.*

dependencies {
    loadFabricApiModules("fabric-lifecycle-events-v1")
    compileOnly(project(":viafabricplus-api"))
}
