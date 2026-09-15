package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.testutils.TestGlb
import io.github.erkko68.filament.compose.testutils.TierBSceneFixture
import io.github.erkko68.filament.compose.testutils.assertSceneEmpty
import io.github.erkko68.filament.compose.testutils.skippedComposeTest
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import io.github.erkko68.filament.testsupport.IgnoreJs
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tier-B (real-backend) coverage for [GltfInstance] — the most-used composable in the module, and
 * previously the only major one with no tests. gltfio uploads real GPU buffers and compiles
 * ubershaders, so this gates on a DEFAULT backend via [TierBSceneFixture] and skips where none is
 * available.
 *
 * The asset is `AnimatedMorphCube.glb`: one mesh with morph targets, the smallest thing that drives
 * the morph-weight path. It is loaded through the fixture's `gltfAsset()` rather than
 * `rememberGltfAsset` — see that helper for why (thread affinity, not a library limitation).
 *
 * ### What these can and cannot assert
 * Filament exposes `setMorphWeights` but **no getter**, so nothing here can read back the weights
 * that reached the GPU. What is observable is scene membership, entity/component liveness, and that
 * each recomposition shape completes without tripping a native abort — so these pin the lifecycle
 * and the recomposition patterns around the weight-diffing path, not the uploaded values. Checking
 * the values themselves needs a Tier-C frame capture or an upstream getter.
 */
class GltfInstanceLifecycleTest : TierBSceneFixture() {

    private fun morphCube() = gltfAsset(TestGlb.getAnimatedMorphCubeGlbBytes())

    /** Mount → entities enter the scene as renderables; dispose → nothing is left behind. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun instanceEntersAndLeavesCleanly() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val asset = morphCube() ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var created = false
            setContent { GltfInstance(asset = asset, onCreate = { created = true }) }
            waitForIdle()

            assertTrue(created, "onCreate should fire once the instance enters the scene")
            assertTrue(scene.entityCount > 0, "the instance should add entities while composed")
            assertTrue(scene.renderableCount > 0, "the morph cube should contribute a renderable")

            setContent {}
            waitForIdle()
            assertSceneEmpty(scene, "GltfInstance leaked after disposal")
        }
    }

    /** Guards the fixture: without morph targets on the asset, the weight tests would be vacuous. */
    // @IgnoreJs: RenderableManager.getMorphTargetCount is a hardcoded `return 0` on web
    // (RenderableManager.web.kt), so this can never report targets there. glTF loading and
    // instancing themselves work fine on web — the other tests in this class run.
    @IgnoreJs
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun assetActuallyHasMorphTargets() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val asset = morphCube() ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var morphTargets = 0
            setContent {
                GltfInstance(
                    asset = asset,
                    onCreate = {
                        val rm = engine.renderableManager
                        morphTargets = instance.entities
                            .filter { rm.hasComponent(it) }
                            .maxOfOrNull { rm.getMorphTargetCount(rm.getInstance(it)) } ?: 0
                    },
                )
            }
            waitForIdle()
            assertTrue(morphTargets > 0, "AnimatedMorphCube should expose morph targets, got $morphTargets")

            setContent {}
            waitForIdle()
        }
    }

    /**
     * Drives every shape the weight-diffing path handles, on one mounted instance: initial push, a
     * **new array with equal contents** (the case the diffing exists to skip), changed contents,
     * in-place mutation of the array already held, and dropping back to null.
     *
     * In-place mutation pins an implementation detail worth keeping: the previous weights are stored
     * as a `copyOf()`, so mutating the caller's array is still detected on the next recomposition.
     * Storing the reference instead would compare the array against itself and silently swallow the
     * update.
     */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun morphWeightRecompositionShapesAreHandled() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val asset = morphCube() ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var weights by mutableStateOf<FloatArray?>(floatArrayOf(0f, 0f))
            setContent { GltfInstance(asset = asset, morphWeights = weights) }
            waitForIdle()
            val mounted = scene.entityCount
            assertTrue(mounted > 0, "instance should be in the scene before weights are exercised")

            fun recompose() { mainClock.advanceTimeByFrame(); waitForIdle() }

            // A distinct array holding equal values — the push the diffing is meant to skip.
            weights = floatArrayOf(0f, 0f)
            recompose()

            // Genuinely changed values.
            weights = floatArrayOf(1f, 0.5f)
            recompose()

            // Same array instance, mutated in place after being handed over.
            val live = floatArrayOf(0.25f, 0.75f)
            weights = live
            recompose()
            live[0] = 0.9f
            recompose()
            assertContentEquals(
                floatArrayOf(0.9f, 0.75f), live,
                "the composable must not write back into the caller's weight array",
            )

            // Dropping to null leaves the last weights applied rather than erroring.
            weights = null
            recompose()

            assertEquals(mounted, scene.entityCount, "weight churn should not change scene membership")

            setContent {}
            waitForIdle()
            assertSceneEmpty(scene, "GltfInstance leaked after weight-update disposal")
        }
    }

    /** `visible = false` pulls the instance from the scene without destroying it, and back again. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun visibleTogglesSceneMembership() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val asset = morphCube() ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var visible by mutableStateOf(true)
            setContent { GltfInstance(asset = asset, visible = visible) }
            waitForIdle()
            val visibleCount = scene.entityCount
            assertTrue(visibleCount > 0, "instance should be in the scene while visible")

            visible = false
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(0, scene.entityCount, "hiding should remove the instance's entities")

            visible = true
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(visibleCount, scene.entityCount, "re-showing should restore the entities")

            setContent {}
            waitForIdle()
            assertSceneEmpty(scene, "GltfInstance leaked after visibility toggling")
        }
    }
}
