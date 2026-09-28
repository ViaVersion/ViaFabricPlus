plugins {
    `maven-publish`
}

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
