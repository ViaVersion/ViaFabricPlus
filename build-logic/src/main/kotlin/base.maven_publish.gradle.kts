plugins {
    `java-library`
    `maven-publish`
    signing
}

val projectName = property("project_name") as String
val ownerId = property("publish_owner_id") as String
val ownerName = property("publish_owner_name") as String
val ownerMail = property("publish_owner_mail") as String
val githubAccount = findProperty("publish_github_account") as String? ?: ownerId
val licenseName = findProperty("publish_license") as String? ?: "Apache-2.0"
val githubRepository = "github.com/$githubAccount/$projectName"

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])

            pom {
                name = projectName
                // Lazy so that a description set in the build script body is picked up
                description = provider { project.description }
                url = "https://$githubRepository"
                licenses {
                    license {
                        name = licenseName
                        url = "https://$githubRepository/blob/main/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = ownerId
                        name = ownerName
                        email = ownerMail
                    }
                }
                scm {
                    connection = "scm:git:git://$githubRepository.git"
                    developerConnection = "scm:git:ssh://$githubRepository.git"
                    url = "https://$githubRepository"
                }
            }
        }
    }
}

signing {
    isRequired = false
    sign(publishing.publications)
}
