import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.gradle.api.attributes.java.TargetJvmVersion

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
            // Project Panama (FFM) bindings: combined libfilament-c + jextract-generated
            // FilamentC class + loader. Replaces the JNI :java:filament dependency.
            api(project(":java:filament-ffm"))
        }
        jsMain.dependencies {
            implementation(project(":js"))
        }
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

// ── JVM/Panama toolchain ──────────────────────────────────────────────────────
// The jvmMain actuals reference java.lang.foreign (MemorySegment, Arena, …), finalized
// in JDK 22. Point only the JVM Kotlin compilations at JDK 25 (LTS) — Android and the
// Kotlin/Native targets keep the module-default toolchain. Bytecode targets JVM 22 so the
// artifact stays usable on any JDK 22+.
val javaToolchains = extensions.getByType<JavaToolchainService>()
val jdk25Launcher = javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.withType<KotlinJvmCompile>().configureEach {
    kotlinJavaToolchain.toolchain.use(jdk25Launcher)
    compilerOptions.jvmTarget.set(JvmTarget.JVM_22)
}

// The :java:filament-ffm dependency targets JVM 22 (FFM floor). Advertise/​request JVM 22 on the
// jvm target's variants so dependency resolution accepts it (the default would be JVM 21, from the
// Gradle-running JDK). Scoped to jvm* configs so Android/Native variants are untouched.
configurations.matching { it.name.startsWith("jvm") }.configureEach {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 22)
}

// jvmTest runs FFM downcalls into libfilament-c; silence the JDK 22+ restricted-native-access
// warning. Downstream app launchers need the same flag.
tasks.withType<Test>().configureEach {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
