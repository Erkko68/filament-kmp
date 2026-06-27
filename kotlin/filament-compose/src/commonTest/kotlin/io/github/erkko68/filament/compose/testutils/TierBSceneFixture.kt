package io.github.erkko68.filament.compose.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.Scene
import io.github.erkko68.filament.testsupport.TestEnv
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Base fixture for Tier-B compose tests: a real ([Engine.Backend.DEFAULT]) engine + a [Scene], for the
 * GPU-resource composables (primitives, materials, textures) that the NOOP driver panics on. Mirrors
 * the core [io.github.erkko68.filament.testutils.RenderingTestFixture] gating — [engine] is null when no
 * backend is available here, so every test must early-return (`val engine = engine ?: return`) and skip
 * gracefully instead of failing. Assertions stay binding-level (handles valid / managers report the
 * component), never pixels.
 *
 * Materials built via [materialInstance] are tracked and destroyed in [tearDown], so a test can hand a
 * live [MaterialInstance] to a primitive composable without owning its lifecycle.
 */
open class TierBSceneFixture {
    protected var engine: Engine? = null
    protected var scene: Scene? = null

    private val materials = mutableListOf<Material>()
    private val materialInstances = mutableListOf<MaterialInstance>()

    @BeforeTest
    fun awaitGraphics(): GraphicsReady = awaitGraphicsReady()

    @BeforeTest
    fun setUp() {
        Filament.init()
        // See RenderingTestFixture: don't attempt Engine.create on a host with no GPU/display — Filament
        // aborts on its driver thread there, which a try/catch can't recover.
        if (!TestEnv.gpuBackendAvailable) return
        val e = try {
            Engine.create(Engine.Backend.DEFAULT).takeIf { it.isValid() }
        } catch (t: Throwable) {
            null
        } ?: return
        engine = e
        scene = e.createScene()
    }

    @AfterTest
    fun tearDown() {
        engine?.let { e ->
            materialInstances.forEach { e.destroyMaterialInstance(it) }
            materials.forEach { e.destroyMaterial(it) }
            scene?.let { e.destroyScene(it) }
            e.flushAndWait()
            e.destroy()
        }
        materialInstances.clear()
        materials.clear()
        scene = null
        engine = null
    }

    /**
     * Builds a [MaterialInstance] from the bundled emissive material, or null when the material bytes
     * aren't available on this target (JS/native/android — see [TestMaterials]). The returned instance
     * and its base material are destroyed in [tearDown], outliving any composable that uses them.
     */
    protected fun materialInstance(): MaterialInstance? {
        val e = engine ?: return null
        val bytes = TestMaterials.getEmissiveMaterialBytes()
        if (bytes.isEmpty()) return null
        val material = Material.Builder().payload(bytes).build(e).also { materials += it }
        return material.createInstance().also { materialInstances += it }
    }
}
