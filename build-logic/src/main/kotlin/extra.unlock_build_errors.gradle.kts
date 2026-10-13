// https://github.com/florianreuth/BaseProject

plugins {
    id("base.java")
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "5000"))
}
