package buildlogic.prebuilts

import org.gradle.api.logging.Logger
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Downloads archives once into a cache dir (<repo>/.gradle/filament-prebuilts-cache) that survives
 * `clean` and is shared by every task wanting the same file.
 */
object ArchiveCache {
    fun filamentRelease(cacheDir: File, version: String, asset: String, logger: Logger): File =
        fetch(cacheDir, "filament-v$version-$asset.tgz",
            "https://github.com/google/filament/releases/download/v$version/filament-v$version-$asset.tgz", logger)

    fun filamentSource(cacheDir: File, version: String, logger: Logger): File =
        fetch(cacheDir, "filament-src-v$version.tar.gz",
            "https://github.com/google/filament/archive/refs/tags/v$version.tar.gz", logger)

    fun emsdk(cacheDir: File, version: String, logger: Logger): File =
        fetch(cacheDir, "emsdk-$version.tar.gz",
            "https://github.com/emscripten-core/emsdk/archive/refs/tags/$version.tar.gz", logger)

    private fun fetch(cacheDir: File, name: String, url: String, logger: Logger): File {
        val cached = cacheDir.resolve(name)
        if (cached.exists()) {
            logger.lifecycle("  cached: $name")
            return cached
        }
        cacheDir.mkdirs()
        logger.lifecycle("  download: $url")
        // Unique temp + atomic move: parallel tasks wanting the same file never see a partial one.
        val tmp = File(cacheDir, "$name.${ProcessHandle.current().pid()}.${Thread.currentThread().id}.part")
        try {
            val conn = URI(url).toURL().openConnection() as HttpURLConnection
            conn.connectTimeout = 30_000
            conn.readTimeout = 120_000
            conn.instanceFollowRedirects = true
            conn.inputStream.use { input -> tmp.outputStream().use { input.copyTo(it, 1 shl 16) } }
            Files.move(tmp.toPath(), cached.toPath(), StandardCopyOption.REPLACE_EXISTING)
        } finally {
            tmp.delete()
        }
        return cached
    }
}
