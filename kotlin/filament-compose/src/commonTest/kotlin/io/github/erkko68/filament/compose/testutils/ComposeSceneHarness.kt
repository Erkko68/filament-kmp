package io.github.erkko68.filament.compose.testutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.Scene
import io.github.erkko68.filament.testsupport.TestEnv
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

    // A single real `setContent` hosts a swappable content slot driven by state. Android's
    // `AndroidComposeUiTest.setContent` is one-shot — a second call throws "already set content" —
    // whereas tests mount, mutate, and unmount repeatedly. Routing every (re)mount through this
    // `mutableState` keeps us to one `setContent` call, so the same harness runs on jvm/js/ios/android.
    var slot by mutableStateOf<@Composable FilamentSceneScope.() -> Unit>({})
    setContent {
        CompositionLocalProvider(
            LocalFilamentEngine provides engine,
            LocalFilamentScene provides scene,
        ) {
            FilamentSceneScopeInstance.slot()
        }
    }
    val setSceneContent: SetSceneContent = { content ->
        slot = content
        // With autoAdvance off, a state-driven recomposition only runs when the frame clock ticks
        // (unlike the old harness, where the real one-shot setContent composed synchronously). Pump a
        // single frame so the new content is mounted and its DisposableEffects committed before the
        // caller's waitForIdle()/assertions. One frame is enough to mount + run effects; the caller
        // advances more frames explicitly when a test needs to drive OnFrame.
        mainClock.advanceTimeByFrame()
    }
    body(setSceneContent)

    // Clear the content and restore auto-advance before the block returns. On wasmJs,
    // runComposeUiTest's teardown awaits the frame clock; with auto-advance still off and
    // a light's unbounded OnFrame (`withFrameNanos`) loop from a not-yet-disposed tree, that
    // await never settles and the returned Promise never resolves — the test then trips
    // Mocha's 30s timeout (flaky under load: prior tests' leaked frame loops starve later
    // ones, so *which* tests hang varies run to run). Disposing here cancels every OnFrame
    // loop; re-enabling auto-advance lets teardown drain and resolve. (js/jvm tear down
    // without leaning on the clock, so this was wasm-only.)
    slot = {}
    waitForIdle()
    mainClock.autoAdvance = true
}

/**
 * Like [withFilamentScene], but creates and destroys the [Engine] and [Scene] **on Compose's own UI
 * dispatcher** instead of taking them from a fixture, and hands them to [body].
 *
 * Needed for anything that touches Filament from inside a coroutine effect. `LaunchedEffect` bodies
 * resume on the UI dispatcher (`AWT-EventQueue-0` on JVM desktop), while the [TierBSceneFixture]
 * `@BeforeTest` runs on the JUnit worker thread — so an engine created there is driven from two
 * threads the moment an effect calls into it. Thread-affine pieces of Filament (notably gltfio's
 * `ResourceLoader`, which `rememberGltfAsset` uses) answer that with a native `PreconditionPanic`
 * that no `try`/`catch` can intercept: the whole test process dies with SIGABRT. Real apps never see
 * it, because `rememberFilamentEngine` creates the engine on the same dispatcher its effects run on
 * — which is exactly the arrangement this harness reproduces.
 *
 * [body] is skipped entirely when no GPU backend is available, mirroring [TierBSceneFixture]'s gate,
 * so callers need no `engine ?: return` dance.
 */
@OptIn(ExperimentalTestApi::class)
fun withUiThreadFilamentScene(
    body: ComposeUiTest.(setContent: SetSceneContent, engine: Engine, scene: Scene) -> Unit,
) = runComposeUiTest {
    mainClock.autoAdvance = false

    var created: Pair<Engine, Scene>? = null
    runOnUiThread {
        Filament.init()
        // See RenderingTestFixture: never call Engine.create on a host with no GPU — Filament aborts
        // on its driver thread, which try/catch cannot recover.
        if (TestEnv.gpuBackendAvailable) {
            val e = try {
                Engine.create(Engine.Backend.DEFAULT).takeIf { it.isValid() }
            } catch (t: Throwable) {
                null
            }
            if (e != null) created = e to e.createScene()
        }
    }

    created?.let { (engine, scene) ->
        var slot by mutableStateOf<@Composable FilamentSceneScope.() -> Unit>({})
        setContent {
            CompositionLocalProvider(
                LocalFilamentEngine provides engine,
                LocalFilamentScene provides scene,
            ) {
                FilamentSceneScopeInstance.slot()
            }
        }
        val setSceneContent: SetSceneContent = { content ->
            slot = content
            mainClock.advanceTimeByFrame()
        }
        body(setSceneContent, engine, scene)

        // Same wasmJs teardown ordering as withFilamentScene — dispose before restoring the clock.
        slot = {}
        waitForIdle()
    }
    mainClock.autoAdvance = true

    // Tear down on the UI thread too: the engine was created there and is thread-affine.
    runOnUiThread {
        created?.let { (engine, scene) ->
            engine.destroyScene(scene)
            engine.flushAndWait()
            engine.destroy()
        }
    }
}

/**
 * Skip branch for gated (Tier-B) tests: a no-op harness run. On web `runComposeUiTest` is
 * asynchronous, so gated tests must *return* their harness result for kotlin.test to await it —
 * dropping it instead lets the composition run concurrently with later tests (wedging their frame
 * clock; the wasmJs 30s-timeout hangs) and silently swallows the dropped test's own assertion
 * failures. This gives the skip branch a value of the exact same platform type as
 * [composeScene]/[withFilamentScene] (both branches must share one type: JUnit needs the JVM method
 * to stay `void`, and a common-code LUB of unrelated expect types would compile to `Object`).
 */
@OptIn(ExperimentalTestApi::class)
fun skippedComposeTest() = runComposeUiTest { }

/**
 * Convenience host for the mount→assert→dispose shape. Mounts [content], lets the caller assert live
 * state via [whileComposed] (run after effects are applied — scene membership, components built —
 * but before disposal), optionally advances [frames] display refreshes to drive `OnFrame`/animation,
 * then **leaves** the composition so every `onDispose` fires and runs [afterDispose] for the leak
 * assertions.
 *
 * Both [whileComposed] and [afterDispose] run **inside** the test body. This matters on JS: there
 * `runComposeUiTest` is asynchronous (it returns a promise rather than blocking like JVM does), so
 * assertions placed *after* a `composeScene(...)` call would execute before the composition has run.
 * Keep all assertions in these two callbacks, and `return composeScene(...)` from the test so
 * kotlin.test awaits the promise.
 */
@OptIn(ExperimentalTestApi::class)
fun composeScene(
    engine: Engine,
    scene: Scene,
    frames: Int = 0,
    whileComposed: () -> Unit = {},
    afterDispose: () -> Unit = {},
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
    afterDispose()
}
