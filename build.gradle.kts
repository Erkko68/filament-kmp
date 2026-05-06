// Plugin coordinates (kotlin, android, compose, vanniktech-publish) are pulled
// onto the classpath through buildSrc/build.gradle.kts and applied by the
// `filament-kmp-module` convention plugin in each :kotlin:* module.

// ── Filament prebuilts download ───────────────────────────────────────────────
//
// One Exec task per prebuilt target — invokes scripts/download_filament_prebuilts.py.
// Tasks live at the root project so they are shared across :kotlin:* and :java:*.
//
// Targets correspond to:
//   • iosArm64 / iosSimulatorArm64 / iosX64 — Kotlin/Native iOS targets.
//   • macosArm64 / macosX64                 — JVM/JNI host (:java:*); macOS uses
//                                              the JVM build, not Kotlin/Native.
//   • linuxX64 / linuxArm64 / mingwX64      — JVM/JNI host on Linux/Windows.

val filaVersion: String by project

val PREBUILT_TARGETS = listOf(
    "iosArm64",
    "iosSimulatorArm64",
    "iosX64",
    "macosArm64",
    "macosX64",
    "linuxX64",
    "linuxArm64",
    "mingwX64",
)

val pythonExe = providers.environmentVariable("PYTHON").orElse("python3")
val downloadScript = layout.projectDirectory.file("scripts/download_filament_prebuilts.py")

PREBUILT_TARGETS.forEach { target ->
    tasks.register<Exec>("downloadPrebuilts_$target") {
        group = "filament"
        description = "Downloads Filament $filaVersion prebuilt static libraries for $target."
        val outDir = layout.projectDirectory.dir("prebuilts/$target/lib").asFile
        outputs.dir(outDir)
        outputs.upToDateWhen { outDir.exists() && outDir.listFiles()?.isNotEmpty() == true }
        commandLine(pythonExe.get(), downloadScript.asFile.absolutePath, filaVersion, target)
    }
}

tasks.register("downloadPrebuilts") {
    group = "filament"
    description = "Downloads Filament $filaVersion prebuilt libraries for all targets."
    dependsOn(PREBUILT_TARGETS.map { "downloadPrebuilts_$it" })
}
