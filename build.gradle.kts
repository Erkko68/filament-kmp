plugins {
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.kover")
}

// Plugin coordinates (kotlin, android, compose, vanniktech-publish) are pulled
// onto the classpath through buildSrc/build.gradle.kts and applied by the
// `filament-kmp-module` convention plugin in each :kotlin:* module.

// ── API docs aggregation ──────────────────────────────────────────────────────
// Dokka V2 no longer auto-collects subprojects; the root gathers the documented
// modules explicitly. `dokkaGenerate` renders the multi-module site to
// build/dokka/html. :java and :js are excluded (FFM internals / generated externals).
dependencies {
    dokka(project(":kotlin:filament"))
    dokka(project(":kotlin:filamat"))
    dokka(project(":kotlin:filament-utils"))
    dokka(project(":kotlin:gltfio"))
    dokka(project(":kotlin:filament-compose"))
}

// ── Test-coverage aggregation (Kover) ─────────────────────────────────────────
// Each :kotlin:* module applies the Kover plugin (via the filament-kmp-module convention
// plugin) so its test runs are instrumented; the root merges them into one report.
// Kover measures the JVM-executed tests (the `jvm` target + Android unit tests) — that's the
// common `expect` surface plus the JVM/FFM actuals. The js/native actuals run on their own
// runtimes Kover can't instrument, so they're out of these numbers by construction.
// Generate with `./gradlew koverHtmlReport` (build/reports/kover/html) or `koverXmlReport`.
dependencies {
    kover(project(":kotlin:filament"))
    kover(project(":kotlin:filamat"))
    kover(project(":kotlin:filament-utils"))
    kover(project(":kotlin:gltfio"))
    kover(project(":kotlin:filament-compose"))
}

// Ensure every project — including the implicit :kotlin and :java parent
// projects created by `include(":kotlin:filament")` — carries valid coordinates,
// so nothing accidentally publishes with group = rootProject.name.
allprojects {
    val baseGroup = project.findProperty("projectGroup") as? String ?: "io.github.erkko68.filament"
    group = if (path.startsWith(":java")) {
        // The :java module carries the JVM native runtime (Project Panama/FFM),
        // published as the `filament-ffm` artifact under a matching `-ffm` group.
        "$baseGroup-ffm"
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
//   • iosArm64 / iosSimulatorArm64 — Kotlin/Native iOS targets.
//   • macosArm64 / macosX64                 — JVM/Panama host (:java:*); macOS uses
//                                              the JVM build, not Kotlin/Native.
//   • linuxX64 / linuxArm64 / mingwX64      — JVM/Panama host on Linux/Windows.
//   • web                                   — Filament.js + WASM for the :js module;
//                                              output goes to prebuilts/web/ (no lib/ subdir).

val filaVersion = project.property("filaVersion") as String

val PREBUILT_TARGETS = listOf(
    "iosArm64",
    "iosSimulatorArm64",
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
        // Re-run when the version bumps or the script changes (the CRT variant /
        // tarball prefix per target lives in the script's TARGETS map). The script
        // itself stamps each extraction and re-extracts on any mismatch.
        inputs.property("filaVersion", filaVersion)
        inputs.file(downloadScript)
        outputs.dir(outDir)
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
