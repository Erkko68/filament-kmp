package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.testutils.TestGlb
import io.github.erkko68.filament.compose.testutils.TierBSceneFixture
import io.github.erkko68.filament.compose.testutils.assertSceneEmpty
import io.github.erkko68.filament.compose.testutils.requestsFrames
import io.github.erkko68.filament.compose.testutils.skippedComposeTest
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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

    /** `castShadows`/`receiveShadows` override what the asset authored, and null gives it back. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shadowOverridesRevertToTheAuthoredFlags() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val asset = morphCube() ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var cast: Boolean? by mutableStateOf(null)
            var receive: Boolean? by mutableStateOf(null)
            setContent { GltfInstance(asset = asset, castShadows = cast, receiveShadows = receive) }
            waitForIdle()
            val rm = engine.renderableManager
            fun flags() = buildList { scene.forEach(::add) }.filter(rm::hasComponent)
                .map { rm.getInstance(it) }.map { rm.isShadowCaster(it) to rm.isShadowReceiver(it) }
            val authored = flags()
            assertTrue(authored.isNotEmpty())

            for (override in listOf(false, true)) {
                cast = override
                receive = override
                mainClock.advanceTimeByFrame()
                waitForIdle()
                assertTrue(flags().all { it == override to override }, "override = $override")
            }

            cast = null
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(authored.map { it.first to true }, flags(), "castShadows alone goes back")

            receive = null
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(authored, flags(), "null should restore the authored flags")
        }
    }

    /** Only a hoisted [AnimationState] needs a frame loop: a still model must not keep the window redrawing. */
    @Test
    fun frameLoopRunsOnlyWithAnAnimationState() {
        val engine = engine ?: return
        val scene = scene ?: return
        val asset = morphCube() ?: return

        assertFalse(requestsFrames(engine, scene) { GltfInstance(asset = asset) }, "a still instance asked for frames")
        assertTrue(
            requestsFrames(engine, scene) { GltfInstance(asset = asset, animationState = rememberAnimationState()) },
            "an animated instance should run a frame loop",
        )
    }

    /** Two instances of one asset are separate entity trees: each has its own transform, and one can leave alone. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun instancesOfOneAssetAreIndependent() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val asset = morphCube() ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var both by mutableStateOf(true)
            var left = 0
            var right = 0
            setContent {
                GltfInstance(asset = asset, position = Position(-1f, 0f, 0f), onCreate = { left = instance.root })
                if (both) GltfInstance(asset = asset, position = Position(1f, 0f, 0f), onCreate = { right = instance.root })
            }
            waitForIdle()
            val tm = engine.transformManager
            fun x(root: Int) = tm.getTransform(tm.getInstance(root))[12]
            assertTrue(left != 0 && right != 0 && left != right, "each instance should have its own root")
            assertEquals(listOf(-1f, 1f), listOf(x(left), x(right)))
            val two = scene.entityCount

            both = false
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(two / 2, scene.entityCount, "one instance leaving should take only its own entities")
            assertEquals(-1f, x(left), "the remaining instance keeps its transform")

            setContent {}
            waitForIdle()
            assertSceneEmpty(scene, "GltfInstance leaked after one of two was removed first")
        }
    }

    /** The transform follows its inputs in place, and the root hangs off the enclosing Group. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun transformAndParentFollowTheInputs() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val asset = morphCube() ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var position by mutableStateOf(Position(1f, 2f, 3f))
            var created = 0
            var root = 0
            var group = 0
            setContent {
                Group(onCreate = { group = entity }) {
                    GltfInstance(asset = asset, position = position, onCreate = { created++; root = instance.root })
                }
            }
            waitForIdle()
            val tm = engine.transformManager
            fun translation() = tm.getTransform(tm.getInstance(root)).slice(12..14)
            assertEquals(group, tm.getParent(tm.getInstance(root)), "the root should be parented to the Group")
            assertEquals(listOf(1f, 2f, 3f), translation())

            position = Position(4f, 5f, 6f)
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(listOf(4f, 5f, 6f), translation())
            assertEquals(1, created, "moving the instance should not rebuild it")

            setContent {}
            waitForIdle()
            assertSceneEmpty(scene)
        }
    }

    /** `animationIndex`/`animationTime` pose the model without a frame loop; an index the asset lacks is ignored. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun manualAnimationPosesTheModel() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val asset = gltfAsset(TestGlb.getFoxGlbBytes()) ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var index: Int? by mutableStateOf(0)
            var time by mutableStateOf(0f)
            var entities = IntArray(0)
            setContent {
                GltfInstance(asset = asset, animationIndex = index, animationTime = time, onCreate = { entities = instance.entities })
            }
            waitForIdle()
            val tm = engine.transformManager
            fun pose() = entities.filter(tm::hasComponent).flatMap { tm.getTransform(tm.getInstance(it)).toList() }
            fun recompose() { mainClock.advanceTimeByFrame(); waitForIdle() }
            val atStart = pose()
            assertTrue(atStart.isNotEmpty())

            time = 0.3f
            recompose()
            val later = pose()
            assertTrue(atStart != later, "a later animation time should move the joints")

            for (missing in listOf(99, -1, null)) {
                index = missing
                time += 0.1f
                recompose()
                assertEquals(later, pose(), "animationIndex = $missing should leave the pose alone")
            }

            setContent {}
            waitForIdle()
            assertSceneEmpty(scene)
        }
    }

    /**
     * Where no further instance can be created (here: the source data was released), the first GltfInstance takes
     * the asset's own instance and any other renders nothing. Leaving composition gives it back.
     */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun theAssetsOwnInstanceIsHandedOutOnceAtATime() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val asset = morphCube() ?: return@run skippedComposeTest()
        asset.filamentAsset.releaseSourceData()
        val own = asset.filamentAsset.instance.entities.size

        withFilamentScene(engine, scene) { setContent ->
            var shown by mutableStateOf(true)
            var created = 0
            setContent {
                if (shown) {
                    GltfInstance(asset = asset, onCreate = { created++ })
                    GltfInstance(asset = asset, onCreate = { created++ })
                }
            }
            waitForIdle()
            assertEquals(own, scene.entityCount, "only one GltfInstance can show the asset's own instance")
            assertEquals(1, created)

            shown = false
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertSceneEmpty(scene)

            shown = true
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(own, scene.entityCount, "the instance should be available again once its GltfInstance left")

            setContent {}
            waitForIdle()
            assertSceneEmpty(scene)
        }
    }
}
