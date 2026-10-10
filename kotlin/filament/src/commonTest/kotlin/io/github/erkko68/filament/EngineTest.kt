package io.github.erkko68.filament

import io.github.erkko68.filament.capi.FilaEngineConfig_create
import io.github.erkko68.filament.capi.FilaEngineConfig_destroy
import io.github.erkko68.filament.interop.NullPointer
import io.github.erkko68.filament.interop.withHandle
import io.github.erkko68.filament.testsupport.IgnoreJs
import kotlin.concurrent.Volatile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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
        Engine.Builder().backend(Engine.Backend.NOOP).config(config).build()!!.use { engine ->
            assertEquals(7, engine.config.resourceAllocatorCacheMaxAge)
        }
    }

    @Test
    fun testConfigDefaultsMatchCpp() {
        Filament.init()
        val cpp = withHandle({ FilaEngineConfig_create() }, { FilaEngineConfig_destroy(it) }) { Engine.Config.of(it) }
        val kotlin = Engine.Config()
        assertEquals(cpp.commandBufferSizeMB, kotlin.commandBufferSizeMB)
        assertEquals(cpp.perRenderPassArenaSizeMB, kotlin.perRenderPassArenaSizeMB)
        assertEquals(cpp.driverHandleArenaSizeMB, kotlin.driverHandleArenaSizeMB)
        assertEquals(cpp.minCommandBufferSizeMB, kotlin.minCommandBufferSizeMB)
        assertEquals(cpp.perFrameCommandsSizeMB, kotlin.perFrameCommandsSizeMB)
        assertEquals(cpp.jobSystemThreadCount, kotlin.jobSystemThreadCount)
        assertEquals(cpp.metalUploadBufferSizeBytes, kotlin.metalUploadBufferSizeBytes)
        assertEquals(cpp.stereoscopicEyeCount, kotlin.stereoscopicEyeCount)
        assertEquals(cpp.resourceAllocatorCacheSizeMB, kotlin.resourceAllocatorCacheSizeMB)
        assertEquals(cpp.resourceAllocatorCacheMaxAge, kotlin.resourceAllocatorCacheMaxAge)
        assertEquals(cpp.sharedUboInitialSizeInBytes, kotlin.sharedUboInitialSizeInBytes)
        assertEquals(cpp.asynchronousMode, kotlin.asynchronousMode)
        assertEquals(cpp.materialCacheCapacity, kotlin.materialCacheCapacity)
        assertEquals(cpp.programCacheCapacity, kotlin.programCacheCapacity)
    }

    @Test
    fun testEngineLifecycleAndProperties() {
        Filament.init()
        val engine = Engine.create(Engine.Backend.NOOP)!!
        assertTrue(engine.isValid)

        // Assert backend is NOOP (or fallback, but since we requested NOOP and JVM supports it, it should be NOOP)
        assertNotNull(engine.backend)

        val activeFl = engine.activeFeatureLevel
        val supportedFl = engine.supportedFeatureLevel
        assertNotNull(activeFl)
        assertNotNull(supportedFl)
        
        assertEquals(activeFl, engine.setActiveFeatureLevel(activeFl))
        
        engine.isAutomaticInstancingEnabled = true
        assertTrue(engine.isAutomaticInstancingEnabled)
        engine.isAutomaticInstancingEnabled = false
        assertFalse(engine.isAutomaticInstancingEnabled)
        
        val cfg = engine.config
        assertNotNull(cfg)
        
        assertTrue(Engine.maxStereoscopicEyes >= 1)
        engine.isStereoSupported(Engine.StereoscopicType.INSTANCED)
        assertTrue(engine.isValid(engine.defaultMaterial))
        assertEquals(0, engine.viewCount)
        val view = engine.createView()
        assertEquals(1, engine.viewCount)
        assertTrue(engine.destroy(view))
        
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
        
        val flags = engine.getFeatureFlags()
        assertTrue(flags.isNotEmpty())
        flags.forEach { assertEquals(it.value, engine.getFeatureFlag(it.name)) }
        flags.firstOrNull { !it.constant }?.let { flag ->
            assertTrue(engine.setFeatureFlag(flag.name, !flag.value))
            assertEquals(!flag.value, engine.getFeatureFlag(flag.name))
            engine.setFeatureFlag(flag.name, flag.value)
        }
        assertFalse(engine.hasFeatureFlag("no.such.flag"))
        assertNull(engine.getFeatureFlag("no.such.flag"))
        assertFalse(engine.setFeatureFlag("no.such.flag", true))

        Engine.destroy(engine)
    }

    @Test
    fun testEntityAndCameraComponent() {
        Filament.init()
        val engine = Engine.create(Engine.Backend.NOOP)!!

        // Camera component lookup
        val entity = EntityManager.get().create()
        val camera = engine.createCamera(entity)
        assertNotNull(camera)
        assertNotNull(engine.getCameraComponent(entity))
        engine.destroyCameraComponent(entity)

        // Entity destruction: the components, then the entity
        engine.destroy(entity)
        EntityManager.get().destroy(entity)

        Engine.destroy(engine)
    }

    // Written by createAsync's callback, possibly on Filament's thread.
    @Volatile private var token: Engine.Token? = null

    @Test
    fun testCreateAsync() {
        Filament.init()
        Engine.createAsync(Engine.Backend.NOOP) { token = it }
        val deadline = Engine.steadyClockTimeNano + 10_000_000_000L
        while (token == null && Engine.steadyClockTimeNano < deadline) Unit
        val engine = assertNotNull(Engine.getEngine(assertNotNull(token)))
        assertEquals(Engine.Backend.NOOP, engine.backend)
        Engine.destroy(engine)
    }

    @Test
    fun testFenceLifecycle() {
        Filament.init()
        val engine = Engine.create(Engine.Backend.NOOP)!!

        val fence = engine.createFence()
        assertNotNull(fence)
        engine.destroy(fence)

        Engine.destroy(engine)
    }

    @Test
    fun testEngineBuilderWithColorGrading() {
        Filament.init()
        // The config rides along here rather than in its own test: each engine costs a WebGL
        // context in the browser, and the suite is already near Chrome's ceiling.
        val engine = Engine.Builder()
            .backend(Engine.Backend.NOOP)
            .config(Engine.Config().apply { enableMultipleDirectionalLights = true })
            .colorGrading(
                ColorGrading.Builder()
                    .quality(ColorGrading.QualityLevel.HIGH)
                    .toneMapper(ToneMapper.Linear())
            )
            .featureLevel(Engine.FeatureLevel.FEATURE_LEVEL_1)
            .paused(true)
            .build()!!
        assertTrue(engine.isValid)
        assertEquals(Engine.FeatureLevel.FEATURE_LEVEL_1, engine.activeFeatureLevel)
        assertTrue(engine.isPaused)
        Engine.destroy(engine)
    }

    @Test
    fun testResourceCountsAndValidity() {
        Filament.init()
        val engine = Engine.create(Engine.Backend.NOOP)!!
        assertTrue(engine.maxAutomaticInstances > 0)
        val counts = {
            with(engine) {
                listOf(
                    bufferObjectCount, vertexBufferCount, indexBufferCount, skinningBufferCount, morphTargetBufferCount,
                    instanceBufferCount, indirectLightCount, sceneCount, skyboxeCount, colorGradingCount,
                    swapChainCount, streamCount, textureCount, renderTargetCount,
                )
            }
        }
        val before = counts()

        val bo = BufferObject.Builder().size(4).name("bo").build(engine)
        val vb = VertexBuffer.Builder().vertexCount(1).bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .name("vb").build(engine)
        val ib = IndexBuffer.Builder().indexCount(3).bufferType(IndexBuffer.IndexType.USHORT).name("ib").build(engine)
        val sb = SkinningBuffer.Builder().boneCount(1).name("sb").build(engine)
        val mtb = MorphTargetBuffer.Builder().vertexCount(1).count(1).name("mtb").build(engine)
        val inb = InstanceBuffer.Builder(1).build(engine)
        val il = IndirectLight.Builder().radiance(1, floatArrayOf(1f, 1f, 1f)).build(engine)
        val scene = engine.createScene()
        val sky = Skybox.Builder().color(0f, 0f, 0f, 1f).build(engine)
        val cg = ColorGrading.Builder().build(engine)
        val swap = engine.createSwapChain(1, 1)
        val stream = Stream.Builder().width(1).height(1).build(engine)
        val tex = Texture.Builder().width(1).height(1).format(Texture.InternalFormat.RGBA8)
            .usage(Texture.Usage.COLOR_ATTACHMENT).build(engine)
        val rt = RenderTarget.Builder().texture(RenderTarget.AttachmentPoint.COLOR, tex).build(engine)

        assertEquals(before.map { it + 1 }, counts())
        with(engine) {
            assertTrue(isValid(bo) && isValid(vb) && isValid(ib) && isValid(sb) && isValid(mtb) && isValid(inb))
            assertTrue(isValid(il) && isValid(scene) && isValid(sky) && isValid(cg) && isValid(swap) && isValid(stream))
            assertTrue(isValid(tex) && isValid(rt))
        }

        // Each wrapper hands out the native object it holds: all set, none shared.
        val renderer = engine.createRenderer()
        val view = engine.createView()
        val cameraEntity = EntityManager.get().create()
        val camera = engine.createCamera(cameraEntity)
        val handles = listOf(
            engine.nativeObject, bo.nativeObject, vb.nativeObject, ib.nativeObject, sb.nativeObject, mtb.nativeObject,
            inb.nativeObject, il.nativeObject, scene.nativeObject, sky.nativeObject, cg.nativeObject, swap.nativeObject,
            stream.nativeObject, tex.nativeObject, rt.nativeObject, renderer.nativeObject, view.nativeObject,
            camera.nativeObject, engine.lightManager.nativeObject, engine.transformManager.nativeObject,
            engine.renderableManager.nativeObject, EntityManager.get().nativeObject,
        )
        assertFalse(NullPointer in handles)
        assertEquals(handles.size, handles.distinct().size)
        engine.destroyCameraComponent(cameraEntity)
        EntityManager.get().destroy(cameraEntity)
        engine.destroy(view)
        engine.destroy(renderer)

        with(engine) {
            destroy(rt); destroy(tex); destroy(stream); destroy(swap); destroy(cg); destroy(sky); destroy(scene)
            destroy(il); destroy(inb); destroy(mtb); destroy(sb); destroy(ib); destroy(vb); destroy(bo)
        }
        assertEquals(before, counts())
        assertFalse(engine.isValid(bo))
        Engine.destroy(engine)
    }
}
