plugins {
    id("java")
    id("base.java")
    id("base.fabric")
    id("via.maven_publish")
}

dependencies {
    api(libs.viaversion.common)
    api(libs.viabackwards.common)
    api(libs.viaaprilfools.common)
    api(libs.vialegacy)
}

tasks {
    runClient {
        enabled = false
    }
}
