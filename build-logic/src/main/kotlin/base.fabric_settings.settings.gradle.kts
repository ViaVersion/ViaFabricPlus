// https://github.com/florianreuth/BaseProject

import net.fabricmc.loom.LoomRepositoryPlugin

plugins {
    id("base.settings")
}

// Loom then declares its repositories in the settings instead of adding them to every project
apply<LoomRepositoryPlugin>()
