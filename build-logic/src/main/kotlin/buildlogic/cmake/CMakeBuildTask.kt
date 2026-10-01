package buildlogic.cmake

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Configures and builds a CMake project, delivering [targets] into [outputDir] (or its [outputSubdir]),
 * passed as `FILA_OUTPUT_DIR`. The build tree lives apart in [buildDir] so Gradle never wipes CMake's cache, and
 * [sources] (everything that can change the output) make the task up-to-date instead of re-running cmake.
 */
abstract class CMakeBuildTask @Inject constructor(private val exec: ExecOperations) : DefaultTask() {
    @get:Input abstract val cmake: Property<String>
    @get:Input abstract val buildType: Property<String>
    @get:Input abstract val arguments: ListProperty<String>
    @get:Input abstract val targets: ListProperty<String>
    @get:Input abstract val environment: MapProperty<String, String>

    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sources: ConfigurableFileCollection

    @get:Internal abstract val sourceDir: DirectoryProperty
    @get:Internal abstract val buildDir: DirectoryProperty
    @get:OutputDirectory abstract val outputDir: DirectoryProperty
    /** Where under [outputDir] the libraries go, e.g. an ABI dir for Android's jniLibs layout. */
    @get:Input abstract val outputSubdir: Property<String>

    init {
        outputSubdir.convention("")
    }

    @TaskAction
    fun build() {
        val build = buildDir.get().asFile.path
        val output = outputDir.get().asFile.resolve(outputSubdir.get())
        run(listOf("-S", sourceDir.get().asFile.path, "-B", build,
            "-DCMAKE_BUILD_TYPE=${buildType.get()}",
            "-DFILA_OUTPUT_DIR=${output.invariantSeparatorsPath}") + arguments.get())
        // A bare --parallel is an unbounded `make -j`: ~100 Filament-including compiles at once swap a 7 GB macOS runner.
        val jobs = Runtime.getRuntime().availableProcessors().toString()
        run(listOf("--build", build, "--config", buildType.get(), "--parallel", jobs, "--target") + targets.get())
    }

    private fun run(args: List<String>) {
        exec.exec {
            commandLine(listOf(cmake.get()) + args)
            environment(this@CMakeBuildTask.environment.get())
        }
    }
}
