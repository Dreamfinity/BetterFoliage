import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("forge") version "1.2-1.1.+"
    kotlin("jvm") version "2.3.21"
}

group = "com.github.octarine-noise"
version = property("mod_version") as String
base.archivesName.set("${rootProject.name}-MC${property("mc_version")}")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)
        apiVersion.set(KotlinVersion.KOTLIN_1_9)
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

minecraft {
    version = "${property("mc_version")}-${property("forge_version")}-${property("mc_version")}"
    mappings = "stable_12"
    runDir = "run"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${property("kotlin_version")}")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("mcversion", minecraft.version)
    filesMatching("mcmod.info") {
        expand("version" to project.version, "mcversion" to minecraft.version)
    }
}

val coremodManifest = mapOf(
    "FMLCorePlugin" to "mods.betterfoliage.loader.BetterFoliageLoader",
    "FMLCorePluginContainsFMLMod" to "mods.betterfoliage.BetterFoliageMod",
    "FMLAT" to "BetterFoliage_at.cfg"
)

tasks.jar {
    manifest { attributes(coremodManifest) }
    exclude("optifine/**")
}

val devJar by tasks.registering(Jar::class) {
    archiveClassifier.set("dev")
    from(sourceSets.main.get().output)
    manifest { attributes(coremodManifest) }
    exclude("optifine/**")
}

val devSourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("dev-sources")
    from(sourceSets.main.get().allSource)
    exclude("optifine/**")
}

tasks.named("assemble") { dependsOn(devJar, devSourcesJar) }
