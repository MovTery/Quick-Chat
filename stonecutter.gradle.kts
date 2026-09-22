plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "26.2-fabric"

val allBuilds = stonecutter.tasks.named("build")

tasks.register("buildAll") {
    group = "build"
    description = "Builds the mod for every registered version and loader."
    dependsOn(allBuilds, "stonecutterSaveModels")
}

tasks.register("publishAllMods") {
    group = "publishing"
    description = "Publishes every version node's build to Modrinth and CurseForge."
    dependsOn(stonecutter.tasks.named("publishMods"))
}
