package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.compose.testutils.GraphicsReady
import io.github.erkko68.filament.compose.testutils.TestKtx
import io.github.erkko68.filament.compose.testutils.awaitGraphicsReady
import io.github.erkko68.filament.compose.testutils.withUiThreadFilamentScene
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The async loaders ([rememberHDREnvironment], [rememberTexture]): bytes arrive from an effect, then
 * the GPU work runs in composition, so everything stays on the UI thread ([withUiThreadFilamentScene]).
 */
class EnvironmentLoadingTest {

    @BeforeTest
    fun awaitGraphics(): GraphicsReady = awaitGraphicsReady()

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.frameUntil(done: () -> Boolean) {
        repeat(50) {
            if (done()) return
            mainClock.advanceTimeByFrame()
            waitForIdle()
        }
        assertTrue(done(), "the async load never finished")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun hdrEnvironmentPrefiltersIntoReflectionsAndSkybox() = withUiThreadFilamentScene { setContent, engine, _ ->
        // A flat 1×1 Radiance image: header, blank line, resolution, one RGBE pixel.
        val hdr = "#?RADIANCE\nFORMAT=32-bit_rle_rgbe\n\n-Y 1 +X 1\n".encodeToByteArray() + byteArrayOf(-128, 64, 32, -127)
        var environment: Environment? = null
        setContent { environment = rememberHDREnvironment(engine, hdr = { hdr }) }
        frameUntil { environment?.indirectLightState?.reflections != null }

        val env = assertNotNull(environment)
        assertTrue(env.skyboxState?.source is SkyboxSource.Cubemap)
        setContent {}
        waitForIdle()
        assertNull(env.indirectLightState.reflections, "disposal clears the reflections")
        assertNull(env.skyboxState?.source)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun ktxEnvironmentLoadsReflectionsHarmonicsAndSkybox() = withUiThreadFilamentScene { setContent, engine, _ ->
        var environment: Environment? = null
        setContent {
            environment = rememberKTXEnvironment(engine, skybox = { TestKtx.skybox }, ibl = { TestKtx.ibl })
        }
        frameUntil { environment?.indirectLightState?.reflections != null && environment?.skyboxState?.source != null }

        val env = assertNotNull(environment)
        assertNotNull(env.indirectLightState.irradianceSh, "cmgen writes spherical harmonics into the IBL")
        assertTrue(env.skyboxState?.source is SkyboxSource.Cubemap)
        setContent {}
        waitForIdle()
        assertNull(env.indirectLightState.reflections, "disposal clears the reflections")
        assertNull(env.skyboxState?.source)
    }

    /**
     * A reloaded environment reaches the scene a frame after its states change, and that frame is rendered first:
     * the textures the scene still draws with must outlive it.
     */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun reloadedEnvironmentKeepsTheTexturesTheSceneStillDraws() = withUiThreadFilamentScene { setContent, engine, scene ->
        var key by mutableStateOf(0)
        setContent {
            val environment = rememberKTXEnvironment(engine, key = key, skybox = { TestKtx.skybox.copyOf() }, ibl = { TestKtx.ibl.copyOf() })
            ApplyIndirectLight(environment.indirectLightState, engine, scene)
            environment.skyboxState?.let { ApplySkybox(it, engine, scene) }
        }
        frameUntil { scene.indirectLight?.reflectionsTexture != null && scene.skybox?.texture != null }
        val reflections = assertNotNull(scene.indirectLight?.reflectionsTexture)
        val sky = assertNotNull(scene.skybox?.texture)

        key = 1
        var reloaded = false
        repeat(50) {
            if (reloaded) return@repeat
            mainClock.advanceTimeByFrame()
            scene.indirectLight?.reflectionsTexture?.let { assertTrue(engine.isValid(it), "the scene's IBL lost its reflections") }
            scene.skybox?.texture?.let { assertTrue(engine.isValid(it), "the scene's skybox lost its texture") }
            reloaded = !engine.isValid(reflections) && !engine.isValid(sky)
        }
        assertTrue(reloaded, "the replaced textures are destroyed once the scene has let go")
        val reloadedReflections = assertNotNull(scene.indirectLight?.reflectionsTexture)
        val reloadedSky = assertNotNull(scene.skybox?.texture)

        setContent {}
        waitForIdle()
        assertTrue(!engine.isValid(reloadedReflections) && !engine.isValid(reloadedSky), "disposal destroys the textures")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun rememberTextureDecodesAnImage() = withUiThreadFilamentScene { setContent, engine, _ ->
        var texture: Texture? = null
        setContent { texture = rememberTexture(engine = engine) { ONE_PIXEL_PNG } }
        frameUntil { texture != null }
        val loaded = assertNotNull(texture)
        assertEquals(1, loaded.getWidth(0))
        assertTrue(engine.isValid(loaded))

        setContent {}
        waitForIdle()
        assertTrue(!engine.isValid(loaded), "disposal destroys the texture")
    }

    private companion object {
        /** A 1×1 opaque red RGBA PNG. */
        val ONE_PIXEL_PNG = byteArrayOf(
            -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 1, 0, 0, 0, 1, 8, 6, 0, 0, 0, 31, 21,
            -60, -119, 0, 0, 0, 13, 73, 68, 65, 84, 120, -100, 99, -8, -49, -64, -16, 31, 0, 5, 0, 1, -1, -119, -103, 61,
            29, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126,
        )
    }
}
