import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

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

    androidTarget() {
        publishLibraryVariants("release", "debug")
    }

    // Declare all targets
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    jvm()

    js {
        browser { binaries.executable() }
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
    defaultConfig { minSdk = 24 }
}
