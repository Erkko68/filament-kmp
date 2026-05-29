import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("filament-publish")
}

// ── Project coordinates (previously in root allprojects {}) ───────────────────
group   = project.findProperty("projectGroup") as? String ?: "io.github.erkko68.filament"
version = project.findProperty("libVersion")   as? String ?: "0.1.0-SNAPSHOT"

// ── Extension to configure per-module XCFramework name ────────────────────────
abstract class FilamentModuleExtension {
    abstract val xcframeworkName: Property<String>
}

val filamentModuleExt = extensions.create("filamentModule", FilamentModuleExtension::class.java).apply {
    xcframeworkName.convention("")
}

// ── Kotlin multiplatform target declarations ──────────────────────────────────
kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
    androidTarget {
        publishLibraryVariants("release", "debug")
        // Make `androidInstrumentedTest` inherit from `commonTest` so the
        // shared `expect`s (e.g. createTestSurface, TestMaterials) line up
        // with the per-Android `actual`s. The default hierarchy template
        // only wires `androidUnitTest` to commonTest.
        instrumentedTestVariant.sourceSetTree.set(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree.test)
    }

    // Declare all targets
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    jvm()

    js {
        browser { binaries.executable() }
    }

    // ── JS test bootstrapping ────────────────────────────────────────────────
    // Karma needs filament.js + filament.wasm loaded before any Kotlin test
    // code references globals like `Engine` / `Renderer`. Stage the WASM
    // loader, binary, and bootstrap script into every module's jsTest
    // resources. Each module also keeps a karma.config.d/filament-setup.js
    // committed alongside its build.gradle.kts (Karma reads that automatically
    // from <projectDir>/karma.config.d).
    val stagedWebAssets = layout.buildDirectory.dir("filamentWebAssets")
    val stageFilamentWebAssets = tasks.register<Sync>("stageFilamentWebAssetsForJsTest") {
        dependsOn(rootProject.tasks.named("downloadPrebuilts_web"))
        from(rootProject.layout.projectDirectory.dir("prebuilts/web")) {
            include("filament.js", "filament.wasm")
        }
        from(rootProject.layout.projectDirectory.file(
            "gradle/karma/filament-karma-bootstrap.js"
        ))
        into(stagedWebAssets)
    }
    sourceSets.named("jsTest") {
        resources.srcDir(stageFilamentWebAssets)
    }

    applyDefaultHierarchyTemplate()

    // iOS minimum deployment target for native binaries
    targets.withType<KotlinNativeTarget>().configureEach {
        if (konanTarget.family == org.jetbrains.kotlin.konan.target.Family.IOS) {
            binaries.all {
                freeCompilerArgs += "-Xoverride-konan-properties=apple.sdk.min.version=15.0"
            }
        }
    }
}

// ── JVM/Panama floor ──────────────────────────────────────────────────────────
// The jvmMain actuals call java.lang.foreign (finalized in JDK 22) and depend on
// :java:filament-ffm. The Gradle daemon runs on JDK 25 (gradle/gradle-daemon-jvm.properties), so
// the toolchain and the requested org.gradle.jvm.version are already 22+ — no per-module toolchain
// or attribute forcing is needed. We only pin the jvm bytecode to a JVM 22 floor so the artifact
// stays usable on any JDK 22+ (Android/native/JS targets are untouched — different task types).
tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_22)
}

// jvmTest runs FFM downcalls into libfilament-c; silence the JDK 22+ restricted-native-access
// warning. Downstream app launchers need the same flag.
tasks.withType<Test>().configureEach {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    // Full exception output for failed tests — the default summary truncates the message,
    // hiding causes like a native "undefined symbol".
    testLogging {
        events("failed")
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true
    }
}

// ── androidx.test runner deps for connectedDebugAndroidTest ───────────────────
// `connectedDebugAndroidTest` needs an instrumentation runner on the device;
// commonTest sources flow into androidInstrumentedTest via the default
// hierarchy template, so every module that applies this plugin gets the deps.
val libs = the<org.gradle.api.artifacts.VersionCatalogsExtension>().named("libs")
kotlin.sourceSets.named("androidInstrumentedTest").configure {
    dependencies {
        implementation(libs.findLibrary("androidx-test-runner").get())
        implementation(libs.findLibrary("androidx-test-ext-junit").get())
    }
}

// ── XCFramework (configured via afterEvaluate so the extension value is available) ──
afterEvaluate {
    val xcfName = filamentModuleExt.xcframeworkName.get().ifEmpty {
        project.name.replaceFirstChar { it.uppercaseChar() }
    }
    kotlin {
        val xcf = XCFramework(xcfName)
        listOf(
            targets.getByName("iosArm64")           as KotlinNativeTarget,
            targets.getByName("iosSimulatorArm64")   as KotlinNativeTarget,
            targets.getByName("iosX64")              as KotlinNativeTarget,
        ).forEach {
            it.binaries.framework {
                baseName = xcfName
                isStatic = true
                xcf.add(this)
            }
        }
    }
}

// ── Android defaults ──────────────────────────────────────────────────────────
android {
    val groupStr = project.group.toString()
    val modulePart = project.name.replace("-", ".")
    namespace  = "$groupStr.$modulePart"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}
