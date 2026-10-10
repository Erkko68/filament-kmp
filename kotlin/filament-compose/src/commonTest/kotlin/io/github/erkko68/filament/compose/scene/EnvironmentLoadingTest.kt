package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.compose.testutils.GraphicsReady
import io.github.erkko68.filament.compose.testutils.TestKtx
import io.github.erkko68.filament.compose.testutils.TestMaterials
import io.github.erkko68.filament.compose.testutils.awaitGraphicsReady
import io.github.erkko68.filament.compose.testutils.withUiThreadFilamentScene
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.awaitCancellation

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

    /** A loader that throws reports the exception to `onError` and yields nothing, rather than failing composition. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun failingLoadsAreReportedNotThrown() = withUiThreadFilamentScene { setContent, engine, _ ->
        val boom = IllegalStateException("no such file")
        val errors = mutableListOf<Pair<String, Throwable>>()
        var results: List<Any?> = listOf("not composed")
        var ktx: Environment? = null
        var hdr: Environment? = null
        setContent {
            results = listOf(
                rememberMaterial(onError = { errors += "material" to it }, engine = engine) { throw boom },
                rememberTexture(onError = { errors += "texture" to it }, engine = engine) { throw boom },
                rememberGltfAsset(onError = { errors += "gltf" to it }, engine = engine) { throw boom },
            )
            ktx = rememberKTXEnvironment(engine, onError = { errors += "ktx" to it }, skybox = { throw boom }, ibl = { throw boom })
            hdr = rememberHDREnvironment(engine, showSkybox = false, onError = { errors += "hdr" to it }) { throw boom }
        }
        frameUntil { errors.size >= 6 }
        assertEquals(listOf("gltf", "hdr", "ktx", "ktx", "material", "texture"), errors.map { it.first }.sorted())
        assertTrue(errors.all { it.second === boom }, "onError should receive the loader's own exception")
        assertEquals(listOf<Any?>(null, null, null), results)
        assertNull(assertNotNull(ktx).indirectLightState.reflections)
        assertNull(assertNotNull(ktx).skyboxState?.source)
        assertNull(assertNotNull(hdr).indirectLightState.reflections)
        assertNull(assertNotNull(hdr).skyboxState, "showSkybox = false makes no skybox state")
    }

    /** Leaving the composition while a load is still running cancels it; that is not an error to report. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun leavingMidLoadIsNotAnError() = withUiThreadFilamentScene { setContent, engine, _ ->
        val errors = mutableListOf<Throwable>()
        var started = 0
        val never: suspend () -> ByteArray = { started++; awaitCancellation() }
        setContent {
            rememberMaterial(onError = { errors += it }, engine = engine, load = never)
            rememberTexture(onError = { errors += it }, engine = engine, load = never)
            rememberGltfAsset(onError = { errors += it }, engine = engine, load = never)
            rememberKTXEnvironment(engine, onError = { errors += it }, skybox = never, ibl = never)
            rememberHDREnvironment(engine, onError = { errors += it }, hdr = never)
        }
        frameUntil { started == 6 }
        setContent {}
        waitForIdle()
        mainClock.advanceTimeByFrame()
        waitForIdle()
        assertEquals(emptyList(), errors)
    }

    /** A KTX environment without a skybox loader has no skybox state, and a material arriving late is still owned. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun asyncMaterialIsBuiltAndFreed() = withUiThreadFilamentScene { setContent, engine, _ ->
        val bytes = TestMaterials.getEmissiveMaterialBytes()
        if (bytes.isEmpty()) return@withUiThreadFilamentScene // not bundled on this target
        var material: Material? = null
        var environment: Environment? = null
        setContent {
            material = rememberMaterial(engine = engine) { bytes }
            environment = rememberKTXEnvironment(engine, ibl = { TestKtx.ibl })
        }
        frameUntil { material != null && environment?.indirectLightState?.reflections != null }
        val built = assertNotNull(material)
        assertTrue(engine.isValid(built))
        assertNull(assertNotNull(environment).skyboxState)

        setContent {}
        waitForIdle()
        assertTrue(!engine.isValid(built), "disposal destroys the material")
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
