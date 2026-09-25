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

// Web: filamat-kmp.wasm (built by :wasm) + its Fila* externals, generated like :wasm's.
val generateFilamatExternals = tasks.register<GenerateWasmExternals>("generateFilamatExternals") {
    dependsOn(":wasm:setupEmsdk")
    modules.set(mapOf("FilamatC" to "filamat"))
    headers.from(rootProject.fileTree("c/filamat/c") { include("*.h") })
    packageName.set("io.github.erkko68.filament.filamat.wasm")
    baseInterface.set("FilamentModule")
    instance.set("io.github.erkko68.filament.filamat.filamatWasm")
    cDir.set(rootProject.layout.projectDirectory.dir("c"))
    emsdkDir.set(rootProject.layout.projectDirectory.dir(".emsdk"))
    mainDir.set(layout.buildDirectory.dir("generated/filamatExternals/webMain"))
    testDir.set(layout.buildDirectory.dir("generated/filamatExternals/webTest"))
}
val stageFilamatWasm = tasks.register<Sync>("stageFilamatWasm") {
    dependsOn(":wasm:stageFilamatWasm")
    from(rootProject.layout.projectDirectory.dir("wasm/build/filamatWasm"))
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
        androidMain.dependencies {
            implementation("com.google.android.filament:filamat-android:$filaVersion")
        }
        jvmMain.dependencies {
            // Project Panama (FFM): the combined libfilament-c image + jextract-generated
            // FilamentC already cover the filamat surface. Replaces the JNI :java:filamat dep.
            api(project(":java"))
        }
        webMain {
            kotlin.srcDir(generateFilamatExternals.flatMap { it.mainDir })
            resources.srcDir(stageFilamatWasm)
            dependencies {
                implementation(project(":wasm"))
            }
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
