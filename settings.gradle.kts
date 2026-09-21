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

        // 1.21.6+ 起使用新一代 Loom（fabric-loom-remap）
        version("1.21.11-fabric", "1.21.11").buildscript = "build.fabric-remap.gradle.kts"
        version("1.21.11-neoforge", "1.21.11").buildscript = "build.neoforge.gradle.kts"
        version("26.1-fabric", "26.1").buildscript = "build.fabric-unobf.gradle.kts"
        version("26.1-neoforge", "26.1").buildscript = "build.neoforge.gradle.kts"
        version("26.2-fabric", "26.2").buildscript = "build.fabric-unobf.gradle.kts"
        version("26.2-neoforge", "26.2").buildscript = "build.neoforge.gradle.kts"

        vcsVersion = "1.21.5-fabric"
    }
}

rootProject.name = "quick-chat"


if (System.getProperty("idea.sync.active") == "true") {
    println("IDEA sync detected: neutralizing idea.sync.active for multi-node compatibility")
    System.setProperty("idea.sync.active", "false")
}
