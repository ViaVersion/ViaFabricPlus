plugins {
    idea
}

idea {
    module {
        excludeDirs.add(file("run"))
    }
}
