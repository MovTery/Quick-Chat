plugins {
    id("net.neoforged.moddev.legacyforge") version "2.0.141"
    id("me.modmuss50.mod-publish-plugin") version "2.2.1"
}

stonecutter {
    constants["fabric"] = false
    constants["neoforge"] = false
    constants["forge"] = true
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
    "forge_version" to prop("deps.forge"),
    "forge_loader_version_range" to prop("deps.forge.loader.range"),
)

version = prop("mod.version")
base { archivesName = "${prop("mod.id")}-forge-${prop("deps.minecraft")}" }

java {
    toolchain { languageVersion = JavaLanguageVersion.of(prop("deps.java").toInt()) }
    withSourcesJar()
}

repositories {
    maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
    maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
    maven("https://maven.parchmentmc.org/") { name = "ParchmentMC" }
    maven("https://repo.spongepowered.org/repository/maven-public/") { name = "Sponge" }
}

legacyForge {
    version = "${prop("deps.minecraft")}-${prop("deps.forge")}"
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

mixin {
    add(sourceSets.main.get(), "${prop("mod.id")}.refmap.json")
    config("${prop("mod.id")}.mixins.json")
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

dependencies {
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    implementation("org.jetbrains:annotations:24.0.1")
}

tasks.jar {
    finalizedBy(tasks.named("reobfJar"))
    manifest.attributes("MixinConfigs" to "${prop("mod.id")}.mixins.json")
}

tasks.processResources {
    dependsOn(tasks.named("stonecutterGenerate"))
    exclude("META-INF/neoforge.mods.toml", "fabric.mod.json")
    inputs.properties(expandProps())
    val props = expandProps()
    filesMatching("META-INF/forge.mods.toml") {
        expand(props)
    }
    eachFile {
        if (path == "META-INF/forge.mods.toml") {
            relativePath = RelativePath(true, "META-INF", "mods.toml")
        }
    }
    filesMatching(listOf("quick_chat.mixins.json", "pack.mcmeta")) {
        expand(props)
    }
}

apply(from = rootProject.file("gradle/publish.mods.gradle"))
