package buildlogic.prebuilts

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.GZIPInputStream

/** Reads .tar.gz archives: static libraries out of release tarballs, whole trees out of source ones. */
object TarExtraction {
    private val LIB_EXTENSIONS = listOf(".a", ".lib")

    inline fun forEachFile(tarball: File, action: (TarArchiveEntry, TarArchiveInputStream) -> Unit) {
        TarArchiveInputStream(GZIPInputStream(tarball.inputStream().buffered())).use { tar ->
            var entry = tar.nextEntry
            while (entry != null) {
                if (entry.isFile) action(entry, tar)
                entry = tar.nextEntry
            }
        }
    }

    /** Extracts the static libraries under [prefix], flat into [outDir]. */
    fun extractLibs(tarball: File, prefix: String, outDir: File): Int {
        outDir.mkdirs()
        val dirPrefix = prefix.trimEnd('/') + "/"
        var n = 0
        forEachFile(tarball) { entry, tar ->
            val base = entry.name.substringAfterLast('/')
            if (entry.name.startsWith(dirPrefix) && LIB_EXTENSIONS.any { base.endsWith(it) }) {
                outDir.resolve(base).outputStream().use { tar.copyTo(it) }
                n++
            }
        }
        return n
    }

    /** Extracts one xcframework slice's libraries: filament/lib/lib<name>.xcframework/<slice>/<file>. */
    fun extractXcframeworkSlice(tarball: File, slice: String, outDir: File): Int {
        outDir.mkdirs()
        var n = 0
        forEachFile(tarball) { entry, tar ->
            val parts = entry.name.split("/")
            if (parts.size == 5 && parts[1] == "lib" && parts[2].endsWith(".xcframework") && parts[3] == slice &&
                LIB_EXTENSIONS.any { parts[4].endsWith(it) }
            ) {
                outDir.resolve(parts[4]).outputStream().use { tar.copyTo(it) }
                n++
            }
        }
        return n
    }

    /** Extracts the whole archive into [destDir] minus its top directory, keeping exec bits and symlinks. */
    fun extractTree(tarball: File, destDir: File) {
        val dest = destDir.toPath().toAbsolutePath().normalize()
        TarArchiveInputStream(GZIPInputStream(tarball.inputStream().buffered())).use { tar ->
            var entry = tar.nextEntry
            while (entry != null) {
                val relative = entry.name.substringAfter('/', "")
                if (relative.isNotEmpty()) {
                    val out = dest.resolve(relative).normalize()
                    check(out.startsWith(dest)) { "Tar entry escapes destination: ${entry.name}" }
                    when {
                        entry.isDirectory -> Files.createDirectories(out)
                        entry.isSymbolicLink -> {
                            Files.createDirectories(out.parent)
                            Files.deleteIfExists(out)
                            Files.createSymbolicLink(out, Paths.get(entry.linkName))
                        }
                        entry.isFile -> {
                            Files.createDirectories(out.parent)
                            out.toFile().outputStream().use { tar.copyTo(it) }
                            if (entry.mode and 0b001_000_000 != 0) out.toFile().setExecutable(true, false)
                        }
                    }
                }
                entry = tar.nextEntry
            }
        }
    }
}
