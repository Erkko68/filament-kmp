package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.testutils.TestGlb
import io.github.erkko68.filament.compose.testutils.assertSceneEmpty
import io.github.erkko68.filament.compose.testutils.withUiThreadFilamentScene
import io.github.erkko68.filament.testsupport.IgnoreJs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Coverage for [rememberGltfAsset] — the asynchronous loading path that apps actually use, as
 * opposed to the pre-loaded assets [GltfInstanceLifecycleTest] hands to `GltfInstance` directly.
 *
 * Runs on [withUiThreadFilamentScene] rather than `TierBSceneFixture`: `rememberGltfAsset` uploads
 * GPU resources from a `LaunchedEffect`, and gltfio's `ResourceLoader` is thread-affine, so the
 * engine has to be created on the same dispatcher the effects resume on. See that harness for the
 * full rationale.
 *
 * The frame clock is manual here, and the loader advances one step per `withFrameNanos` — so tests
 * pump frames via [pumpUntil] rather than assuming the asset is ready after `waitForIdle()`.
 *
 * ### Why the loading tests are `@IgnoreJs`
 * On web `ResourceLoader.asyncUpdateLoad` kicks off `FilamentAsset.loadResources(onDone = …)` and
 * only reports progress 1.0 from that **browser callback** (ResourceLoader.web.kt). Advancing
 * Compose's manual frame clock does not run the browser's task queue, so the callback never fires
 * within the pumped frames and the asset stays un-ready — a harness limitation, not a web gap:
 * `GltfInstanceLifecycleTest` shows glTF loading and instancing working on web via the synchronous
 * path. The error-path tests below need no completed load, so they run everywhere.
 */
class GltfAssetLoadingTest {

    /**
     * Pumps frames until [predicate] holds, or gives up after [maxFrames].
     *
     * Tests wait on the *observable end state* (entities in the scene) rather than just
     * `isReady`: the flag is set inside the loader's effect, and the recomposition that
     * actually mounts `GltfInstance` — which early-returns while the asset is not ready —
     * only runs on the following frame. Waiting on `isReady` alone races that mount.
     */
    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.pumpUntil(maxFrames: Int = 240, predicate: () -> Boolean): Boolean {
        repeat(maxFrames) {
            if (predicate()) return true
            mainClock.advanceTimeByFrame()
            waitForIdle()
        }
        return predicate()
    }

    /** The whole arc: null while loading, then a ready asset whose instance populates the scene. */
    @IgnoreJs
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun assetLoadsAsynchronouslyAndFeedsAnInstance() = withUiThreadFilamentScene { setContent, _, scene ->
        var asset: GltfAsset? = null
        setContent {
            val a = rememberGltfAsset { TestGlb.getAnimatedMorphCubeGlbBytes() }
            asset = a
            if (a != null) GltfInstance(asset = a)
        }

        assertTrue(
            pumpUntil { asset?.isReady == true && scene.entityCount > 0 },
            "rememberGltfAsset should load and its instance reach the scene within the frame budget",
        )
        assertNotNull(asset, "the asset should be non-null once loaded")
        assertTrue(scene.renderableCount > 0, "the morph cube should contribute a renderable")

        setContent {}
        waitForIdle()
        assertSceneEmpty(scene, "asset + instance leaked after disposal")
    }

    /**
     * Bad bytes must not throw inside composition — the contract is null forever plus one [onError].
     * A composable that threw here would take down the whole UI on a corrupt download.
     */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun invalidBytesReportOnceAndNeverThrow() = withUiThreadFilamentScene { setContent, _, scene ->
        var asset: GltfAsset? = null
        var errors = 0
        setContent {
            asset = rememberGltfAsset(onError = { errors++ }) { ByteArray(64) { 0x7F } }
        }
        repeat(8) { mainClock.advanceTimeByFrame(); waitForIdle() }

        assertNull(asset, "unparseable bytes should leave the asset null")
        assertEquals(1, errors, "onError should fire exactly once for a parse failure")
        assertEquals(0, scene.entityCount, "nothing should reach the scene when parsing fails")

        setContent {}
        waitForIdle()
    }

    /** A throwing `load` lambda is reported the same way as unparseable bytes, not propagated. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun loadFailureIsReportedNotThrown() = withUiThreadFilamentScene { setContent, _, _ ->
        var asset: GltfAsset? = null
        var caught: Throwable? = null
        setContent {
            asset = rememberGltfAsset(onError = { caught = it }) { error("network is down") }
        }
        repeat(8) { mainClock.advanceTimeByFrame(); waitForIdle() }

        assertNull(asset, "a failed load should leave the asset null")
        assertNotNull(caught, "onError should receive the exception thrown by the load lambda")
        assertTrue(
            caught.message?.contains("network is down") == true,
            "the original failure should be surfaced, got: ${caught.message}",
        )

        setContent {}
        waitForIdle()
    }

    /** Two instances share one asset: both populate the scene, and disposal cleans up all of it. */
    @IgnoreJs
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun oneAssetBacksMultipleInstances() = withUiThreadFilamentScene { setContent, _, scene ->
        var asset: GltfAsset? = null
        setContent {
            val a = rememberGltfAsset { TestGlb.getAnimatedMorphCubeGlbBytes() }
            asset = a
            if (a != null) {
                GltfInstance(asset = a, position = Position(-2f, 0f, 0f))
                GltfInstance(asset = a, position = Position(2f, 0f, 0f))
            }
        }

        assertTrue(
            pumpUntil { asset?.isReady == true && scene.entityCount >= 2 },
            "two instances sharing one asset should add at least two entities, got ${scene.entityCount}",
        )

        setContent {}
        waitForIdle()
        assertSceneEmpty(scene, "shared-asset instances leaked after disposal")
    }

    /**
     * Swapping `key` re-runs `load` and the fresh bytes replace the old asset rather than
     * accumulating alongside it.
     *
     * `load` returns a **copy** deliberately. The inner `rememberGltfAsset` keys `createAsset` on the
     * byte array's *reference*, so returning the shared `EmbeddedGlb` array — as every other test
     * here does — re-runs `load` but reuses the existing asset. That is fine (identical bytes,
     * identical asset), and it means the reload machinery only engages for a genuinely new array,
     * which is what a real file/network read produces each time.
     */
    @IgnoreJs
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun changingKeyReloadsTheAsset() = withUiThreadFilamentScene { setContent, _, scene ->
        var key by mutableStateOf(1)
        var asset: GltfAsset? = null
        var loads = 0
        setContent {
            val a = rememberGltfAsset(key = key) {
                loads++
                TestGlb.getAnimatedMorphCubeGlbBytes().copyOf()
            }
            asset = a
            if (a != null) GltfInstance(asset = a)
        }

        assertTrue(
            pumpUntil { asset?.isReady == true && scene.entityCount > 0 },
            "first load should complete",
        )
        val first = asset
        val entitiesAfterFirst = scene.entityCount

        assertEquals(1, loads, "load should have run exactly once for the first key")

        key = 2
        assertTrue(
            pumpUntil { asset !== first && asset?.isReady == true && scene.entityCount > 0 },
            "changing key should produce a freshly loaded asset that reaches the scene",
        )
        assertEquals(2, loads, "changing key should re-run the load lambda")
        assertEquals(
            entitiesAfterFirst, scene.entityCount,
            "the reloaded asset should replace the old one, not accumulate alongside it",
        )

        setContent {}
        waitForIdle()
        assertSceneEmpty(scene, "reloaded asset leaked after disposal")
    }
}
