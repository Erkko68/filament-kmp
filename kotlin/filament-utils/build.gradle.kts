import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("filament-kmp-module")
}

filamentModule {
    xcframeworkName.set("FilamentUtils")
}

val filaVersion: String by project
val libVersion: String by project

// Additional prebuilts needed by filament-utils-c beyond what :kotlin:filament already embeds.
// (zstd, utils, filaflat, filabridge are covered by the filament module.)
val FILAMENT_UTILS_PREBUILT_LIBS = listOf(
    "libfilament-iblprefilter.a",
    "libcamutils.a",
    "libimage.a",
    "libimageio-lite.a",
    "libktxreader.a",
)

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":kotlin:filament"))
        }
        androidMain.dependencies {
            implementation("com.google.android.filament:filament-utils-android:$filaVersion")
        }
        jvmMain.dependencies {
            api("io.github.erkko68.filament.java:filament-utils:$libVersion")
        }
        jsMain.dependencies {
            implementation(project(":js"))
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops {
            create("filament_utils") {
                defFile(project.file("src/nativeInterop/cinterop/filament-utils.def"))
                includeDirs(
                    project.file("../../c/filament-utils/c"),
                    project.file("../../c/filament/c"),
                    project.file("../../include"),
                )
            }
        }
        applyFilamentNative(project, "filament_utils", "filament-utils-c", FILAMENT_UTILS_PREBUILT_LIBS)
    }
}
