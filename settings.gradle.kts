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
        // 每个节点代表一个代码兼容档；节点覆盖的版本区间记录在
        // versions/<节点>/gradle.properties 的 deps.minecraft.range（fabric 另有 dep）中。
        // 1.20.2/1.20.3、1.20.5、1.21.6 ~ 1.21.10（1.21.9 输入系统重构）未适配，不在支持范围内。

        version("1.20.1-fabric", "1.20.1").buildscript = "build.fabric.gradle.kts"
        version("1.20.1-forge", "1.20.1").buildscript = "build.forge.gradle.kts"

        // [1.20.4, 1.21.6)：1.20.4 ~ 1.21.5 同代码档，逐版本编译验证过
        version("1.20.4-fabric", "1.20.4").buildscript = "build.fabric.gradle.kts"
        version("1.20.4-neoforge", "1.20.4").buildscript = "build.neoforge.gradle.kts"

        version("1.20.6-neoforge", "1.20.6").buildscript = "build.neoforge.gradle.kts"

        // [1.21, 1.21.6)：1.21 ~ 1.21.5 同代码档
        version("1.21-neoforge", "1.21").buildscript = "build.neoforge.gradle.kts"

        version("1.21.11-fabric", "1.21.11").buildscript = "build.fabric-remap.gradle.kts"
        version("1.21.11-neoforge", "1.21.11").buildscript = "build.neoforge.gradle.kts"

        version("26.1-fabric", "26.1").buildscript = "build.fabric-unobf.gradle.kts"
        version("26.1-neoforge", "26.1").buildscript = "build.neoforge.gradle.kts"

        // [26.2, 26.4)：26.3 同代码档
        version("26.2-fabric", "26.2").buildscript = "build.fabric-unobf.gradle.kts"
        version("26.2-neoforge", "26.2").buildscript = "build.neoforge.gradle.kts"

        vcsVersion = "26.2-fabric"
    }
}

rootProject.name = "quick-chat"


if (System.getProperty("idea.sync.active") == "true") {
    println("IDEA sync detected: neutralizing idea.sync.active for multi-node compatibility")
    System.setProperty("idea.sync.active", "false")
}
