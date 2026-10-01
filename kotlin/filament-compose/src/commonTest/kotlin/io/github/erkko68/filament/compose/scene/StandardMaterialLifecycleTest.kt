package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.compose.testutils.TierBSceneFixture
import io.github.erkko68.filament.compose.testutils.assertDestroyed
import io.github.erkko68.filament.compose.testutils.skippedComposeTest
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Tier-B (real-backend) coverage for the built-in [StandardMaterial]s and the reactive
 * [rememberMaterialInstance] overload. Building a [Material] from the embedded `.filamat` bytes hits a
 * GPU resource the NOOP driver panics on, so this gates on a DEFAULT backend via [TierBSceneFixture]
 * and skips where none is available.
 *
 * Verifies (1) every embedded standard material is valid `.filamat` and builds → kept alive while
 * composed → freed on disposal; (2) the reactive overload re-applies `configure` on a key change
 * without swapping the instance, and destroys it on disposal; (3) [StandardMaterialCache] shares one
 * base material per type and frees them on dispose.
 */
class StandardMaterialLifecycleTest : TierBSceneFixture() {

    // One withFilamentScene hosts the whole loop, and the tests *return* the harness result
    // (skippedComposeTest() on the skip branch) so the async web `runComposeUiTest` is awaited —
    // see skippedComposeTest's KDoc.
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun eachStandardMaterialBuildsAndIsFreed() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            for (type in StandardMaterial.entries) {
                var captured: Material? = null
                setContent { captured = rememberStandardMaterial(type) }
                waitForIdle()
                val m = assertNotNull(captured, "$type should build from embedded bytes")
                assertTrue(engine.isValid(m), "$type should be live while composed")

                setContent {}
                waitForIdle()
                assertDestroyed("$type should be destroyed after disposal") { engine.isValid(m) }
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun reactiveInstanceReappliesOnKeyChangeAndIsFreed() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val material = Material.Builder().payload(StandardMaterial.Lit.payload()).build(engine)!!

        withFilamentScene(engine, scene) { setContent ->
            var applyCount = 0
            var instance: MaterialInstance? = null
            var key by mutableStateOf(0)

            setContent {
                instance = rememberMaterialInstance(material, key) { applyCount++ }
            }
            waitForIdle()
            val first = assertNotNull(instance, "instance should be created")
            assertEquals(1, applyCount, "configure runs once on creation")
            assertTrue(engine.isValid(material, first), "instance live while composed")

            // The harness runs with mainClock.autoAdvance = false, so a state-change recomposition
            // only happens when the frame clock is ticked — waitForIdle() alone won't drive it.
            key = 1
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(2, applyCount, "configure re-applies when the key changes")
            assertSame(first, instance, "the same instance is updated in place, never swapped")

            key = 1 // unchanged — no recomposition, no re-apply
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(2, applyCount, "configure does not re-apply when the key is unchanged")

            setContent {}
            waitForIdle()
            assertDestroyed("instance should be destroyed after disposal") {
                engine.isValid(material, first)
            }

            // Inside the body: on web the harness runs asynchronously, so code placed after the
            // withFilamentScene call would destroy the material before the composition uses it.
            engine.destroy(material)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun eachInstanceHelperAppliesItsParametersAndIsFreed() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val texture = Texture.Builder().width(1).height(1).format(Texture.InternalFormat.RGBA8).build(engine)
        val red = LinearColor(1f, 0f, 0f)
        val float = MaterialInstance.FloatElement.FLOAT
        // Each helper, and a scalar it must have set (name to value).
        val helpers: List<Triple<String, @Composable () -> MaterialInstance, Pair<String, Float>>> = listOf(
            Triple("Color", { rememberColorMaterialInstance(red, roughness = 0.25f) }, "roughness" to 0.25f),
            Triple("UnlitColor", { rememberUnlitColorMaterialInstance(red) }, "" to 0f),
            Triple("Textured", { rememberTexturedMaterialInstance(texture, metallic = 0.75f) }, "metallic" to 0.75f),
            Triple("Emissive", { rememberEmissiveMaterialInstance(red, intensity = 4f) }, "intensity" to 4f),
            Triple("TransparentColor", { rememberTransparentColorMaterialInstance(red, alpha = 0.3f) }, "alpha" to 0.3f),
        )

        withFilamentScene(engine, scene) { setContent ->
            for ((name, helper, expected) in helpers) {
                var instance: MaterialInstance? = null
                setContent { instance = helper() }
                waitForIdle()
                val mi = assertNotNull(instance, name)
                val material = mi.material
                assertTrue(engine.isValid(material, mi), "$name should be live while composed")
                val (param, value) = expected
                if (param.isNotEmpty()) assertEquals(value, mi.getParameter(param, float)[0], 1e-6f, "$name.$param")

                setContent {}
                waitForIdle()
                assertDestroyed("$name should be destroyed after disposal") { engine.isValid(material) }
            }
            engine.destroy(texture) // inside the body: on web it runs after this function returns
        }
    }

    @Test
    fun cacheSharesOneMaterialPerTypeAndDisposes() {
        val engine = engine ?: return

        val cache = StandardMaterialCache(engine)
        val lit1 = cache.get(StandardMaterial.Lit)
        val lit2 = cache.get(StandardMaterial.Lit)
        assertSame(lit1, lit2, "cache returns one shared material per type")
        assertTrue(engine.isValid(lit1), "cached material is live")

        val unlit = cache.get(StandardMaterial.Unlit)
        assertTrue(lit1 != unlit, "different types get different materials")

        cache.dispose()
        assertDestroyed("cache.dispose frees the Lit material") { engine.isValid(lit1) }
        assertDestroyed("cache.dispose frees the Unlit material") { engine.isValid(unlit) }
    }
}
