import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import java.awt.GraphicsEnvironment

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
        publishLibraryVariants("release")
        // Make `androidInstrumentedTest` inherit from `commonTest` so the
        // shared `expect`s (e.g. createTestSurface, TestMaterials) line up
        // with the per-Android `actual`s. The default hierarchy template
        // only wires `androidUnitTest` to commonTest.
        instrumentedTestVariant.sourceSetTree.set(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree.test)
    }

    // Declare all targets
    iosArm64()
    iosSimulatorArm64()
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
// :java. The Gradle daemon runs on JDK 25 (gradle/gradle-daemon-jvm.properties), so
// the toolchain and the requested org.gradle.jvm.version are already 22+ — no per-module toolchain
// or attribute forcing is needed. We only pin the jvm bytecode to a JVM 22 floor so the artifact
// stays usable on any JDK 22+ (Android/native/JS targets are untouched — different task types).
tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_22)
}

// ── Real-backend (GPU) test gating, decided once here on the host ─────────────
// Whether a real Filament backend can be created is a property of the host, which
// only Gradle sees reliably — the forked JVM and the iOS simulator don't inherit
// the host env. So decide it here and inject FILAMENT_TEST_GPU; TestEnv just reads
// it, no per-platform GPU guessing at runtime. Override anything with the
// -PfilamentTestGpu property or the FILAMENT_TEST_GPU env var.
// The default differs by target because the same runner behaves differently:
val forcedGpu: String? = providers.gradleProperty("filamentTestGpu").orNull
    ?: System.getenv("FILAMENT_TEST_GPU")
val osName = System.getProperty("os.name").orEmpty().lowercase()

// JVM host backend — CI-independent: macOS Actions runners are Apple-silicon with a
// real GPU, so Metal runs on CI too. Windows DEFAULT=Vulkan aborts uncatchably with
// no usable driver; headless Linux has no display (CI Linux opts in via lavapipe).
val jvmGpu: String = forcedGpu ?: when {
    osName.contains("mac") -> "true"
    osName.contains("win") -> "false"
    else -> (!GraphicsEnvironment.isHeadless()).toString()
}

// iOS simulator only reaches the GPU from a locally-booted sim; on a CI runner the
// sim's Metal driver aborts on real draw. Conservative default off under CI — flip
// with -PfilamentTestGpu=true to test whether a given runner's sim can render.
val simGpu: String = forcedGpu ?: (System.getenv("CI") == null).toString()

// jvmTest runs FFM downcalls into libfilament-c; silence the JDK 22+ restricted-native-access
// warning. Downstream app launchers need the same flag.
tasks.withType<Test>().configureEach {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    environment("FILAMENT_TEST_GPU", jvmGpu)
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
            targets.getByName("iosArm64")          as KotlinNativeTarget,
            targets.getByName("iosSimulatorArm64") as KotlinNativeTarget,
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

// ── iOS simulator test device ─────────────────────────────────────────────────
// Kotlin/Native runs simulator tests in `--standalone` mode by default, which has
// no graphics context — Filament's Metal driver then can't obtain a device and
// aborts. Run inside a booted simulator instead (like Android needs an emulator).
// The device must be booted beforehand; scripts/dev/run-tests.sh handles that.
// Override the device with -PiosSimulatorDevice="<name>".
tasks.withType<org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest>().configureEach {
    standalone.set(false)
    device.set(providers.gradleProperty("iosSimulatorDevice").orElse("iPhone 17"))
    // The simulated test process does NOT inherit the host environment, so forward
    // the already-decided flag (SIMCTL_CHILD_ is the prefix simctl spawn unwraps).
    environment("SIMCTL_CHILD_FILAMENT_TEST_GPU", simGpu)
}
