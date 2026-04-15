package io.github.erkko68.filament

import io.github.erkko68.filament.jni.Engine as JniEngine
import io.github.erkko68.filament.jni.Filament

actual class Engine(val nativeEngine: JniEngine) {
    actual enum class Backend { 
        DEFAULT, OPENGL, VULKAN, METAL, WEBGPU, NOOP;
        internal fun toJni() = JniEngine.Backend.values()[ordinal]
    }

    actual enum class FeatureLevel { 
        FEATURE_LEVEL_0, FEATURE_LEVEL_1, FEATURE_LEVEL_2, FEATURE_LEVEL_3;
        internal fun toJni() = JniEngine.FeatureLevel.values()[ordinal]
    }

    actual enum class StereoscopicType { 
        NONE, INSTANCED, MULTIVIEW;
        internal fun toJni() = JniEngine.StereoscopicType.values()[ordinal]
    }

    actual enum class GpuContextPriority { 
        DEFAULT, LOW, MEDIUM, HIGH, REALTIME;
        internal fun toJni() = JniEngine.GpuContextPriority.values()[ordinal]
    }

    actual class Config actual constructor() {
        private val jni = JniEngine.Config()

        actual var commandBufferSizeMB: Long
            get() = jni.commandBufferSizeMB
            set(value) { jni.commandBufferSizeMB = value }
        actual var perRenderPassArenaSizeMB: Long
            get() = jni.perRenderPassArenaSizeMB
            set(value) { jni.perRenderPassArenaSizeMB = value }
        actual var driverHandleArenaSizeMB: Long
            get() = jni.driverHandleArenaSizeMB
            set(value) { jni.driverHandleArenaSizeMB = value }
        actual var minCommandBufferSizeMB: Long
            get() = jni.minCommandBufferSizeMB
            set(value) { jni.minCommandBufferSizeMB = value }
        actual var perFrameCommandsSizeMB: Long
            get() = jni.perFrameCommandsSizeMB
            set(value) { jni.perFrameCommandsSizeMB = value }
        actual var jobSystemThreadCount: Long
            get() = jni.jobSystemThreadCount
            set(value) { jni.jobSystemThreadCount = value }
        actual var stereoscopicType: StereoscopicType
            get() = StereoscopicType.values()[jni.stereoscopicType.ordinal]
            set(value) { jni.stereoscopicType = JniEngine.StereoscopicType.values()[value.ordinal] }
        actual var stereoscopicEyeCount: Long
            get() = jni.stereoscopicEyeCount
            set(value) { jni.stereoscopicEyeCount = value }
        actual var resourceAllocatorCacheSizeMB: Long
            get() = jni.resourceAllocatorCacheSizeMB
            set(value) { jni.resourceAllocatorCacheSizeMB = value }
        actual var resourceAllocatorCacheMaxAge: Long
            get() = jni.resourceAllocatorCacheMaxAge
            set(value) { jni.resourceAllocatorCacheMaxAge = value }

        actual enum class ShaderLanguage { DEFAULT, MSL, METAL_LIBRARY }
        actual var preferredShaderLanguage: ShaderLanguage
            get() = ShaderLanguage.values()[jni.preferredShaderLanguage.ordinal]
            set(value) { jni.preferredShaderLanguage = JniEngine.Config.ShaderLanguage.values()[value.ordinal] }
        actual var forceGLES2Context: Boolean
            get() = jni.forceGLES2Context
            set(value) { jni.forceGLES2Context = value }
        actual var gpuContextPriority: GpuContextPriority
            get() = GpuContextPriority.values()[jni.gpuContextPriority.ordinal]
            set(value) { jni.gpuContextPriority = JniEngine.GpuContextPriority.values()[value.ordinal] }
        actual var sharedUboInitialSizeInBytes: Long
            get() = jni.sharedUboInitialSizeInBytes
            set(value) { jni.sharedUboInitialSizeInBytes = value }

        internal fun toJni() = jni
    }

    actual class Builder actual constructor() {
        private val jni = JniEngine.Builder()

        actual fun backend(backend: Backend): Builder { jni.backend(backend.toJni()); return this }
        actual fun sharedContext(sharedContext: Any): Builder { jni.sharedContext(sharedContext); return this }
        actual fun config(config: Config): Builder { jni.config(config.toJni()); return this }
        actual fun featureLevel(featureLevel: FeatureLevel): Builder { jni.featureLevel(featureLevel.toJni()); return this }
        actual fun paused(paused: Boolean): Builder { jni.paused(paused); return this }
        actual fun feature(name: String, value: Boolean): Builder { jni.feature(name, value); return this }
        actual fun build(): Engine {
            Filament.init()
            return Engine(jni.build())
        }
    }

    actual companion object {
        actual fun create(): Engine {
            Filament.init()
            return Engine(JniEngine.create())
        }
        actual fun create(backend: Backend): Engine {
            Filament.init()
            return Engine(JniEngine.create(backend.toJni()))
        }
        actual fun create(sharedContext: Any): Engine {
            Filament.init()
            return Engine(JniEngine.create(sharedContext))
        }
        actual fun getSteadyClockTimeNano(): Long = JniEngine.getSteadyClockTimeNano()
    }

    actual fun isValid(): Boolean = nativeEngine.isValid()
    actual fun destroy() = nativeEngine.destroy()
    actual fun getBackend(): Backend = Backend.values()[nativeEngine.backend.ordinal]
    actual fun getSupportedFeatureLevel(): FeatureLevel = FeatureLevel.values()[nativeEngine.supportedFeatureLevel.ordinal]
    actual fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel = FeatureLevel.values()[nativeEngine.setActiveFeatureLevel(featureLevel.toJni()).ordinal]
    actual fun getActiveFeatureLevel(): FeatureLevel = FeatureLevel.values()[nativeEngine.activeFeatureLevel.ordinal]
    actual fun setAutomaticInstancingEnabled(enable: Boolean) = nativeEngine.setAutomaticInstancingEnabled(enable)
    actual fun isAutomaticInstancingEnabled(): Boolean = nativeEngine.isAutomaticInstancingEnabled
    actual fun getConfig(): Config {
        val config = Config()
        // We can't easily sync back from JNI Config to our Config if it's not exposed
        return config 
    }
    actual fun getMaxStereoscopicEyes(): Long = nativeEngine.maxStereoscopicEyes

    actual fun isValidRenderer(renderer: Renderer): Boolean = nativeEngine.isValidRenderer(renderer.nativeRenderer)
    actual fun isValidView(view: View): Boolean = nativeEngine.isValidView(view.nativeView)
    actual fun isValidScene(scene: Scene): Boolean = nativeEngine.isValidScene(scene.nativeScene)
    actual fun isValidFence(fence: Fence): Boolean = nativeEngine.isValidFence(fence.nativeFence)
    actual fun isValidRenderTarget(renderTarget: RenderTarget): Boolean = nativeEngine.isValidRenderTarget(renderTarget.nativeRenderTarget)
    actual fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean = nativeEngine.isValidIndexBuffer(indexBuffer.nativeIndexBuffer)
    actual fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean = nativeEngine.isValidVertexBuffer(vertexBuffer.nativeVertexBuffer)
    actual fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean = nativeEngine.isValidSkinningBuffer(skinningBuffer.nativeSkinningBuffer)
    actual fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean = nativeEngine.isValidMorphTargetBuffer(morphTargetBuffer.nativeMorphTargetBuffer)
    actual fun isValidIndirectLight(ibl: IndirectLight): Boolean = nativeEngine.isValidIndirectLight(ibl.nativeIndirectLight)
    actual fun isValidMaterial(material: Material): Boolean = nativeEngine.isValidMaterial(material.nativeMaterial)
    actual fun isValidMaterialInstance(material: Material, materialInstance: MaterialInstance): Boolean = nativeEngine.isValidMaterialInstance(material.nativeMaterial, materialInstance.nativeMaterialInstance)
    actual fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean = nativeEngine.isValidExpensiveMaterialInstance(materialInstance.nativeMaterialInstance)
    actual fun isValidSkybox(skybox: Skybox): Boolean = nativeEngine.isValidSkybox(skybox.nativeSkybox)
    actual fun isValidColorGrading(colorGrading: ColorGrading): Boolean = nativeEngine.isValidColorGrading(colorGrading.nativeColorGrading)
    actual fun isValidTexture(texture: Texture): Boolean = nativeEngine.isValidTexture(texture.nativeTexture)
    actual fun isValidStream(stream: Stream): Boolean = nativeEngine.isValidStream(stream.nativeStream)
    actual fun isValidSwapChain(swapChain: SwapChain): Boolean = nativeEngine.isValidSwapChain(swapChain.nativeSwapChain)

    actual fun createSwapChain(surface: NativeSurface): SwapChain = SwapChain(nativeEngine.createSwapChain(surface.surface))
    actual fun createSwapChain(surface: NativeSurface, flags: Long): SwapChain = SwapChain(nativeEngine.createSwapChain(surface.surface, flags))
    actual fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain = SwapChain(nativeEngine.createSwapChain(width, height, flags))
    actual fun destroySwapChain(swapChain: SwapChain) = nativeEngine.destroySwapChain(swapChain.nativeSwapChain)

    actual fun createView(): View = View(nativeEngine.createView())
    actual fun destroyView(view: View) = nativeEngine.destroyView(view.nativeView)

    actual fun createRenderer(): Renderer = Renderer(this, nativeEngine.createRenderer())
    actual fun destroyRenderer(renderer: Renderer) = nativeEngine.destroyRenderer(renderer.nativeRenderer)

    actual fun createCamera(): Camera = Camera(nativeEngine.createCamera(EntityManager.get().create()))
    actual fun createCamera(entity: Int): Camera = Camera(nativeEngine.createCamera(entity))
    actual fun getCameraComponent(entity: Int): Camera? {
        val jni = nativeEngine.getCameraComponent(entity) ?: return null
        return Camera(jni)
    }
    actual fun destroyCamera(camera: Camera) = nativeEngine.destroyCameraComponent(camera.nativeCamera.entity)
    actual fun destroyCameraComponent(entity: Int) = nativeEngine.destroyCameraComponent(entity)

    actual fun createScene(): Scene = Scene(nativeEngine.createScene())
    actual fun destroyScene(scene: Scene) = nativeEngine.destroyScene(scene.nativeScene)

    actual fun createFence(): Fence = Fence(nativeEngine.createFence())
    actual fun destroyFence(fence: Fence) = nativeEngine.destroyFence(fence.nativeFence)

    actual fun destroyIndexBuffer(indexBuffer: IndexBuffer) = nativeEngine.destroyIndexBuffer(indexBuffer.nativeIndexBuffer)
    actual fun destroyVertexBuffer(vertexBuffer: VertexBuffer) = nativeEngine.destroyVertexBuffer(vertexBuffer.nativeVertexBuffer)
    actual fun destroySkinningBuffer(skinningBuffer: SkinningBuffer) = nativeEngine.destroySkinningBuffer(skinningBuffer.nativeSkinningBuffer)
    actual fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer) = nativeEngine.destroyMorphTargetBuffer(morphTargetBuffer.nativeMorphTargetBuffer)
    actual fun destroyIndirectLight(ibl: IndirectLight) = nativeEngine.destroyIndirectLight(ibl.nativeIndirectLight)
    actual fun destroyMaterial(material: Material) = nativeEngine.destroyMaterial(material.nativeMaterial)
    actual fun destroyMaterialInstance(materialInstance: MaterialInstance) = nativeEngine.destroyMaterialInstance(materialInstance.nativeMaterialInstance)
    actual fun destroySkybox(skybox: Skybox) = nativeEngine.destroySkybox(skybox.nativeSkybox)
    actual fun destroyColorGrading(colorGrading: ColorGrading) = nativeEngine.destroyColorGrading(colorGrading.nativeColorGrading)
    actual fun destroyTexture(texture: Texture) = nativeEngine.destroyTexture(texture.nativeTexture)
    actual fun destroyRenderTarget(target: RenderTarget) = nativeEngine.destroyRenderTarget(target.nativeRenderTarget)
    actual fun destroyStream(stream: Stream) = nativeEngine.destroyStream(stream.nativeStream)
    actual fun destroyEntity(entity: Int) = nativeEngine.destroyEntity(entity)

    actual fun getTransformManager(): TransformManager = TransformManager(nativeEngine.transformManager)
    actual fun getLightManager(): LightManager = LightManager(nativeEngine.lightManager)
    actual fun getRenderableManager(): RenderableManager = RenderableManager(nativeEngine.renderableManager)
    actual fun getEntityManager(): EntityManager = EntityManager(nativeEngine.entityManager)

    actual fun flushAndWait() = nativeEngine.flushAndWait()
    actual fun flushAndWait(timeout: Long): Boolean = nativeEngine.flushAndWait(timeout)
    actual fun flush() = nativeEngine.flush()
    actual fun isPaused(): Boolean = nativeEngine.isPaused
    actual fun setPaused(paused: Boolean) = nativeEngine.setPaused(paused)
    actual fun unprotected() = nativeEngine.unprotected()
    actual fun hasFeatureFlag(name: String): Boolean = nativeEngine.hasFeatureFlag(name)
    actual fun setFeatureFlag(name: String, value: Boolean): Boolean = nativeEngine.setFeatureFlag(name, value)
    actual fun getFeatureFlag(name: String): Boolean = nativeEngine.getFeatureFlag(name)
}
