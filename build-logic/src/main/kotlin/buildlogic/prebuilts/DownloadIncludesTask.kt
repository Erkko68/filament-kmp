package buildlogic.prebuilts

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.zip.GZIPInputStream

/**
 * Syncs Filament's public headers into <repo>/include/ from the platform-neutral
 * GitHub *source* tarball (per-platform release tarballs carry generated blobs and
 * platform-only helpers we don't include). The header tree must match the prebuilt
 * .a files exactly — types have had ABI breaks across patch versions.
 *
 * Also synthesizes include/gltfio/materials/uberarchive.h: upstream generates it
 * per platform with a hardcoded UBERARCHIVE_DEFAULT_SIZE, so we read each
 * platform's size out of its release tarball and emit one header that picks the
 * right value via preprocessor branches.
 */
abstract class DownloadIncludesTask : DefaultTask() {
    @get:Input abstract val filamentVersion: Property<String>
    @get:Internal abstract val includeDir: DirectoryProperty
    @get:Internal abstract val cacheDir: DirectoryProperty
    @get:OutputFile abstract val stampFile: RegularFileProperty

    init {
        // Skip when include/ is already stamped at this version (survives output-file
        // timestamp churn; declared here, not in the plugin script, so the spec stays
        // configuration-cache serializable).
        outputs.upToDateWhen {
            val stamp = stampFile.get().asFile
            stamp.exists() && stamp.readText().trim() == filamentVersion.get()
        }
    }

    /** Source-tarball sub-trees whose *contents* are mirrored under include/. */
    private val includeSources = listOf(
        "libs/utils/include/", "libs/filamat/include/", "libs/camutils/include/",
        "libs/filabridge/include/", "libs/filaflat/include/", "libs/gltfio/include/",
        "libs/ibl/include/", "libs/image/include/", "libs/imageio-lite/include/",
        "libs/ktxreader/include/", "libs/mathio/include/", "libs/uberz/include/",
        "libs/viewer/include/", "libs/geometry/include/", "libs/math/include/",
        "libs/iblprefilter/include/", "libs/filameshio/include/",
        "libs/generatePrefilterMipmap/include/",
        "filament/include/", "filament/backend/include/",
        // BlueVK and the Vulkan headers, for backend/platforms/VulkanPlatform.h (jni's D3DHelper).
        "libs/bluevk/include/",
        // Third-party headers that filament's public API transitively includes.
        "third_party/robin-map/include/", "third_party/mikktspace/include/",
        "third_party/getopt/include/",
    )

    /** tarball-suffix → uberarchive.h path inside that release tarball. */
    private val uberarchivePaths = mapOf(
        "mac" to "filament/include/gltfio/materials/uberarchive.h",
        "ios" to "filament/include/gltfio/materials/uberarchive.h",
        "linux" to "filament/include/gltfio/materials/uberarchive.h",
        "windows" to "include/gltfio/materials/uberarchive.h",
        "android-native" to "filament/include/gltfio/materials/uberarchive.h",
    )

    @TaskAction
    fun run() {
        val version = filamentVersion.get()
        val outDir = includeDir.get().asFile
        val cache = cacheDir.get().asFile
        logger.lifecycle("[includes v$version]")

        val tarball = ArchiveCache.filamentSource(cache, version, logger)
        var count = 0
        var rootPrefix: String? = null
        TarArchiveInputStream(GZIPInputStream(tarball.inputStream().buffered())).use { tar ->
            var entry = tar.nextEntry
            while (entry != null) {
                // GitHub source tarballs wrap everything in filament-<version>/.
                if (rootPrefix == null) rootPrefix = entry.name.substringBefore('/') + "/"
                if (entry.isFile) {
                    val src = includeSources.firstOrNull { entry.name.startsWith(rootPrefix + it) }
                    if (src != null) {
                        val rel = entry.name.removePrefix(rootPrefix + src)
                        if (rel.isNotEmpty()) {
                            val dest = outDir.resolve(rel)
                            dest.parentFile.mkdirs()
                            dest.outputStream().use { tar.copyTo(it) }
                            count++
                        }
                    }
                }
                entry = tar.nextEntry
            }
        }
        check(count > 0) { "No headers extracted from ${tarball.name}" }

        synthesizeUberarchive(version, outDir, cache)
        stampFile.get().asFile.writeText(version + "\n")
        logger.lifecycle("  extracted $count headers -> $outDir")
    }

    private fun synthesizeUberarchive(version: String, outDir: File, cache: File) {
        logger.lifecycle("  uberarchive.h (per-platform sizes):")
        val sizeRe = Regex("""#define\s+UBERARCHIVE_DEFAULT_SIZE\s+(\d+)""")
        val sizes = uberarchivePaths.mapValues { (suffix, path) ->
            val tarball = ArchiveCache.filamentRelease(cache, version, suffix, logger)
            var size: Int? = null
            TarArchiveInputStream(GZIPInputStream(tarball.inputStream().buffered())).use { tar ->
                var entry = tar.nextEntry
                while (entry != null) {
                    if (entry.isFile && entry.name == path) {
                        size = sizeRe.find(tar.readBytes().toString(Charsets.UTF_8))?.groupValues?.get(1)?.toInt()
                        break
                    }
                    entry = tar.nextEntry
                }
            }
            val s = size ?: error("UBERARCHIVE_DEFAULT_SIZE not found at $path in ${tarball.name}")
            logger.lifecycle("    %-8s %d".format(suffix, s))
            s
        }
        val dst = outDir.resolve("gltfio/materials/uberarchive.h")
        dst.parentFile.mkdirs()
        dst.writeText(
            """
            // Generated by the downloadIncludes task (build-logic) — do not edit.
            // Sizes are extracted from each platform's per-release tarball.
            #ifndef UBERARCHIVE_H_
            #define UBERARCHIVE_H_

            #include <stdint.h>

            #if defined(__APPLE__)
            #include <TargetConditionals.h>
            #endif

            extern "C" {
                extern const uint8_t UBERARCHIVE_PACKAGE[];
            }

            #define UBERARCHIVE_DEFAULT_OFFSET 0
            #if defined(__APPLE__) && TARGET_OS_IPHONE
            #define UBERARCHIVE_DEFAULT_SIZE ${sizes["ios"]}
            #elif defined(__APPLE__)
            #define UBERARCHIVE_DEFAULT_SIZE ${sizes["mac"]}
            #elif defined(__ANDROID__)
            #define UBERARCHIVE_DEFAULT_SIZE ${sizes["android-native"]}
            #elif defined(_WIN32) || defined(_WIN64)
            #define UBERARCHIVE_DEFAULT_SIZE ${sizes["windows"]}
            #else
            #define UBERARCHIVE_DEFAULT_SIZE ${sizes["linux"]}
            #endif
            #define UBERARCHIVE_DEFAULT_DATA (UBERARCHIVE_PACKAGE + UBERARCHIVE_DEFAULT_OFFSET)

            #endif
            """.trimIndent() + "\n"
        )
    }
}
