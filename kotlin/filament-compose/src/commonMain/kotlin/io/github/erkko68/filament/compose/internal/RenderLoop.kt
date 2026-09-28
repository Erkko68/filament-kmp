package io.github.erkko68.filament.compose.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
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
