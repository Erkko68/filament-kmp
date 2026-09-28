import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    kotlin("jvm")
    id("filament-publish")
}

group = project.findProperty("projectGroup") as? String ?: "io.github.erkko68.filament"
version = project.findProperty("libVersion") as? String ?: "0.1.0-SNAPSHOT"

// Portable JNI bindings over the Fila* C API: generated `external fun`s + the FilaJni runtime (Kotlin),
// and the matching C forwarders (src/main/cpp) that each JNI runtime module compiles into its libfilament-c.
// No native build here: :android builds it per ABI; a JVM JNI runtime can reuse it per host.

// JNI needs nothing newer than Java 8, so this stays at the Android floor, not the FFM module's JDK 22.
val jvmRelease = libs.versions.android.jvmTarget.get()
tasks.withType<JavaCompile>().configureEach {
    options.release.set(jvmRelease.toInt())
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(jvmRelease))
}

sourceSets.main { kotlin.srcDir("src/main/generated") }

// Desktop loader unit tests (extraction cache + stale-dir cleanup).
dependencies {
    testImplementation(libs.junit)
}
tasks.named<Test>("test") { useJUnit() }

// Committed output (like :web's externals): regenerate after a C header change.
tasks.register<GenerateJniBindings>("generateJniBindings") {
    val cModules = mapOf("FilamentC" to "filament", "FilamatC" to "filamat", "FilamentUtilsC" to "filament-utils", "GltfioC" to "gltfio")
    modules.set(cModules)
    headers.from(cModules.values.map { rootProject.fileTree("c/$it/c") { include("*.h") } })
    packageName.set("io.github.erkko68.filament.jni")
    cSourceDir.set(rootProject.layout.projectDirectory.dir("c"))
    cDir.set(layout.projectDirectory.dir("src/main/cpp/generated"))
    kotlinDir.set(layout.projectDirectory.dir("src/main/generated"))
}
