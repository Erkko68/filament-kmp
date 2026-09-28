import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("filament-publish")
}

group = project.findProperty("projectGroup") as? String ?: "io.github.erkko68.filament"
version = project.findProperty("libVersion") as? String ?: "0.1.0-SNAPSHOT"

// Android native runtime for :jni: libfilament-c.so per ABI (the Fila* C API + jni/'s forwarders + the
// Android-only src/androidMain/cpp), over the upstream android-native prebuilts. NDK and CMake are pinned to
// upstream's (build/common/versions) so the prebuilts' libc++ matches.

val androidAbis = listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
val minSdkVersion = libs.versions.android.minSdk.get().toInt()

kotlin {
    android {
        namespace = "io.github.erkko68.filament.jni"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = minSdkVersion
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.android.jvmTarget.get()))
        }
        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        androidMain.dependencies {
            api(project(":jni"))
        }
        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.test.runner)
            implementation(libs.androidx.test.ext.junit)
            implementation(libs.kotlin.testJunit)
        }
    }
}

val buildJniLibs = tasks.register<BuildAndroidJniLibs>("buildJniLibs") {
    abis.set(androidAbis)
    minSdk.set(minSdkVersion)
    ndkVersion.set("29.0.14206865")
    cmakeVersion.set("3.22.1")
    sdkDirectory.set(androidComponents.sdkComponents.sdkDirectory)
    cmakeSourceDir.set(rootProject.layout.projectDirectory.dir("c"))
    cmakeBuildDir.set(layout.buildDirectory.dir("cmake"))
    outputDir.set(layout.buildDirectory.dir("jniLibs"))
    sources.from(
        rootProject.fileTree("c") { include("**/CMakeLists.txt", "**/*.c", "**/*.cpp", "**/*.h"); exclude("build/**") },
        project(":jni").fileTree("src/main/cpp"),
        fileTree("src/androidMain/cpp"),
        androidAbis.map { rootProject.layout.projectDirectory.dir("prebuilts/android-$it/lib") },
    )
    dependsOn(androidAbis.map { ":downloadPrebuilts_android-$it" }, ":downloadIncludes")
}

androidComponents.onVariants { it.sources.jniLibs?.addGeneratedSourceDirectory(buildJniLibs, BuildAndroidJniLibs::outputDir) }
