import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
        exceptionFormat = TestExceptionFormat.FULL
    }

    val runDir = layout.projectDirectory.dir("run").asFile
    if (runDir.isDirectory) {
        workingDir = runDir
    }
}

pluginManager.withPlugin("net.fabricmc.fabric-loom") {
    dependencies {
        "testImplementation"(project.the<VersionCatalogsExtension>().named("libs").findLibrary("fabric-loader-junit").get())
    }
}
