package io.github.erkko68.filament.compose.testutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Scene
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.FilamentSceneScopeInstance
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene

/**
 * Sets scene [content] as the active composition, wrapping it in the three composition locals that
 * scene composables read. Calling it again replaces the tree (driving recomposition / disposal of
 * the previous one). Available inside [withFilamentScene].
 */
typealias SetSceneContent = (content: @Composable FilamentSceneScope.() -> Unit) -> Unit

/**
 * Lower-level headless host: runs [body] inside a `runComposeUiTest`, handing it a [SetSceneContent]
 * that mounts scene composables with the engine/scene locals provided — no `FilamentView`/platform
 * surface needed. Use this for tests that recompose with changed inputs (mount, mutate state,
 * `waitForIdle`, assert) or that need direct `mainClock` control. For the common mount→assert→dispose
 * shape, prefer [composeScene].
 *
 * The harness neither creates nor destroys [engine]/[scene]; the caller (a NOOP fixture) owns them,
 * so post-dispose accounting can still read the scene.
 */
@OptIn(ExperimentalTestApi::class)
fun withFilamentScene(
    engine: Engine,
    scene: Scene,
    body: ComposeUiTest.(setContent: SetSceneContent) -> Unit,
) = runComposeUiTest {
    // Drive the frame clock manually. `OnFrame` runs an unbounded `withFrameNanos` loop (every light
    // registers one for `followGroupRotation`), so with the default auto-advancing clock the
    // composition is never idle and `waitForIdle()` hangs forever. Disabling auto-advance lets idle
    // work settle without time passing; tests call `advanceTimeByFrame()` to step `OnFrame` on demand.
    mainClock.autoAdvance = false
    val setSceneContent: SetSceneContent = { content ->
        setContent {
            CompositionLocalProvider(
                LocalFilamentEngine provides engine,
                LocalFilamentScene provides scene,
            ) {
                FilamentSceneScopeInstance.content()
            }
        }
    }
    body(setSceneContent)
}

/**
 * Convenience host for the mount→assert→dispose shape. Mounts [content], lets the caller assert live
 * state via [whileComposed] (run after effects are applied — scene membership, components built —
 * but before disposal), optionally advances [frames] display refreshes to drive `OnFrame`/animation,
 * then **leaves** the composition so every `onDispose` fires — the point of the leak assertions.
 */
@OptIn(ExperimentalTestApi::class)
fun composeScene(
    engine: Engine,
    scene: Scene,
    frames: Int = 0,
    whileComposed: () -> Unit = {},
    content: @Composable FilamentSceneScope.() -> Unit,
) = withFilamentScene(engine, scene) { setContent ->
    setContent(content)
    waitForIdle() // commits the composition's DisposableEffects (entity, scene membership, build)
    repeat(frames) { mainClock.advanceTimeByFrame() } // drive OnFrame logic (CameraNode, animation)
    whileComposed()
    // Replace the content with nothing: Compose tears down the previous tree, firing every
    // DisposableEffect.onDispose in reverse registration order — the lifecycle under test.
    setContent {}
    waitForIdle()
}
