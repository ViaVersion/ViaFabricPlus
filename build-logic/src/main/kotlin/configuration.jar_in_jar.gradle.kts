plugins {
    id("net.fabricmc.fabric-loom")
}

val jarInJar = configurations.create("jarInJar")

configurations {
    implementation { extendsFrom(jarInJar) }
    include { extendsFrom(jarInJar) }
    api { extendsFrom(jarInJar) }
}
