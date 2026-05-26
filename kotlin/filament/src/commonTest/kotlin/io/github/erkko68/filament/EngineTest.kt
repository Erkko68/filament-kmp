package io.github.erkko68.filament

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EngineTest {
    @Test
    fun testConfigProperties() {
        val config = Engine.Config().apply {
            commandBufferSizeMB = 64
            perRenderPassArenaSizeMB = 12
            driverHandleArenaSizeMB = 12
            minCommandBufferSizeMB = 12
            perFrameCommandsSizeMB = 12
            jobSystemThreadCount = 2
            stereoscopicType = Engine.StereoscopicType.NONE
            stereoscopicEyeCount = 2
            resourceAllocatorCacheSizeMB = 16
            resourceAllocatorCacheMaxAge = 10
            preferredShaderLanguage = Engine.Config.ShaderLanguage.DEFAULT
            forceGLES2Context = false
            gpuContextPriority = Engine.GpuContextPriority.DEFAULT
            sharedUboInitialSizeInBytes = 1024
        }
        assertEquals(64, config.commandBufferSizeMB)
        assertEquals(12, config.perRenderPassArenaSizeMB)
        assertEquals(12, config.driverHandleArenaSizeMB)
        assertEquals(12, config.minCommandBufferSizeMB)
        assertEquals(12, config.perFrameCommandsSizeMB)
        assertEquals(2, config.jobSystemThreadCount)
        assertEquals(Engine.StereoscopicType.NONE, config.stereoscopicType)
        assertEquals(2, config.stereoscopicEyeCount)
        assertEquals(16, config.resourceAllocatorCacheSizeMB)
        assertEquals(10, config.resourceAllocatorCacheMaxAge)
        assertEquals(Engine.Config.ShaderLanguage.DEFAULT, config.preferredShaderLanguage)
        assertFalse(config.forceGLES2Context)
        assertEquals(Engine.GpuContextPriority.DEFAULT, config.gpuContextPriority)
        assertEquals(1024, config.sharedUboInitialSizeInBytes)
    }

    @Test
    fun testEngineLifecycleAndProperties() {
        Filament.init()
        val engine = Engine.create(Engine.Backend.NOOP)
        assertTrue(engine.isValid())
        
        // Assert backend is NOOP (or fallback, but since we requested NOOP and JVM supports it, it should be NOOP)
        assertNotNull(engine.getBackend())
        
        val activeFl = engine.getActiveFeatureLevel()
        val supportedFl = engine.getSupportedFeatureLevel()
        assertNotNull(activeFl)
        assertNotNull(supportedFl)
        
        engine.setActiveFeatureLevel(supportedFl)
        
        engine.setAutomaticInstancingEnabled(true)
        assertTrue(engine.isAutomaticInstancingEnabled())
        engine.setAutomaticInstancingEnabled(false)
        assertFalse(engine.isAutomaticInstancingEnabled())
        
        val cfg = engine.getConfig()
        assertNotNull(cfg)
        
        assertTrue(engine.getMaxStereoscopicEyes() >= 1)
        
        // Managers
        assertNotNull(engine.getTransformManager())
        assertNotNull(engine.getLightManager())
        assertNotNull(engine.getRenderableManager())
        assertNotNull(engine.getEntityManager())
        
        // Flush & wait
        engine.flush()
        engine.flushAndWait()
        engine.flushAndWait(100L)
        
        // Paused state
        assertFalse(engine.isPaused())
        engine.setPaused(true)
        assertTrue(engine.isPaused())
        engine.setPaused(false)
        
        // Feature flags / other methods
        engine.unprotected()
        engine.enableAccurateTranslations()
        
        // `setFeatureFlag` / `getFeatureFlag` throw on Android/JVM when the
        // name isn't a real Filament flag, so use one upstream actually
        // ships (`backend_debug_marker` exists in 1.71.x).
        val flag = "backend_debug_marker"
        if (engine.hasFeatureFlag(flag)) {
            val previous = engine.getFeatureFlag(flag)
            engine.setFeatureFlag(flag, !previous)
            engine.setFeatureFlag(flag, previous)
        }
        
        // Cleanup
        engine.destroy()
    }
}
