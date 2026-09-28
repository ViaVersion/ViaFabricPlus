import de.florianreuth.baseproject.latestCommitHash

plugins {
    id("net.fabricmc.fabric-loom")
    id("idea.exclude_run_dir")
}

// Precompiled script plugins have no type-safe accessors for the consumer's catalog
val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    minecraft(libs.findLibrary("minecraft").get())
    implementation(libs.findLibrary("fabric-loader").get())
}

val accessWidenerFile = file("src/main/resources/${project.name.lowercase()}.accesswidener")
if (accessWidenerFile.exists()) {
    loom {
        accessWidenerPath = accessWidenerFile
    }
}

val supportedMcVersion = (findProperty("supported_minecraft_versions") as String?)?.ifEmpty { null }
    ?: libs.findVersion("minecraft").get().requiredVersion
tasks.processResources {
    val projectName = project.name
    val projectVersion = project.version
    val projectDescription = project.description
    val mcVersion = supportedMcVersion
    val latestCommitHash = latestCommitHash()
    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to projectVersion,
                "implVersion" to "git-${projectName}-${projectVersion}:${latestCommitHash}",
                "description" to projectDescription,
                "mcVersion" to mcVersion,
                "commitHash" to latestCommitHash,
                "shortCommitHash" to latestCommitHash.take(7)
            )
        )
    }
}
