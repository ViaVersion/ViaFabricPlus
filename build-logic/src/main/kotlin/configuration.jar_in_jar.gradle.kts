// https://github.com/florianreuth/BaseProject

plugins {
    id("base.fabric")
}

val jarInJar = configurations.create("jarInJar")

configurations {
    implementation {
        extendsFrom(jarInJar)
    }
    include {
        extendsFrom(jarInJar)
    }
    api {
        extendsFrom(jarInJar)
    }
}
