plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.5-fabric"

val allBuilds = stonecutter.tasks.named("build")

tasks.register("buildAll") {
    group = "build"
    description = "Builds the mod for every registered version and loader."
    dependsOn(allBuilds)
}
