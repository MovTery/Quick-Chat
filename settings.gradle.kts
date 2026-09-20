pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9.8"
}

stonecutter {
    create(rootProject) {
        fun match(minecraft: String, vararg loaders: String) =
            loaders.forEach { loader ->
                version("$minecraft-$loader", minecraft).buildscript = "build.$loader.gradle.kts"
            }

        match("1.20.1", "fabric", "forge")
        match("1.20.4", "fabric", "neoforge")
        match("1.20.6", "fabric", "neoforge")
        match("1.21", "fabric", "neoforge")
        match("1.21.1", "fabric", "neoforge")
        match("1.21.2", "fabric", "neoforge")
        match("1.21.3", "fabric", "neoforge")
        match("1.21.4", "fabric", "neoforge")
        match("1.21.5", "fabric", "neoforge")

        vcsVersion = "1.21.5-fabric"
    }
}

rootProject.name = "quick-chat"
