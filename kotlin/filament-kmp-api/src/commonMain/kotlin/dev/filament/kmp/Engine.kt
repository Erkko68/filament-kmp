package dev.filament.kmp

/**
 * Kotlin-style wrapper over Filament's Engine lifecycle.
 */
expect class Engine {
    val isValid: Boolean

    fun createRenderer(): Renderer

    fun destroyRenderer(renderer: Renderer)

    fun createScene(): Scene

    fun destroyScene(scene: Scene)

    fun createView(): View

    fun destroyView(view: View)

    fun createSwapChain(surface: Any, flags: Long = SwapChainFlags.CONFIG_DEFAULT): SwapChain

    fun createSwapChain(width: Int, height: Int, flags: Long = SwapChainFlags.CONFIG_DEFAULT): SwapChain

    fun destroySwapChain(swapChain: SwapChain)

    fun destroyRenderTarget(target: RenderTarget)

    fun destroyTexture(texture: Texture)

    fun destroyBufferObject(bufferObject: BufferObject)

    fun destroyIndexBuffer(indexBuffer: IndexBuffer)

    fun destroyVertexBuffer(vertexBuffer: VertexBuffer)

    fun destroyMaterial(material: Material)

    fun destroyMaterialInstance(materialInstance: MaterialInstance)

    fun getRenderableManager(): RenderableManager

    fun getTransformManager(): TransformManager

    fun close()

    companion object {
        fun create(config: EngineConfig = EngineConfig()): Engine
    }
}

enum class EngineStereoscopicType {
    NONE,
    INSTANCED,
    MULTIVIEW,
}

data class EngineConfig(
    val stereoscopicType: EngineStereoscopicType = EngineStereoscopicType.NONE,
    val stereoscopicEyeCount: UByte = 1u,
)

