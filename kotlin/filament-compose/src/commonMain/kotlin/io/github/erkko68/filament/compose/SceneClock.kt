package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember

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
 * This is the **observable-value** layer over [OnFrame]: use it when you want elapsed time as a
 * state read in composition. For a per-frame side effect that should *not* recompose, use
 * [OnFrame] directly; for property animations with easing prefer `animateFloatAsState` /
 * `Animatable`.
 */
@Composable
fun rememberSceneClock(): State<Float> {
    val state = remember { mutableFloatStateOf(0f) }
    OnFrame { state.floatValue = it.elapsedSeconds }
    return state
}
