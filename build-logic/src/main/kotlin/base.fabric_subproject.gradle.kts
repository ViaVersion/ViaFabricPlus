// https://github.com/florianreuth/BaseProject

import net.fabricmc.loom.LoomGradleExtension

plugins {
    id("base.fabric")
}

// Compile against the Minecraft jar of the root project, including its class tweaker and injected interfaces
configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "net.minecraft") {
            val minecraftJars = LoomGradleExtension.get(rootProject).namedMinecraftProvider.minecraftJars
            check(minecraftJars.size == 1) {
                "Expected exactly one minecraft jar, got ${minecraftJars.size}"
            }
            val jar = minecraftJars.single()
            check(jar.isMerged) {
                "Expected minecraft jar to be merged"
            }
            val hash = jar.path.fileName.toString().substringAfter("minecraft-merged-").substringBefore("-")
            useTarget("${requested.group}:minecraft-merged-$hash:${requested.version}")
        }
    }
}

// Sources and runs are provided by the root project
tasks.configureEach {
    if (name.startsWith("genSources") || name.startsWith("run")) {
        enabled = false
    }
}
