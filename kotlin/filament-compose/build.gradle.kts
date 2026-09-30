import buildlogic.resources.registerEmbeddedResources

plugins {
    id("filament-kmp-module")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

composeCompiler {
    // Teaches the compiler that Filament's native handles are stable — see the file for why.
    stabilityConfigurationFiles.add(layout.projectDirectory.file("compose-stability.conf"))

    // Stability/recomposition reports: -PcomposeMetrics. Output lands in build/compose-reports
    // (*-composables.txt, *-classes.txt, *-module.json).
    if (providers.gradleProperty("composeMetrics").isPresent) {
        metricsDestination = layout.buildDirectory.dir("compose-reports")
        reportsDestination = layout.buildDirectory.dir("compose-reports")
    }
}

// Built-in materials, precompiled with `matc -p all -a all` (runtime compilation isn't available on web);
// regenerate on each filaVersion bump, see docs/internals/upgrading-filament.md.
val generateEmbeddedMaterials = registerEmbeddedResources(
    taskName = "generateEmbeddedMaterials",
    inputDir = "src/commonMain/materials",
    fileExtension = ".filamat",
    packageName = "io.github.erkko68.filament.compose.scene",
    objectName = "EmbeddedMaterials",
)

// ── Embed test .glb assets into a generated commonTest source ────────────────
// Same rationale as the materials above (and mirroring :kotlin:gltfio): the committed .glb in
// src/commonTest/glb is the source of truth, base64-encoded so every target reads it without
// per-platform resource IO. Keep this set minimal — the bytes land in generated Kotlin source.
val generateEmbeddedGlb = registerEmbeddedResources(
    taskName = "generateEmbeddedGlb",
    inputDir = "src/commonTest/glb",
    fileExtension = ".glb",
    packageName = "io.github.erkko68.filament.compose.testutils",
    objectName = "EmbeddedGlb",
)

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir(generateEmbeddedMaterials)
        }
        named("commonTest") {
            kotlin.srcDir(generateEmbeddedGlb)
        }
        commonMain.dependencies {
            api(project(":kotlin:filament"))
            api(project(":kotlin:filament-utils"))
            api(project(":kotlin:gltfio"))
            api(project(":kotlin:filamat"))
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.ui)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
        commonTest.dependencies {
            implementation(libs.compose.uiTest)
        }
        // Android instrumented tests: runComposeUiTest needs a host Activity, supplied by the
        // ui-test-manifest artifact (it merges a debug AndroidManifest with a test ComponentActivity).
        // Lives only here — filament-compose is the only module that drives runComposeUiTest.
        named("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.compose.ui.test.manifest)
                // Override the stale Espresso (3.5.0) that compose ui-test drags in; the old one calls
                // an InputManager method removed in Android 14+, breaking waitForIdle() on-device.
                implementation(libs.androidx.test.espresso.core)
            }
        }
    }
}

// ── Published-artifact size trimming (Maven Central deployment bundle cap) ────
// Two payloads get duplicated across all seven per-target publications and dominate
// the deployment bundle. Neither is useful to a consumer.

// 1. EmbeddedMaterials.kt is 3.3 MB of generated base64. Seven sources jars carry it.
// KMP sources jars are org.gradle.jvm.tasks.Jar, not the bundling.Jar that `Jar` resolves to.
tasks.withType<org.gradle.jvm.tasks.Jar>().matching { it.name.endsWith("SourcesJar") }.configureEach {
    exclude("**/EmbeddedMaterials.kt")
}

// 2. The Compose plugin stages the skiko web runtime (9.8 MB, mostly skiko.wasm) into
//    jsMain resources for app bundling, and Kotlin/JS packs resources into the klib.
//    Consumer apps unpack their own copy. jsTest keeps its (the plugin stages that
//    compilation's resources separately), so Karma still serves skiko.
tasks.named<ProcessResources>("jsProcessResources") {
    exclude("skiko.wasm", "skiko.mjs", "skikod8.mjs", "js-reexport-symbols.mjs")
}
