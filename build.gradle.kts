plugins {
    // Versioned via the catalog: the root applies no convention plugin, so unlike the
    // subprojects it can't resolve these version-less from the build-logic classpath.
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    id("filament-prebuilts")
}

// Plugin coordinates (kotlin, android, compose, vanniktech-publish) are pulled
// onto the classpath through build-logic/build.gradle.kts and applied by the
// `filament-kmp-module` convention plugin in each :kotlin:* module.

// ── API docs aggregation ──────────────────────────────────────────────────────
// Dokka V2 no longer auto-collects subprojects; the root gathers the documented
// modules explicitly. `dokkaGenerate` renders the multi-module site to
// build/dokka/html. :java and :web are excluded (FFM internals / generated externals).
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

// The Filament prebuilt/header download tasks (downloadPrebuilts, downloadPrebuilts_<target>,
// downloadIncludes) are registered by the `filament-prebuilts` convention plugin applied above
// (build-logic/src/main/kotlin/filament-prebuilts.gradle.kts).
