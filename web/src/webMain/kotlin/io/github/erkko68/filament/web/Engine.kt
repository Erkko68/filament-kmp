package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class Engine : JsAny {
fun execute(): Unit
fun createCamera(entity: Entity): Camera
fun createMaterial(urlOrBuffer: BufferReference, options: EngineCreateMaterialOptions = definedExternally): Material
fun createRenderer(): Renderer
fun createFence(): Fence
fun destroyFence(fence: Fence): Unit
fun createScene(): Scene
fun createSwapChain(): SwapChain
fun createTextureFromJpeg(urlOrBuffer: BufferReference, options: JsAny = definedExternally): Texture
fun createTextureFromPng(urlOrBuffer: BufferReference, options: JsAny = definedExternally): Texture
fun createIblFromKtx1(urlOrBuffer: BufferReference): IndirectLight
fun createSkyFromKtx1(urlOrBuffer: BufferReference): Skybox
fun createTextureFromKtx1(urlOrBuffer: BufferReference, options: JsAny = definedExternally): Texture
fun createTextureFromKtx2(urlOrBuffer: BufferReference, options: JsAny = definedExternally): Texture
fun createView(): View
fun createAssetLoader(): gltfio_AssetLoader
fun destroySwapChain(swapChain: SwapChain): Unit
fun destroyRenderer(renderer: Renderer): Unit
fun destroyView(view: View): Unit
fun destroyScene(scene: Scene): Unit
fun destroyCameraComponent(camera: Entity): Unit
fun destroyMaterial(material: Material): Unit
fun destroyEntity(entity: Entity): Unit
fun destroyIndexBuffer(indexBuffer: IndexBuffer): Unit
fun destroyIndirectLight(indirectLight: IndirectLight): Unit
fun destroyMaterialInstance(materialInstance: MaterialInstance): Unit
fun destroyRenderTarget(renderTarget: RenderTarget): Unit
fun destroySkybox(skybox: Skybox): Unit
fun destroyTexture(texture: Texture): Unit
fun destroyColorGrading(colorGrading: ColorGrading): Unit
fun getCameraComponent(entity: Entity): Camera
fun getLightManager(): LightManager
fun destroyVertexBuffer(vertexBuffer: VertexBuffer): Unit
fun destroySkinningBuffer(skinningBuffer: SkinningBuffer): Unit
fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Unit
fun getRenderableManager(): RenderableManager
fun getSupportedFormatSuffix(suffix: String): Unit
fun getTransformManager(): TransformManager
fun init(assets: js.array.ReadonlyArray<JsString>, onready: () -> Unit): Unit
fun loadFilamesh(urlOrBuffer: BufferReference, definstance: MaterialInstance = definedExternally, matinstances: JsAny = definedExternally): Filamesh
fun getBackend(): Backend
fun getSupportedFeatureLevel(): FeatureLevel
fun setActiveFeatureLevel(level: FeatureLevel): FeatureLevel
fun getActiveFeatureLevel(): FeatureLevel
fun setAutomaticInstancingEnabled(enabled: Boolean): Unit
fun isAutomaticInstancingEnabled(): Boolean
fun enableAccurateTranslations(): Unit
fun unprotected(): Boolean
fun hasUnrecoverableFailure(): Boolean
fun getConfig(): Engine_Config
fun getEntityManager(): EntityManager
fun isValidRenderer(renderer: Renderer): Boolean
fun isValidFence(fence: Fence): Boolean
fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean
fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean
fun isValidView(view: View): Boolean
fun isValidScene(scene: Scene): Boolean
fun isValidIndexBuffer(buffer: IndexBuffer): Boolean
fun isValidVertexBuffer(buffer: VertexBuffer): Boolean
fun isValidIndirectLight(light: IndirectLight): Boolean
fun isValidMaterial(material: Material): Boolean
fun isValidMaterialInstance(material: Material, instance: MaterialInstance): Boolean
fun isValidExpensiveMaterialInstance(instance: MaterialInstance): Boolean
fun isValidSkybox(skybox: Skybox): Boolean
fun isValidColorGrading(colorGrading: ColorGrading): Boolean
fun isValidTexture(texture: Texture): Boolean
fun isValidRenderTarget(renderTarget: RenderTarget): Boolean
fun isValidSwapChain(swapChain: SwapChain): Boolean
companion object {
fun getSteadyClockTimeNano(): Double
fun create(canvas: org.w3c.dom.HTMLCanvasElement, options: EngineCreateOptions = definedExternally): Engine
fun destroy(engine: Engine): Unit
fun getMaxStereoscopicEyes(): Double
}
}

// ── Engine ────────────────────────────────────────────────────────────────────
