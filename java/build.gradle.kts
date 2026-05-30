import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    kotlin("jvm")
    id("filament-publish")
}

// JVM/Panama (FFM) bindings module. Drives the combined CMake build at c/CMakeLists.txt
// (-DFILAMENT_BUILD_SHARED=ON → one libfilament-c shared lib with the Filament static
// archives linked exactly once), runs jextract over the C headers to generate the
// low-level binding classes, packages the dylib into this module's resources, and ships
// a loader that System.loads it so jextract's loaderLookup resolves the symbols. It also
// hosts the shared FFM helpers (src/main/kotlin Ffm.kt) the kotlin:* jvm actuals build on.
//
// kotlin:filament's jvmMain depends on this module and writes the idiomatic Kotlin
// actuals on top of the generated FilamentC class.

// Directory is java/ but the published artifact keeps its id `filament-ffm`
// (filament-publish derives the id from project.name = "java", so pin it back here).
mavenPublishing {
    coordinates(group.toString(), "filament-ffm", version.toString())
}

// FFM was finalized in JDK 22. The Gradle daemon runs on JDK 25 (gradle/gradle-daemon-jvm.properties),
// so we just pin a JVM 22 release floor here to keep the published artifact usable on any JDK 22+.
tasks.withType<JavaCompile>().configureEach {
    options.release.set(22)
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_22)
}

// ── Native build + jextract generation (see buildSrc/FilamentJvmNative.kt) ────
// Extract the full C surface — filament + filamat + filament-utils + gltfio — into one
// FilamentC class. The combined libfilament-c shared library already links all four wrappers
// (one Filament image → one EntityManager), so a single jextract run over every header keeps
// the generated FilaTypes structs consistent and lets all four kotlin JVM modules share it.
val ffm = applyFilamentJvmNative(
    headerDirs = listOf(
        rootProject.file("c/filament/c"),
        rootProject.file("c/filamat/c"),
        rootProject.file("c/filament-utils/c"),
        rootProject.file("c/gltfio/c"),
    ),
    includeDirs = listOf(
        rootProject.file("c/filament/c"),
        rootProject.file("c/filamat/c"),
        rootProject.file("c/filament-utils/c"),
        rootProject.file("c/gltfio/c"),
        rootProject.file("include"),
    ),
    ffmPackage = "io.github.erkko68.filament.ffm",
    headerClassName = "FilamentC",
)

// Generated jextract sources compile alongside the hand-written loader. Adding the
// jextract task (not just its output dir) as the source dir makes Gradle infer the
// dependency for every consumer — compileJava, compileKotlin, sourcesJar — so none
// of them can run before the bindings are generated.
sourceSets.named("main") {
    java.srcDir(ffm.jextract)
}

// ── Package the combined dylib into resources (mirrors java/filament) ─────────
tasks.named<ProcessResources>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(ffm.cmakeBuild)
    val platformArch = ffm.platformArch
    // Single-config generators (Make / Ninja) put libs directly in build/cmake/.
    from(ffm.dylibDir) {
        include("*.dylib", "*.so", "*.dll")
        into("natives/$platformArch")
    }
    // Multi-config generators (Visual Studio) put DLLs in build/cmake/Release/.
    from(ffm.dylibDir.map { it.dir("Release") }) {
        include("*.dll")
        into("natives/$platformArch")
    }
    // Bundle pre-compiled natives from other platforms (CI publish job). Pass
    // -PcArtifactsDir=<path> where the dir has {platform}-{arch}/ subdirs each
    // containing the combined libfilament-c for that platform.
    val cArtifactsDir = (project.findProperty("cArtifactsDir") as? String)?.let { file(it) }
    if (cArtifactsDir != null && cArtifactsDir.exists()) {
        cArtifactsDir.listFiles { f -> f.isDirectory }?.forEach { platformDir ->
            from(platformDir) {
                include("*.dylib", "*.so", "*.dll")
                into("natives/${platformDir.name}")
            }
        }
    }
}

tasks.named("assemble") {
    dependsOn(ffm.cmakeBuild)
}
