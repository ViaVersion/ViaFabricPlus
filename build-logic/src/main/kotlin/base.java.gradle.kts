plugins {
    `java-library`
}

group = property("project_group") as String
version = property("project_version") as String
description = property("project_description") as String

if (project == rootProject) {
    (findProperty("project_name") as String?)?.let { base.archivesName = it }
}

// The Kotlin plugin picks up the Java toolchain, so no Kotlin specific configuration is needed
val jvmVersion = (property("jvm_version") as String).toInt()
java {
    toolchain.languageVersion = JavaLanguageVersion.of(jvmVersion)
    sourceCompatibility = JavaVersion.toVersion(jvmVersion)
    targetCompatibility = JavaVersion.toVersion(jvmVersion)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-nowarn", "-Xlint:-unchecked", "-Xlint:-deprecation"))
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).apply {
        addBooleanOption("Xdoclint:none", true)
        quiet()
    }
}

tasks.jar {
    val projectName = project.name
    from("LICENSE") {
        rename { "LICENSE_$projectName" }
    }
}
