import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("filament-kmp-module")
}

filamentModule {
    xcframeworkName.set("Filamat")
}

val filaVersion: String by project
val libVersion: String by project

// Additional prebuilts needed by filamat-c beyond what :kotlin:filament already embeds.
// (filament, backend, utils, filabridge, smol-v are covered by the filament module.)
val FILAMAT_PREBUILT_LIBS = listOf(
    "libfilamat.a",
    "libshaders.a",
    "libfilabridge.a",  // safe to re-list; linker deduplicates
    "libfilaflat.a",
)

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":kotlin:filament"))
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            implementation("com.google.android.filament:filamat-android:$filaVersion")
        }
        jvmMain.dependencies {
            // Project Panama (FFM): the combined libfilament-c image + jextract-generated
            // FilamentC already cover the filamat surface. Replaces the JNI :java:filamat dep.
            api(project(":java"))
        }
        jsMain.dependencies {
            implementation(project(":js"))
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops {
            create("filamat") {
                defFile(project.file("src/nativeInterop/cinterop/filamat.def"))
                includeDirs(
                    project.file("../../c/filamat/c"),
                    project.file("../../c/filament/c"),
                    project.file("../../include"),
                )
            }
        }
        applyFilamentNative(project, "filamat", "filamat-c", FILAMAT_PREBUILT_LIBS)
    }
}
