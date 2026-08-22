import de.florianreuth.baseproject.core.unlockBuildErrors
import de.florianreuth.baseproject.integration.configureJarInJar
import de.florianreuth.baseproject.integration.configureTest
import de.florianreuth.baseproject.integration.fabricApiVersion
import de.florianreuth.baseproject.integration.includeTransitiveJijDependencies
import de.florianreuth.baseproject.integration.setupFabric
import de.florianreuth.baseproject.setupProject
import de.florianreuth.baseproject.setupViaPublishing

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
        //mavenLocal() // Uncomment during Minecraft updates for preview VV/VB builds
    }

}

subprojects {

    dependencies {
        api("com.viaversion:viaversion-common:5.12.0-SNAPSHOT")
        api("com.viaversion:viabackwards-common:5.12.0-SNAPSHOT")
        api("com.viaversion:viaaprilfools-common:4.2.3-SNAPSHOT")
        api("net.raphimc:ViaLegacy:3.1.0-SNAPSHOT")
    }

    tasks {
        runClient {
            enabled = false
        }
    }

}

configureTest().also {
    // Comment during Minecraft updates to update data diff files
    tasks.test.get().enabled = false
}
unlockBuildErrors()

val shade = configureJarInJar()

dependencies {
    shade(project(":viafabricplus-api")) {
        exclude("net.fabricmc", "fabric-loader")
    }

    shade(fabricApi.module("fabric-api-base", fabricApiVersion))
    shade(fabricApi.module("fabric-resource-loader-v1", fabricApiVersion))
    shade(fabricApi.module("fabric-resource-loader-v0", fabricApiVersion))
    shade(fabricApi.module("fabric-networking-api-v1", fabricApiVersion))
    shade(fabricApi.module("fabric-command-api-v2", fabricApiVersion))
    shade(fabricApi.module("fabric-lifecycle-events-v1", fabricApiVersion))
    shade(fabricApi.module("fabric-particles-v1", fabricApiVersion))
    shade(fabricApi.module("fabric-registry-sync-v0", fabricApiVersion))

    shade("net.lenni0451:Reflect:1.6.4")
    shade("de.florianreuth:classic4j:2.3.0")

    compileOnly("com.terraformersmc:modmenu:20.0.0")
}

includeTransitiveJijDependencies()
