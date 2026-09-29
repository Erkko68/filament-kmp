package io.github.erkko68.filament.compose.internal.target

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.ExperimentalGpuToGpuFrameSharing
import io.github.erkko68.filament.compose.FilamentComposeDesktop
import java.awt.Window
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap

private const val ISSUES_URL = "https://github.com/Erkko68/filament-kmp/issues/new"

/** The desktop OS, which decides the API Compose draws with and so the GPU-to-GPU path. */
internal enum class DesktopOs {
    /** skiko draws with Metal. */
    MACOS,
    /** skiko draws with Direct3D 12. */
    WINDOWS,
    /** skiko draws with OpenGL (GLX). */
    LINUX,
    /** No GPU-to-GPU path: CPU readback only. */
    OTHER;

    companion object {
        val current: DesktopOs = System.getProperty("os.name").orEmpty().lowercase().let {
            when {
                "mac" in it -> MACOS
                "win" in it -> WINDOWS
                "linux" in it -> LINUX
                else -> OTHER
            }
        }
    }
}

/**
 * A setup the GPU path doesn't cover (e.g. Compose fell back to software rendering): not a bug, so
 * it falls back with a one-line warning rather than a report.
 */
internal class GpuFrameSharingUnavailable(message: String) : Exception(message)

internal fun unavailable(reason: String): Nothing = throw GpuFrameSharingUnavailable(reason)

/**
 * Whether to try GPU-to-GPU frame sharing, and the guard around it: anything it throws is reported
 * once and the session continues with CPU readback.
 */
internal object GpuFrameSharing {
    private val optedInEngines = Collections.synchronizedSet(Collections.newSetFromMap(WeakHashMap<Engine, Boolean>()))
    private val warnings = ConcurrentHashMap.newKeySet<String>()

    @Volatile private var failed = false

    /** Opted in, and nothing has failed yet this session. */
    @OptIn(ExperimentalGpuToGpuFrameSharing::class)
    val enabled: Boolean get() = FilamentComposeDesktop.gpuToGpuFrameSharingEnabled && !failed

    /** Records that [engine] was created for GPU-to-GPU sharing; engines created otherwise keep CPU readback. */
    fun optIn(engine: Engine) {
        optedInEngines += engine
    }

    fun enabledFor(engine: Engine): Boolean = enabled && engine in optedInEngines

    /**
     * Runs [block]. If the GPU path isn't available it warns once; if it fails it prints a report
     * and turns GPU sharing off for the session. Either way [fallback] provides the result.
     */
    inline fun <T> guard(stage: String, window: Window?, engine: Engine?, block: () -> T, fallback: () -> T): T =
        try {
            block()
        } catch (e: GpuFrameSharingUnavailable) {
            warnUnavailable(e.message.orEmpty())
            fallback()
        } catch (e: Exception) {
            fail(stage, e, window, engine)
            fallback()
        } catch (e: LinkageError) {
            // A missing native entry point or a skiko class that changed shape.
            fail(stage, e, window, engine)
            fallback()
        }

    fun warnUnavailable(reason: String) {
        if (warnings.add(reason)) {
            System.err.println("filament-kmp: GPU-to-GPU frame sharing unavailable, using CPU readback: $reason")
        }
    }

    fun fail(stage: String, error: Throwable, window: Window?, engine: Engine?) {
        failed = true
        System.err.print(report(stage, error, window, engine))
    }

    private fun report(stage: String, error: Throwable, window: Window?, engine: Engine?): String = buildString {
        val rule = "=".repeat(80)
        fun row(label: String, value: () -> Any?) {
            val text = runCatching { value()?.toString() }.getOrElse { "? (${it.javaClass.simpleName})" }
            appendLine("  ${label.padEnd(18)}${text ?: "none"}")
        }
        appendLine(rule)
        appendLine("filament-kmp: experimental GPU-to-GPU frame sharing failed while $stage.")
        appendLine("Switched to CPU readback for the rest of this session: rendering continues, with a")
        appendLine("GPU->CPU copy per frame. Please open an issue with this whole report:")
        appendLine("  $ISSUES_URL")
        appendLine("-".repeat(80))
        row("filament-kmp") { FilamentComposeDesktop::class.java.`package`?.implementationVersion ?: "unknown" }
        row("OS") { "${System.getProperty("os.name")} ${System.getProperty("os.version")} (${System.getProperty("os.arch")})" }
        row("Java") { "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})" }
        row("skiko") { org.jetbrains.skiko.Version.skiko }
        row("Compose renderer") { window?.findSkiaLayer()?.renderApi }
        row("skiko redrawer") { skikoRedrawer(window)?.javaClass?.simpleName }
        row("GPU") { skikoRedrawer(window)?.let { it.javaClass.getMethod("getRenderInfo").invoke(it) }?.toString()?.trim()?.replace("\n", "; ") }
        row("Filament backend") { engine?.takeIf { it.isValid }?.backend }
        appendLine("-".repeat(80))
        append(error.stackTraceToString())
        appendLine(rule)
    }
}
