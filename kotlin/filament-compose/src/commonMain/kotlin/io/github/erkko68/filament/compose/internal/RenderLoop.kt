package io.github.erkko68.filament.compose.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.isActive

/** Calls [onFrame] once per display refresh while [enabled]; no frame is scheduled otherwise. */
@Composable
internal fun FilamentRenderLoop(enabled: Boolean = true, onFrame: (Long) -> Unit) {
    val currentOnFrame by rememberUpdatedState(onFrame)
    LaunchedEffect(enabled) {
        if (!enabled) return@LaunchedEffect
        while (isActive) {
            withFrameNanos { frameTimeNanos ->
                currentOnFrame(frameTimeNanos)
            }
        }
    }
}

/**
 * Keeps a paused surface rendering until its scene is on screen. Stopping the loop the moment
 * `renderingEnabled` goes false strands the last change (its frame not rendered or not shown yet), and a view
 * that starts paused, or whose surface is rebuilt while paused, shows nothing at all.
 *
 * [framesToSettle] is how many frames must reach the screen while paused: one more than the surface can have
 * in flight, so the last one was rendered after the pause.
 */
internal class PausedFrameGate(private val framesToSettle: Int) {
    private var shown by mutableIntStateOf(0)

    /** Whether the render loop runs: always while rendering, and while paused until [delivered] has settled. */
    fun loopEnabled(renderingEnabled: Boolean): Boolean = renderingEnabled || shown < framesToSettle

    /** A frame reached the screen; [paused] is whether rendering was paused then. */
    fun delivered(paused: Boolean) {
        if (paused && shown < framesToSettle) shown++
    }

    /** Nothing shown yet: resumed, or the surface was resized or rebuilt. */
    fun reset() {
        shown = 0
    }
}

/** A [PausedFrameGate] that starts over when rendering resumes or [targets] change. */
@Composable
internal fun rememberPausedFrameGate(renderingEnabled: Boolean, framesToSettle: Int = 1, vararg targets: Any?): PausedFrameGate {
    val gate = remember(*targets) { PausedFrameGate(framesToSettle) }
    SideEffect { if (renderingEnabled) gate.reset() }
    return gate
}
