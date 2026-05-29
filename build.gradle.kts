// Plugin coordinates (kotlin, android, compose, vanniktech-publish) are pulled
// onto the classpath through buildSrc/build.gradle.kts and applied by the
// `filament-kmp-module` convention plugin in each :kotlin:* module.

// Ensure every project — including the implicit :kotlin and :java parent
// projects created by `include(":kotlin:filament")` — carries valid coordinates,
// so nothing accidentally publishes with group = rootProject.name.
allprojects {
    val baseGroup = project.findProperty("projectGroup") as? String ?: "io.github.erkko68.filament"
    group = if (path.startsWith(":java")) {
        "$baseGroup-jni"
    } else {
        baseGroup
    }
    version = project.findProperty("libVersion") as? String ?: "0.1.0-SNAPSHOT"

    // Module declarations live under java/*. The "erkko68" component triggers a JLS §6.1
    // advisory warning about terminal digits in module names; harmless but noisy in CI.
    if (path.startsWith(":java")) {
        tasks.withType<JavaCompile>().configureEach {
            options.compilerArgs.add("-Xlint:-module")
        }
    }
}

// ── Filament prebuilts download ───────────────────────────────────────────────
//
// One Exec task per prebuilt target — invokes scripts/gradle/download_filament_prebuilts.py.
// Tasks live at the root project so they are shared across :kotlin:* and :java:*.
//
// Targets correspond to:
//   • iosArm64 / iosSimulatorArm64 / iosX64 — Kotlin/Native iOS targets.
//   • macosArm64 / macosX64                 — JVM/JNI host (:java:*); macOS uses
//                                              the JVM build, not Kotlin/Native.
//   • linuxX64 / linuxArm64 / mingwX64      — JVM/JNI host on Linux/Windows.
//   • web                                   — Filament.js + WASM for the :js module;
//                                              output goes to prebuilts/web/ (no lib/ subdir).

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
    "web",
)

val pythonExe = providers.environmentVariable("PYTHON").orElse("python3")
val downloadScript = layout.projectDirectory.file("scripts/gradle/download_filament_prebuilts.py")
val downloadIncludesScript = layout.projectDirectory.file("scripts/gradle/download_filament_includes.py")

PREBUILT_TARGETS.forEach { target ->
    tasks.register<Exec>("downloadPrebuilts_$target") {
        group = "filament"
        description = "Downloads Filament $filaVersion prebuilt libraries for $target."
        // Web prebuilts land directly in prebuilts/web/; all others in prebuilts/<target>/lib/.
        val outDir = if (target == "web") {
            layout.projectDirectory.dir("prebuilts/web").asFile
        } else {
            layout.projectDirectory.dir("prebuilts/$target/lib").asFile
        }
        outputs.dir(outDir)
        outputs.upToDateWhen { outDir.exists() && outDir.listFiles()?.isNotEmpty() == true }
        commandLine(pythonExe.get(), downloadScript.asFile.absolutePath, filaVersion, target)
    }
}

tasks.register<Exec>("downloadIncludes") {
    group = "filament"
    description = "Downloads Filament $filaVersion public headers into include/."
    val stamp = layout.projectDirectory.file("include/.filament-version").asFile
    val expectedVersion = filaVersion  // capture as local so the closure isn't tied to script scope
    outputs.file(stamp)
    outputs.upToDateWhen { stamp.exists() && stamp.readText().trim() == expectedVersion }
    commandLine(pythonExe.get(), downloadIncludesScript.asFile.absolutePath, filaVersion)
}

tasks.register("downloadPrebuilts") {
    group = "filament"
    description = "Downloads Filament $filaVersion prebuilt libraries + headers for all targets."
    dependsOn(PREBUILT_TARGETS.map { "downloadPrebuilts_$it" })
    dependsOn("downloadIncludes")
}
