import de.florianmichael.baseproject.*

description = "Additional mod for ViaFabricPlus to add visual-only changes."

dependencies {
    loadFabricApiModules("fabric-lifecycle-events-v1")
    compileOnly(project(":viafabricplus-api"))
}
