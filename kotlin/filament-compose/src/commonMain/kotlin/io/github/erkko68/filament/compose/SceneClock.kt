package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.isActive

/**
 * A monotonic per-frame clock as a `State<Float>` — seconds elapsed since the composable
 * entered the composition. Reading the value triggers recomposition every frame, so use it
 * with a `by` delegate inside scene composables to drive animations cheaply:
 *
 * ```kotlin
 * val time by rememberSceneClock()
 * Group(position = Position(cos(time) * 3f, 0f, sin(time) * 3f)) {
 *     Sphere(material = earth)
 * }
 * ```
 *
 * Use this for ambient animation loops (orbits, breathing, pulsing). For property
 * animations with easing prefer `animateFloatAsState` / `Animatable`; for one-off
 * per-frame work without a state read, use `FilamentEffect { onFrame { ... } }`.
 */
@Composable
fun rememberSceneClock(): State<Float> {
    val state = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(state) {
        val start = withFrameNanos { it }
        while (isActive) {
            withFrameNanos { nanos -> state.floatValue = (nanos - start) / 1e9f }
        }
    }
    return state
}
