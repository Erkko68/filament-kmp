import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("filament-kmp-module")
}

filamentModule {
    xcframeworkName.set("Filament")
}

val filaVersion: String by project
val libVersion: String by project

// Libraries that filament-c links against from the prebuilts directory.
// These are the direct dependencies of the C wrapper plus known transitive
// dependencies of libfilament.a that the linker must resolve.
val FILAMENT_PREBUILT_LIBS = listOf(
    "libfilament.a",
    "libbackend.a",
    "libutils.a",
    "libgeometry.a",
    "libibl-lite.a",       // CMakeLists: ibl-lite (NOT libibl.a)
    "libfilaflat.a",
    "libfilabridge.a",
    "libsmol-v.a",
    "libzstd.a",
    "libmeshoptimizer.a",  // transitive dep of libfilament.a
)

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            implementation("com.google.android.filament:filament-android:$filaVersion")
        }
        jvmMain.dependencies {
            api(project(":java:filament"))
        }
        jsMain.dependencies {
            implementation(project(":js"))
        }
    }

    // Stage filament.js + filament.wasm from prebuilts into a build dir, then expose
    // that dir as an extra jsTest resources source so jsTestProcessResources picks
    // them up alongside src/jsTest/resources (Karma serves them — see
    // src/jsTest/resources/karma.config.d/filament-setup.js).
    val stagedWebAssets = layout.buildDirectory.dir("filamentWebAssets")
    val stageFilamentWebAssetsForJsTest = tasks.register<Sync>("stageFilamentWebAssetsForJsTest") {
        dependsOn(rootProject.tasks.named("downloadPrebuilts_web"))
        from(rootProject.layout.projectDirectory.dir("prebuilts/web")) {
            include("filament.js", "filament.wasm")
        }
        into(stagedWebAssets)
    }
    sourceSets.named("jsTest") {
        resources.srcDir(stageFilamentWebAssetsForJsTest)
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops {
            create("filament") {
                defFile(project.file("src/nativeInterop/cinterop/filament.def"))
                includeDirs(
                    project.file("../../c/filament/c"),
                    project.file("../../c/filamat/c"),
                    project.file("../../c/filament-utils/c"),
                    project.file("../../include"),
                )
            }
        }
        applyFilamentNative(project, "filament", "filament-c", FILAMENT_PREBUILT_LIBS)
    }
}
