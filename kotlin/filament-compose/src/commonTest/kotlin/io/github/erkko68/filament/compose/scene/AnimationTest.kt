package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.TestGlb
import io.github.erkko68.filament.compose.testutils.compositionFailure
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import io.github.erkko68.filament.gltfio.Animator
import io.github.erkko68.filament.gltfio.AssetConfiguration
import io.github.erkko68.filament.gltfio.AssetLoader
import io.github.erkko68.filament.gltfio.Gltfio
import io.github.erkko68.filament.gltfio.ResourceConfiguration
import io.github.erkko68.filament.gltfio.ResourceLoader
import io.github.erkko68.filament.gltfio.createUbershaderProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Playback logic of [AnimationMixer], [AnimationTrack] and [AnimationState] against a real gltfio
 * [Animator] (Fox: Survey, Walk, Run). gltfio loads under NOOP, so this runs everywhere.
 */
class AnimationTest : ComposeTestFixture() {

    /** Loads Fox with its resources (the animator needs them) and hands it to [block]; frees it after. */
    private fun withFox(block: (GltfAsset, Animator) -> Unit) {
        Gltfio.init()
        val provider = createUbershaderProvider(engine)
        val loader = AssetLoader.create(AssetConfiguration(engine, provider, engine.entityManager))
        val asset = assertNotNull(loader.createAsset(TestGlb.getFoxGlbBytes()))
        ResourceLoader(ResourceConfiguration(engine)).use { it.loadResources(asset) }
        try {
            block(GltfAsset(asset, loader).apply { isReady = true }, asset.instance.animator)
        } finally {
            loader.destroyAsset(asset)
            AssetLoader.destroy(loader)
            provider.destroy()
        }
    }

    @Test
    fun mixerAdvancesClampsPausesAndRemovesTracks() = withFox { _, animator ->
        val mixer = AnimationMixer()
        val walk = mixer.addTrack(1)
        val run = mixer.addTrack(2, weight = 0.5f, loop = false)
        assertEquals(listOf(walk, run), mixer.tracks.toList())

        mixer.apply(animator, 0.25f)
        assertEquals(0.25f, walk.time, 1e-5f)
        assertTrue(walk.progress > 0f)

        // Past the end: the looping track wraps, the one-shot holds its last frame and finishes.
        mixer.apply(animator, 100f)
        assertTrue(walk.time < animator.getAnimationDuration(1))
        assertFalse(walk.isFinished)
        assertEquals(animator.getAnimationDuration(2), run.time, 1e-4f)
        assertEquals(1f, run.progress)
        assertTrue(run.isFinished)

        mixer.isPaused = true
        val held = walk.time
        mixer.apply(animator, 1f)
        assertEquals(held, walk.time)

        walk.seek(0f)
        assertEquals(0f, walk.time)
        mixer.removeTrack(run)
        assertEquals(listOf(walk), mixer.tracks.toList())
        mixer.clearTracks()
        assertTrue(mixer.tracks.isEmpty())
    }

    @Test
    fun singleTrackCrossFadesBetweenClips() = withFox { _, animator ->
        val state = AnimationState(0, initialSpeed = 1f, initialCrossFadeDuration = 0.5f, initialLoop = true)
        state.apply(animator, 0.2f)
        assertEquals(0.2f, state.time, 1e-5f)
        assertTrue(state.progress > 0f)
        assertFalse(state.isTransitioning)

        state.animationIndex = 1
        state.apply(animator, 0.1f)
        assertTrue(state.isTransitioning)
        assertEquals(0.1f, state.time, 1e-5f) // the new clip starts from 0
        state.apply(animator, 0.5f)
        assertFalse(state.isTransitioning)

        state.seek(0f)
        assertEquals(0f, state.time)
    }

    /** A fade cut short while paused divides zero by zero: the pose must not turn to NaN. */
    @Test
    fun aCrossFadeCutToZeroWhilePausedKeepsThePoseFinite() = withFox { asset, animator ->
        val state = AnimationState(0, initialSpeed = 1f, initialCrossFadeDuration = 0.5f, initialLoop = true)
        state.apply(animator, 0.2f)
        state.animationIndex = 1
        state.apply(animator, 0.1f)
        assertTrue(state.isTransitioning)

        state.isPaused = true
        state.crossFadeDuration = 0f
        state.apply(animator, 0.1f)
        assertFalse(state.isTransitioning, "no duration left: the fade is over")
        val tm = engine.transformManager
        for (entity in asset.filamentAsset.entities) {
            if (!tm.hasComponent(entity)) continue
            assertTrue(tm.getTransform(tm.getInstance(entity)).all { it.isFinite() }, "entity $entity has a NaN transform")
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun rememberedTrackJoinsAndLeavesTheMixer() = withFilamentScene(engine, scene) { setContent ->
        withFox { fox, _ ->
            var state: AnimationState? = null
            var track: AnimationTrack? = null
            var mixer: AnimationMixer? = null
            var names: List<String?> = emptyList()
            var weight by mutableStateOf(1f)
            setContent {
                val s = rememberAnimationState(initialAnimationIndex = null).also { state = it }
                track = rememberAnimationTrack(s, index = 1, weight = weight)
                mixer = rememberAnimationMixer()
                names = rememberAnimationNames(fox)
            }
            waitForIdle()
            assertEquals(listOf("Survey", "Walk", "Run"), names)
            assertEquals(listOf(track), state!!.mixer.tracks.toList())
            assertTrue(mixer!!.tracks.isEmpty())

            weight = 0.25f
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(0.25f, track!!.weight)

            setContent {}
            waitForIdle()
            assertTrue(state!!.mixer.tracks.isEmpty(), "the track leaves the mixer with its composition")
        }
    }

    /** A clip the asset doesn't have, or none at all, plays nothing instead of reaching the animator. */
    @Test
    fun missingClipsPlayNothing() = withFox { _, animator ->
        for (index in listOf(null, -1, animator.animationCount)) {
            val state = AnimationState(index, initialSpeed = 1f, initialCrossFadeDuration = 0.3f, initialLoop = true)
            state.apply(animator, 0.2f)
            assertEquals(0f, state.time, "index $index")
            assertEquals(0f, state.progress, "index $index")
        }

        // Fading in from a missing clip plays only the incoming one, and the fade still ends.
        val state = AnimationState(99, initialSpeed = 1f, initialCrossFadeDuration = 0.5f, initialLoop = true)
        state.apply(animator, 0.1f)
        state.animationIndex = 1
        state.apply(animator, 0.1f)
        assertEquals(0.1f, state.time, 1e-5f)
        state.apply(animator, 1f)
        assertFalse(state.isTransitioning)

        // Starting from no clip there is nothing to fade from.
        val fromNothing = AnimationState(null, initialSpeed = 1f, initialCrossFadeDuration = 0.5f, initialLoop = true)
        fromNothing.apply(animator, 0.1f)
        fromNothing.animationIndex = 0
        fromNothing.apply(animator, 0.1f)
        assertFalse(fromNothing.isTransitioning)
        assertEquals(0.1f, fromNothing.time, 1e-5f)

        // In the mixer a missing track stands still; a silent one keeps time without being applied.
        val mixer = AnimationMixer()
        val missing = mixer.addTrack(99)
        val silent = mixer.addTrack(1, weight = 0f)
        mixer.apply(animator, 0.25f)
        assertEquals(0f, missing.time)
        assertEquals(0f, missing.progress)
        assertEquals(0.25f, silent.time, 1e-5f)
    }

    /** Without looping the clip holds its last frame; a state driving its mixer leaves the single clip alone. */
    @Test
    fun oneShotHoldsItsEndAndTheMixerTakesOver() = withFox { _, animator ->
        val once = AnimationState(0, initialSpeed = 1f, initialCrossFadeDuration = 0f, initialLoop = false)
        once.apply(animator, 1000f)
        assertEquals(animator.getAnimationDuration(0), once.time, 1e-4f)
        assertEquals(1f, once.progress)

        // No cross-fade duration: a new clip cuts in at once.
        once.animationIndex = 1
        once.apply(animator, 0.1f)
        assertFalse(once.isTransitioning)

        val mixed = AnimationState(0, initialSpeed = 1f, initialCrossFadeDuration = 0.3f, initialLoop = true)
        val track = mixed.mixer.addTrack(2)
        mixed.apply(animator, 0.2f)
        assertEquals(0.2f, track.time, 1e-5f)
        assertEquals(0f, mixed.time, "the single clip is not advanced while tracks are mixing")
        mixed.isPaused = true
        mixed.apply(animator, 0.2f)
        assertEquals(0.2f, track.time, 1e-5f)
    }

    /** Names are only readable from a loaded asset. */
    @Test
    fun animationNamesNeedAReadyAsset() = withFox { asset, _ ->
        var names: List<String?>? = null
        assertNull(compositionFailure(engine, scene) { names = rememberAnimationNames(null) })
        assertEquals(emptyList(), names)
        asset.isReady = false
        assertNull(compositionFailure(engine, scene) { names = rememberAnimationNames(asset) })
        assertEquals(emptyList(), names)
        asset.isReady = true
        assertNull(compositionFailure(engine, scene) { names = rememberAnimationNames(asset) })
        assertEquals(listOf<String?>("Survey", "Walk", "Run"), names)
    }
}
