package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.isActive

/**
 * Timing passed to an [OnFrame] callback, computed once per display refresh.
 *
 * @property frameTimeNanos The frame time from `withFrameNanos` (monotonic, platform clock).
 * @property deltaSeconds Seconds since the previous frame, clamped to a sane maximum so a stalled
 *   or backgrounded frame doesn't produce a huge time jump. Zero on the first frame.
 * @property elapsedSeconds Seconds since this [OnFrame] entered the composition.
 */
data class FrameInfo(
    val frameTimeNanos: Long,
    val deltaSeconds: Float,
    val elapsedSeconds: Float,
)

/** Maximum [FrameInfo.deltaSeconds] — clamps the jump after a stall/background pause. */
private const val MAX_DELTA_SECONDS = 0.1f

/**
 * The single per-frame primitive for `filament-compose`: runs [onFrame] once per display refresh
 * (via `withFrameNanos`) without triggering recomposition. Every other per-frame helper —
 * [rememberSceneClock], [FilamentEffect]'s `onFrame`, `rememberFlightCameraState`,
 * [io.github.erkko68.filament.compose.scene.rememberAnimationState] — is built on this.
 *
 * Use it directly for per-frame side effects (driving a transform, a material parameter, a custom
 * entity) that don't need to be read back in composition. For an elapsed-time *value* you can read
 * in a composable, use [rememberSceneClock] instead. For reacting to **state** changes rather than
 * the frame clock, use `GltfInstance`'s `onUpdate` (which runs per recomposition, not per frame).
 *
 * The [onFrame] lambda may change between recompositions without restarting the loop.
 *
 * ```kotlin
 * OnFrame { frame ->
 *     angle += frame.deltaSeconds * speed   // advance independent of recomposition
 * }
 * ```
 */
@Composable
fun OnFrame(onFrame: (FrameInfo) -> Unit) {
    val current by rememberUpdatedState(onFrame)
    LaunchedEffect(Unit) {
        var startNanos = -1L
        var lastNanos = -1L
        while (isActive) {
            withFrameNanos { now ->
                if (startNanos < 0L) startNanos = now
                val delta = if (lastNanos >= 0L) {
                    ((now - lastNanos) / 1e9f).coerceIn(0f, MAX_DELTA_SECONDS)
                } else 0f
                lastNanos = now
                current(FrameInfo(now, delta, (now - startNanos) / 1e9f))
            }
        }
    }
}
