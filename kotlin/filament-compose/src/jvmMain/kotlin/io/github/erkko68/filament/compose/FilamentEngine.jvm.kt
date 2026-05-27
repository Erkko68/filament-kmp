package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.compose.internal.SkikoInterop
import kotlinx.coroutines.delay

internal enum class HostOs { MACOS, LINUX, WINDOWS }

private fun detectHostOs(): HostOs {
    val name = System.getProperty("os.name").lowercase()
    return when {
        name.contains("mac") || name.contains("darwin") -> HostOs.MACOS
        name.contains("win") -> HostOs.WINDOWS
        else -> HostOs.LINUX
    }
}

/**
 * Bookkeeping about how the JVM Engine was built. The `sharedContextPtr` is the
 * Skiko-owned native handle (MTLDevice / GLXContext / HGLRC) that Filament was
 * told to share. A zero pointer means we couldn't find the Skiko redrawer in
 * time and created a stand-alone Engine — callers must fall back to a non-
 * zero-copy path.
 */
internal class SharedEngineHandle(val engine: Engine, val sharedContextPtr: Long, val os: HostOs)

private val skikoHandleHolders = mutableMapOf<Engine, SharedEngineHandle>()

internal fun sharedHandleFor(engine: Engine): SharedEngineHandle? = skikoHandleHolders[engine]

@Composable
internal actual fun rememberDefaultViewEngine(backend: Engine.Backend): Engine? {
    var engine by remember { mutableStateOf<Engine?>(null) }

    LaunchedEffect(backend) {
        Filament.init()
        val os = detectHostOs()
        println("[FilamentEngine] starting deferred init os=$os backend=$backend")

        // Skiko initializes its redrawer lazily as the SkiaLayer becomes
        // displayable. Poll a few times before giving up and falling back
        // to a stand-alone Engine (which will render via CPU readback).
        var ctx = 0L
        var attempts = 0
        repeat(40) {
            attempts++
            ctx = when (os) {
                HostOs.MACOS   -> SkikoInterop.findMetalDevicePtr()
                HostOs.LINUX,
                HostOs.WINDOWS -> SkikoInterop.findGLContextHandle()
            }
            if (ctx != 0L) return@repeat
            delay(50)
        }
        println("[FilamentEngine] polling done attempts=$attempts ctx=0x${ctx.toString(16)}")

        val resolvedBackend = if (backend == Engine.Backend.DEFAULT) {
            if (os == HostOs.MACOS) Engine.Backend.METAL else Engine.Backend.OPENGL
        } else backend

        engine = if (ctx != 0L) {
            println("[FilamentEngine] building Engine with sharedContext=0x${ctx.toString(16)} backend=$resolvedBackend")
            Engine.Builder().backend(resolvedBackend).sharedContext(ctx).build()
                .also { skikoHandleHolders[it] = SharedEngineHandle(it, ctx, os) }
                .also { println("[FilamentEngine] built shared engine=$it") }
        } else {
            println("[FilamentEngine] no shared context found, building stand-alone Engine backend=$resolvedBackend")
            Engine.create(resolvedBackend).also { println("[FilamentEngine] built standalone engine=$it") }
        }
    }

    DisposableEffect(engine) {
        val toDispose = engine
        onDispose {
            toDispose?.let {
                skikoHandleHolders.remove(it)
                it.destroy()
            }
        }
    }

    return engine
}
