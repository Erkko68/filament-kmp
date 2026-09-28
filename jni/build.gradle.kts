import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    id("filament-publish")
}

// The JNI runtime shared by desktop and Android: FilaJni (native memory, callbacks) over src/main/cpp, which
// each native runtime (:desktop, :android) compiles into its libfilament-c with the generated forwarders
// (see jni/CMakeLists.txt). JNI needs nothing newer than the Android bytecode floor.
val jvmRelease = libs.versions.android.jvmTarget.get()
kotlin { compilerOptions { jvmTarget.set(JvmTarget.fromTarget(jvmRelease)) } }
java { targetCompatibility = JavaVersion.toVersion(jvmRelease) }
