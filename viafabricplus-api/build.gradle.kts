plugins {
    id("base.fabric_subproject")
    id("via.maven_publish")
}

dependencies {
    api(libs.viaversion.common)
    api(libs.viabackwards.common)
    api(libs.viaaprilfools.common)
    api(libs.vialegacy)
}
