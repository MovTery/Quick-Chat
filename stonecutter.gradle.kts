plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "26.2-fabric"

val allBuilds = stonecutter.tasks.named("build")

tasks.register("buildAll") {
    group = "build"
    description = "Builds the mod for every registered version and loader."
    dependsOn(allBuilds)
}
