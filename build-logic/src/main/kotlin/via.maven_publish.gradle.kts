// https://github.com/florianreuth/BaseProject

plugins {
    id("maven-publish")
}

// base.maven_publish reads these when applied, so it is applied only after they are set
extra["publish_github_account"] = "ViaVersion"
extra["publish_license"] = "GPL-3.0"
apply(plugin = "base.maven_publish")

publishing {
    repositories {
        maven {
            name = "Via"
            url = uri("https://repo.viaversion.com/")
            credentials {
                username = findProperty("ViaUsername") as String?
                password = findProperty("ViaPassword") as String?
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}
