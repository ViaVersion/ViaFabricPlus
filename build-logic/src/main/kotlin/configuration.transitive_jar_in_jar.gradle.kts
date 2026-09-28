plugins {
    id("configuration.jar_in_jar")
}

val jarInJar = configurations.named("jarInJar")
listOf("api", "implementation", "include").forEach { targetName ->
    configurations.named(targetName) {
        defaultDependencies {
            jarInJar.get().incoming.resolutionResult.allComponents
                // Platforms such as BOMs have no jar and must not be added as a library
                .filterNot { component -> component.variants.any { it.isPlatform() } }
                .mapNotNull { it.id as? ModuleComponentIdentifier }
                .forEach { id ->
                    add(project.dependencies.create("${id.group}:${id.module}:${id.version}") {
                        isTransitive = false
                    })
                }
        }
    }
}

// Resolved attributes may be desugared to strings, so the category is matched by name
fun ResolvedVariantResult.isPlatform(): Boolean {
    val category = attributes.keySet().firstOrNull { it.name == Category.CATEGORY_ATTRIBUTE.name } ?: return false
    return attributes.getAttribute(category).toString() in setOf(Category.REGULAR_PLATFORM, Category.ENFORCED_PLATFORM)
}
