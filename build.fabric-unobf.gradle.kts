plugins {
    id("net.fabricmc.fabric-loom") version "1.18.2"
    id("me.modmuss50.mod-publish-plugin") version "2.2.1"
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
    "mixin_level" to "JAVA_21",
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
    maven("https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
}

dependencies {
    minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
    implementation("net.fabricmc:fabric-loader:${prop("deps.fabric.loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric.api")}")
    implementation("com.terraformersmc:modmenu:${prop("deps.modmenu")}")
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
    dependsOn(tasks.named("stonecutterGenerate"))
    exclude("META-INF/forge.mods.toml", "META-INF/neoforge.mods.toml")
    inputs.properties(expandProps())
    val props = expandProps()
    filesMatching(listOf("fabric.mod.json", "quick_chat.mixins.json", "pack.mcmeta")) {
        expand(props)
    }
}

apply(from = rootProject.file("gradle/publish.mods.gradle"))
