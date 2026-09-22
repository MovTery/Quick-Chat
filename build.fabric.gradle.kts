plugins {
    id("fabric-loom") version "1.13.6"
}

stonecutter {
    constants["fabric"] = true
    constants["neoforge"] = false
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
    "minecraft_dep" to (if (hasProperty("deps.minecraft.dep")) prop("deps.minecraft.dep") else prop("deps.minecraft")),
    "fabric_loader_version" to prop("deps.fabric.loader"),
)

version = prop("mod.version")
base { archivesName = "${prop("mod.id")}-fabric-${prop("deps.minecraft")}" }

java {
    toolchain { languageVersion = JavaLanguageVersion.of(prop("deps.java").toInt()) }
    withSourcesJar()
}

repositories {
    maven("https://maven.parchmentmc.org/") { name = "ParchmentMC" }
    maven("https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
    maven("https://maven.nucleoid.xyz/") { name = "Nucleoid" }
}

dependencies {
    minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
    mappings(loom.layered {
        officialMojangMappings()
        if (hasProperty("deps.parchment")) {
            val (mc, ver) = prop("deps.parchment").split(':')
            parchment("org.parchmentmc.data:parchment-$mc:$ver@zip")
        }
    })
    modImplementation("net.fabricmc:fabric-loader:${prop("deps.fabric.loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric.api")}")
    modImplementation("com.terraformersmc:modmenu:${prop("deps.modmenu")}")
}

loom {
    mixin { defaultRefmapName = "${prop("mod.id")}.refmap.json" }
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
    exclude("META-INF/forge.mods.toml", "META-INF/neoforge.mods.toml")
    inputs.properties(expandProps())
    val props = expandProps()
    filesMatching(listOf("fabric.mod.json", "quick_chat.mixins.json", "pack.mcmeta")) {
        expand(props)
    }
}
