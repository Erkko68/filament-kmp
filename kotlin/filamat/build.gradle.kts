import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("filament-kmp-module")
}

filamentModule {
    xcframeworkName.set("Filamat")
}

val filaVersion = project.property("filaVersion") as String
val libVersion = project.property("libVersion") as String

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
            implementation(project(":kotlin:test-support"))
        }
        androidMain.dependencies {
            implementation("com.google.android.filament:filamat-android:$filaVersion")
        }
        jvmMain.dependencies {
            // Project Panama (FFM): the combined libfilament-c image + jextract-generated
            // FilamentC already cover the filamat surface. Replaces the JNI :java:filamat dep.
            api(project(":java"))
        }
        webMain.dependencies {
            implementation(project(":web"))
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
