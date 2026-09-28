plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    // Repositories live in the settings only, a project or plugin declaring its own fails the build
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
        maven("https://maven.florianreuth.de/releases")
    }
}
