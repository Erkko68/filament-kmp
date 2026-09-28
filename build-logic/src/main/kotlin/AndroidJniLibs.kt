import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Builds c/CMakeLists.txt's Android JNI image (`filament-c-android` → libfilament-c.so) once per ABI into
 * [outputDir]/<abi>, with the SDK's pinned NDK and CMake. Run as a plain task rather than AGP's
 * externalNativeBuild so configuring the build never needs an Android SDK.
 */
abstract class BuildAndroidJniLibs @Inject constructor(private val exec: ExecOperations) : DefaultTask() {
    @get:Input abstract val abis: ListProperty<String>
    @get:Input abstract val minSdk: Property<Int>
    @get:Input abstract val ndkVersion: Property<String>
    @get:Input abstract val cmakeVersion: Property<String>

    /** c/ sources, the JNI forwarders and the prebuilts: anything that changes the .so. */
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sources: ConfigurableFileCollection

    /** :jni:generateJniGlue's output, compiled in via FILA_JNI_GLUE_DIR. */
    @get:InputDirectory @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val jniGlueDir: DirectoryProperty

    @get:Internal abstract val sdkDirectory: DirectoryProperty
    @get:Internal abstract val cmakeSourceDir: DirectoryProperty
    @get:Internal abstract val cmakeBuildDir: DirectoryProperty
    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @TaskAction
    fun build() {
        val sdk = sdkDirectory.get().asFile
        val ndk = sdk.resolve("ndk/${ndkVersion.get()}")
        val bin = sdk.resolve("cmake/${cmakeVersion.get()}/bin")
        check(ndk.isDirectory && bin.isDirectory) {
            "Missing NDK/CMake in $sdk: sdkmanager \"ndk;${ndkVersion.get()}\" \"cmake;${cmakeVersion.get()}\""
        }
        val exe = if (hostPlatform() == "windows") ".exe" else ""
        val cmake = bin.resolve("cmake$exe").path
        for (abi in abis.get()) {
            val buildDir = cmakeBuildDir.get().dir(abi).asFile
            exec.exec {
                commandLine(
                    cmake, "-S", cmakeSourceDir.get().asFile.path, "-B", buildDir.path, "-G", "Ninja",
                    "-DCMAKE_MAKE_PROGRAM=${bin.resolve("ninja$exe")}",
                    "-DCMAKE_TOOLCHAIN_FILE=${ndk.resolve("build/cmake/android.toolchain.cmake")}",
                    "-DANDROID_ABI=$abi",
                    "-DANDROID_PLATFORM=android-${minSdk.get()}",
                    "-DANDROID_STL=c++_static",
                    "-DFILAMENT_PLATFORM=android",
                    "-DFILA_JNI_GLUE_DIR=${jniGlueDir.get().asFile}",
                    // Always Release: a Debug libfilament-c against Release prebuilts buys nothing.
                    "-DCMAKE_BUILD_TYPE=Release",
                    "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=${outputDir.get().dir(abi).asFile}",
                )
            }
            exec.exec { commandLine(cmake, "--build", buildDir.path, "--target", "filament-c-android") }
        }
    }
}
