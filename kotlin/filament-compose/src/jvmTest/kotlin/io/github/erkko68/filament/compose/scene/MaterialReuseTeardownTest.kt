package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.compose.EngineLifetimes
import io.github.erkko68.filament.testsupport.TestEnv
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Regression: a material torn down before one of its instances, the order an app hit when a LazyColumn item
 * holding a scene was deactivated for reuse. Filament panics on destroying a material with live instances
 * ("destroying material ... but 1 instances still alive"), which aborts the JVM; [MaterialLifetimes] defers it
 * to the last instance instead.
 */
class MaterialReuseTeardownTest {

    @Test
    fun materialDestroyedBeforeItsInstanceGoesWithIt() {
        if (!TestEnv.gpuBackendAvailable) return
        Filament.init()
        val engine = Engine.create(Engine.Backend.DEFAULT)
        try {
            val material = Material.Builder().payload(StandardMaterial.Lit.payload()).build(engine)
            val first = MaterialLifetimes.createInstance(material)
            val second = MaterialLifetimes.createInstance(material)

            MaterialLifetimes.destroyMaterial(engine, material)
            assertTrue(engine.isValidMaterial(material), "the material outlives its live instances")

            MaterialLifetimes.destroyInstance(engine, material, first)
            assertTrue(engine.isValidMaterial(material), "one instance is still alive")

            MaterialLifetimes.destroyInstance(engine, material, second)
            assertFalse(engine.isValidMaterial(material), "the last instance takes the material with it")
        } finally {
            engine.destroy()
        }
    }

    /** The engine's owner leaving first (an engine hoisted above a recycled SubcomposeLayout item). */
    @Test
    fun engineReleasedByItsOwnerWaitsForItsUsers() {
        if (!TestEnv.gpuBackendAvailable) return
        Filament.init()
        val engine = Engine.create(Engine.Backend.DEFAULT)
        EngineLifetimes.retain(engine)
        EngineLifetimes.destroyWhenUnused(engine)
        assertTrue(engine.isValid, "a user still holds the engine")
        val scene = engine.createScene()
        engine.destroyScene(scene)
        EngineLifetimes.release(engine)
        assertFalse(engine.isValid, "the last user takes the engine with it")
    }

    /** An instance destroyed while a renderable still draws with it waits for that renderable. */
    @Test
    fun instanceInUseWaitsForItsRenderable() {
        if (!TestEnv.gpuBackendAvailable) return
        Filament.init()
        val engine = Engine.create(Engine.Backend.DEFAULT)
        try {
            val material = Material.Builder().payload(StandardMaterial.Lit.payload()).build(engine)
            val instance = MaterialLifetimes.createInstance(material)
            MaterialLifetimes.use(instance)
            MaterialLifetimes.destroyInstance(engine, material, instance)
            MaterialLifetimes.destroyMaterial(engine, material)
            assertTrue(engine.isValidMaterialInstance(material, instance), "a renderable still draws with the instance")
            MaterialLifetimes.unuse(instance)
            assertFalse(engine.isValidMaterial(material), "instance, then material, go with the renderable")
        } finally {
            engine.destroy()
        }
    }
}
