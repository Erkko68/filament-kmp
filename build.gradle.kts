plugins {
    // Versioned via the catalog: the root applies no convention plugin, so unlike the
    // subprojects it can't resolve these version-less from the build-logic classpath.
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    alias(libs.plugins.binaryCompatibilityValidator)
    id("filament-root")
}

// ── Public-API surface guard (binary-compatibility-validator) ─────────────────
// `apiDump` records each published :kotlin:* module's public JVM ABI under
// <module>/api/; `apiCheck` (wired into `check`, so it runs on CI) fails on any
// undeclared change. Regenerate the dumps deliberately when the API changes.
// Klib (native/js) validation is left off for now: dumps would differ by CI host
// (Linux runners can't build the iOS klibs). The JVM dump already covers the
// common `expect` surface.
apiValidation {
    ignoredProjects += listOf(
        "test-support", // internal test helpers, not published
        // Native runtimes: interop plumbing, not a curated API.
        "web", "jni", "android", "desktop",
        "runtime-macos-arm64", "runtime-linux-x64", "runtime-linux-arm64", "runtime-windows-x64", "runtime-windows-arm64",
    )
}

// Plugin coordinates (kotlin, android, compose, vanniktech-publish) are pulled
// onto the classpath through build-logic/build.gradle.kts and applied by the
// `filament-kmp-module` convention plugin in each :kotlin:* module.

// ── API docs aggregation ──────────────────────────────────────────────────────
// Dokka V2 no longer auto-collects subprojects; the root gathers the documented
// modules explicitly. `dokkaGenerate` renders the multi-module site to
// build/dokka/html. The native runtimes (:jni, :desktop, :android, :web) are interop plumbing, not documented.
dependencies {
    dokka(project(":kotlin:filament"))
    dokka(project(":kotlin:filamat"))
    dokka(project(":kotlin:filament-utils"))
    dokka(project(":kotlin:gltfio"))
    dokka(project(":kotlin:filament-compose"))
    dokkaPlugin(libs.dokka.versioningPlugin)
}

// ── API docs versioning ───────────────────────────────────────────────────────
// The versioning plugin renders the version dropdown and copies each directory of
// build/previousDocs/<version> into the site under `older/`, so one deploy serves
// every release. CI restores that dir from the `docs-archive` branch and pushes the
// freshly built version back; locally it's absent and only this version renders.
dokka {
    pluginsConfiguration.versioning {
        version = providers.gradleProperty("libVersion").getOrElse("0.1.0-SNAPSHOT")
        olderVersionsDir = layout.buildDirectory.dir("previousDocs")
    }
}

// ── Test-coverage aggregation (Kover) ─────────────────────────────────────────
// Each :kotlin:* module applies the Kover plugin (via the filament-kmp-module convention
// plugin) so its test runs are instrumented; the root merges them into one report.
// Kover measures the JVM-executed tests (the `jvm` target + Android unit tests) — that's the
// common `expect` surface plus the JNI actuals. The js/native actuals run on their own
// runtimes Kover can't instrument, so they're out of these numbers by construction.
// Generate with `./gradlew koverHtmlReport` (build/reports/kover/html) or `koverXmlReport`.
dependencies {
    kover(project(":kotlin:filament"))
    kover(project(":kotlin:filamat"))
    kover(project(":kotlin:filament-utils"))
    kover(project(":kotlin:gltfio"))
    kover(project(":kotlin:filament-compose"))
}

// Every project (including the implicit :kotlin parent) carries valid coordinates, so nothing publishes
// with group = rootProject.name.
allprojects {
    group = project.findProperty("projectGroup") as? String ?: "io.github.erkko68.filament"
    version = project.findProperty("libVersion") as? String ?: "0.1.0-SNAPSHOT"
}

// Shared tasks (prebuilts_<id>, downloadIncludes, setupEmsdk, generateBindings, cmakeBuild_<ios id>) come
// from the `filament-root` plugin applied above (build-logic/src/main/kotlin/filament-root.gradle.kts).
