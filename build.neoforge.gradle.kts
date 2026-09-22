plugins {
    id("net.neoforged.moddev") version "2.0.147"
}

stonecutter {
    constants["fabric"] = false
    constants["neoforge"] = true
    constants["forge"] = false
}

fun Project.prop(name: String): String = property(name) as String

fun Project.expandProps(): Map<String, String> = mapOf(
    "version" to prop("mod.version"),
    "mod_id" to prop("mod.id"),
    "mod_name" to prop("mod.name"),
    "mod_author" to prop("mod.author"),
    "license" to prop("mod.license"),
    "description" to prop("mod.description"),
    "java_version" to prop("deps.java"),
    "mixin_level" to if (prop("deps.java") == "21") "JAVA_21" else "JAVA_17",
    "minecraft_version_range" to prop("deps.minecraft.range"),
    "neoforge_version" to prop("deps.neoforge"),
    "neoforge_version_range" to prop("deps.neoforge.range"),
    "neoforge_loader_version_range" to prop("deps.neoforge.loader.range"),
    //NeoForge 26.2 起废弃 logoFile
    "mod_logo_property" to if (sc.current.parsed < "26.2") "logoFile" else "iconFile",
)

version = prop("mod.version")
base { archivesName = "${prop("mod.id")}-neoforge-${prop("deps.minecraft")}" }

java {
    toolchain { languageVersion = JavaLanguageVersion.of(prop("deps.java").toInt()) }
    withSourcesJar()
}

repositories {
    maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
    maven("https://maven.parchmentmc.org/") { name = "ParchmentMC" }
}

neoForge {
    version = prop("deps.neoforge")
    if (hasProperty("deps.parchment")) {
        val (mc, ver) = prop("deps.parchment").split(':')
        parchment {
            minecraftVersion = mc
            mappingsVersion = ver
        }
    }
    runs {
        register("client") {
            client()
        }
    }
    mods {
        register(prop("mod.id")) {
            sourceSet(sourceSets.main.get())
        }
    }
}

tasks.named("createMinecraftArtifacts") {
    dependsOn(tasks.named("stonecutterGenerate"))
}

val generatedStonecutter = layout.buildDirectory.dir("generated/stonecutter/main")

sourceSets.main {
    if (stonecutter.current.isActive) {
        java.setSrcDirs(listOf(rootDir.resolve("src/main/java")))
        resources.setSrcDirs(listOf(rootDir.resolve("src/main/resources")))
    } else {
        java.setSrcDirs(listOf(generatedStonecutter.map { it.dir("java") }))
        resources.setSrcDirs(listOf(generatedStonecutter.map { it.dir("resources") }))
    }
}

tasks.compileJava { dependsOn(tasks.named("stonecutterGenerate")) }
tasks.withType<ProcessResources>().configureEach { dependsOn(tasks.named("stonecutterGenerate")) }
tasks.withType<Jar>().configureEach { dependsOn(tasks.named("stonecutterGenerate")) }

tasks.processResources {
    exclude("META-INF/forge.mods.toml", "fabric.mod.json")
    inputs.properties(expandProps())
    val props = expandProps()
    // NeoForge 1.20.4 及以下读取 META-INF/mods.toml，1.20.5 起读取 META-INF/neoforge.mods.toml
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(props)
    }
    if (sc.current.parsed < "1.20.5") {
        eachFile {
            if (path == "META-INF/neoforge.mods.toml") {
                relativePath = RelativePath(true, "META-INF", "mods.toml")
            }
        }
    }
    filesMatching(listOf("quick_chat.mixins.json", "pack.mcmeta")) {
        expand(props)
    }
}
