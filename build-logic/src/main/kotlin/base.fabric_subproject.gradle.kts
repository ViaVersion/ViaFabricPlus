// https://github.com/florianreuth/BaseProject

plugins {
    id("base.fabric")
}

tasks.configureEach {
    if (name.startsWith("genSources") || name.startsWith("run")) {
        enabled = false
    }
}
