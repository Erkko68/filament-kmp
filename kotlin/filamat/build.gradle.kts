import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("filament-kmp-module")
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

// Web: filamat-kmp.wasm, built by :web; its exports are installed as globals like filament-kmp.wasm's.
val stageFilamatWasm = tasks.register<Sync>("stageFilamatWasm") {
    dependsOn(":web:stageFilamatWasm")
    from(rootProject.layout.projectDirectory.dir("web/build/filamatWasm"))
    into(layout.buildDirectory.dir("filamatWasm"))
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":kotlin:filament"))
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":kotlin:test-support"))
        }
        webMain.dependencies {
            implementation(project(":web"))
        }
        webTest {
            resources.srcDir(stageFilamatWasm)
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
