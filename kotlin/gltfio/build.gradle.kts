import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("filament-kmp-module")
}

filamentModule {
    xcframeworkName.set("Gltfio")
}

val filaVersion: String by project
val libVersion: String by project

// Additional prebuilts needed by gltfio-c beyond what :kotlin:filament already embeds.
// (filament, backend, utils, filaflat, filabridge, zstd are covered by the filament module.)
val GLTFIO_PREBUILT_LIBS = listOf(
    "libgltfio_core.a",
    "libdracodec.a",
    "libbasis_transcoder.a",  // transitive dep of gltfio_core
    "libmikktspace.a",        // transitive dep of gltfio_core
    "libstb.a",
    "libimage.a",
    "libimageio-lite.a",
    "libktxreader.a",
)

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":kotlin:filament"))
        }
        androidMain.dependencies {
            implementation("com.google.android.filament:gltfio-android:$filaVersion")
        }
        jvmMain.dependencies {
            implementation("io.github.erkko68.filament.java:gltfio:$libVersion")
        }
        jsMain.dependencies {
            implementation(project(":js"))
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops {
            create("gltfio") {
                defFile(project.file("src/nativeInterop/cinterop/gltfio.def"))
                includeDirs(
                    project.file("../../c/gltfio/c"),
                    project.file("../../c/filament/c"),
                    project.file("../../include"),
                )
            }
        }
        applyFilamentNative(project, "gltfio", "gltfio-c", GLTFIO_PREBUILT_LIBS)
    }
}
