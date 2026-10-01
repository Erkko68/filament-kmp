package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.TestGlb
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
}
