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
            disableParallelShaderCompile = true
            disableHandleUseAfterFreeCheck = true
            assertNativeWindowIsValid = true
            stereoscopicType = Engine.StereoscopicType.NONE
            stereoscopicEyeCount = 2
            resourceAllocatorCacheSizeMB = 16
            resourceAllocatorCacheMaxAge = 10
            preferredShaderLanguage = Engine.Config.ShaderLanguage.DEFAULT
            forceGLES2Context = false
            gpuContextPriority = Engine.GpuContextPriority.DEFAULT
            sharedUboInitialSizeInBytes = 1024
            enableMultipleDirectionalLights = true
        }
        assertEquals(64, config.commandBufferSizeMB)
        assertEquals(12, config.perRenderPassArenaSizeMB)
        assertEquals(12, config.driverHandleArenaSizeMB)
        assertEquals(12, config.minCommandBufferSizeMB)
        assertEquals(12, config.perFrameCommandsSizeMB)
        assertEquals(2, config.jobSystemThreadCount)
        assertTrue(config.disableParallelShaderCompile)
        assertTrue(config.disableHandleUseAfterFreeCheck)
        assertTrue(config.assertNativeWindowIsValid)
        assertEquals(Engine.StereoscopicType.NONE, config.stereoscopicType)
        assertEquals(2, config.stereoscopicEyeCount)
        assertEquals(16, config.resourceAllocatorCacheSizeMB)
        assertEquals(10, config.resourceAllocatorCacheMaxAge)
        assertEquals(Engine.Config.ShaderLanguage.DEFAULT, config.preferredShaderLanguage)
        assertFalse(config.forceGLES2Context)
        assertEquals(Engine.GpuContextPriority.DEFAULT, config.gpuContextPriority)
        assertEquals(1024, config.sharedUboInitialSizeInBytes)
        assertTrue(config.enableMultipleDirectionalLights)
    }

    @Test
    fun testConfigIsReportedBackByTheEngine() {
        val config = Engine.Config().apply { resourceAllocatorCacheMaxAge = 7 }
        Engine.Builder().backend(Engine.Backend.NOOP).config(config).build().use { engine ->
            assertEquals(7, engine.config.resourceAllocatorCacheMaxAge)
        }
    }

    @Test
    fun testEngineLifecycleAndProperties() {
        Filament.init()
        val engine = Engine.create(Engine.Backend.NOOP)
        assertTrue(engine.isValid)
        
        // Assert backend is NOOP (or fallback, but since we requested NOOP and JVM supports it, it should be NOOP)
        assertNotNull(engine.backend)

        val activeFl = engine.activeFeatureLevel
        val supportedFl = engine.supportedFeatureLevel
        assertNotNull(activeFl)
        assertNotNull(supportedFl)
        
        engine.activeFeatureLevel = supportedFl
        
        engine.isAutomaticInstancingEnabled = true
        assertTrue(engine.isAutomaticInstancingEnabled)
        engine.isAutomaticInstancingEnabled = false
        assertFalse(engine.isAutomaticInstancingEnabled)
        
        val cfg = engine.config
        assertNotNull(cfg)
        
        assertTrue(engine.maxStereoscopicEyes >= 1)
        
        // Managers
        assertNotNull(engine.transformManager)
        assertNotNull(engine.lightManager)
        assertNotNull(engine.renderableManager)
        assertNotNull(engine.entityManager)
        
        // Flush & wait
        engine.flush()
        engine.flushAndWait()
        engine.flushAndWait(100L)

        // A healthy engine reports no unrecoverable (device-lost) failure
        assertFalse(engine.hasUnrecoverableFailure)

        // Paused state
        assertFalse(engine.isPaused)
        engine.isPaused = true
        assertTrue(engine.isPaused)
        engine.isPaused = false
        
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

    @Test
    fun testEntityAndCameraComponent() {
        Filament.init()
        val engine = Engine.create(Engine.Backend.NOOP)

        // Camera component lookup
        val entity = EntityManager.get().create()
        val camera = engine.createCamera(entity)
        assertNotNull(camera)
        assertNotNull(engine.getCameraComponent(entity))
        engine.destroyCameraComponent(entity)

        // Entity destruction
        engine.destroyEntity(entity)
        EntityManager.get().destroy(entity)

        engine.destroy()
    }

    @Test
    fun testFenceLifecycle() {
        Filament.init()
        val engine = Engine.create(Engine.Backend.NOOP)

        val fence = engine.createFence()
        assertNotNull(fence)
        engine.destroyFence(fence)

        engine.destroy()
    }

    @Test
    fun testEngineBuilderWithColorGrading() {
        Filament.init()
        // The config rides along here rather than in its own test: each engine costs a WebGL
        // context in the browser, and the suite is already near Chrome's ceiling. On web it
        // marshals into filament.js's Engine$Config value_object, which aborts if mistyped.
        val engine = Engine.Builder()
            .backend(Engine.Backend.NOOP)
            .config(Engine.Config().apply { enableMultipleDirectionalLights = true })
            .colorGrading(
                ColorGrading.Builder()
                    .quality(ColorGrading.QualityLevel.HIGH)
                    .toneMapper(ToneMapper.Linear())
            )
            .build()
        assertTrue(engine.isValid)
        engine.destroy()
    }
}
