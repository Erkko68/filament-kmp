package buildlogic.jvm

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.DataInputStream
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile

/**
 * Fails if any class on [classpath] needs a newer JVM than [release], so a dependency bump
 * can't raise consumers' runtime floor unnoticed. Multi-release entries and module-info are skipped.
 */
abstract class BytecodeLevelTask : DefaultTask() {
    @get:Classpath abstract val classpath: ConfigurableFileCollection
    @get:Input abstract val release: Property<Int>
    @get:OutputFile abstract val report: RegularFileProperty

    @TaskAction
    fun check() {
        val maxMajor = release.get() + 44
        val offenders = classpath.files.filter { it.exists() }.mapNotNull { file ->
            val major = highestMajor(file)
            if (major > maxMajor) "${file.name}: Java ${major - 44}" else null
        }
        check(offenders.isEmpty()) {
            "Classes above the Java ${release.get()} floor on the runtime classpath:\n  " +
                offenders.joinToString("\n  ")
        }
        report.get().asFile.writeText("Java ${release.get()}: ${classpath.files.size} entries ok\n")
    }

    private fun highestMajor(file: File): Int = when {
        file.isDirectory -> file.walkTopDown()
            .filter { it.isFile && isChecked(it.relativeTo(file).invariantSeparatorsPath) }
            .maxOfOrNull { f -> f.inputStream().use(::major) } ?: 0
        file.extension == "jar" -> ZipFile(file).use { zip ->
            zip.entries().asSequence().filter { !it.isDirectory && isChecked(it.name) }
                .maxOfOrNull { e -> zip.getInputStream(e).use(::major) } ?: 0
        }
        else -> 0 // .aar, .so etc.: not JVM classpath bytecode
    }

    private fun isChecked(path: String) =
        path.endsWith(".class") && !path.startsWith("META-INF/versions/") && !path.endsWith("module-info.class")

    // Class file header: u4 magic, u2 minor, u2 major.
    private fun major(input: InputStream): Int = DataInputStream(input).run { skipNBytes(6); readUnsignedShort() }
}
