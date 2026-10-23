plugins {
    // Versioned via the catalog: subprojects inherit them through the classpath loaded here,
    // but the root itself can't resolve them version-less.
    alias(libs.plugins.dokka)
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

// Every project (including the implicit :kotlin parent) carries valid coordinates, so nothing publishes
// with group = rootProject.name.
allprojects {
    group = project.findProperty("projectGroup") as? String ?: "io.github.erkko68.filament"
    version = project.findProperty("libVersion") as? String ?: "0.1.0-SNAPSHOT"
}

// Shared tasks (prebuilts_<id>, downloadIncludes, setupEmsdk, generateBindings, cmakeBuild_<ios id>) come
// from the `filament-root` plugin applied above (build-logic/src/main/kotlin/filament-root.gradle.kts).
