package dev.filament.kmp

actual class Engine {
    actual val isValid: Boolean
        get() = TODO("Not yet implemented")

    actual fun createRenderer(): Renderer {
        TODO("Not yet implemented")
    }

    actual fun destroyRenderer(renderer: Renderer) {
    }

    actual fun createScene(): Scene {
        TODO("Not yet implemented")
    }

    actual fun destroyScene(scene: Scene) {
    }

    actual fun createView(): View {
        TODO("Not yet implemented")
    }

    actual fun destroyView(view: View) {
    }

    actual fun createSwapChain(surface: Any, flags: Long): SwapChain {
        TODO("Not yet implemented")
    }

    actual fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain {
        TODO("Not yet implemented")
    }

    actual fun destroySwapChain(swapChain: SwapChain) {
    }

    actual fun destroyRenderTarget(target: RenderTarget) {
    }

    actual fun destroyTexture(texture: Texture) {
    }

    actual fun destroyBufferObject(bufferObject: BufferObject) {
    }

    actual fun destroyIndexBuffer(indexBuffer: IndexBuffer) {
    }

    actual fun destroyVertexBuffer(vertexBuffer: VertexBuffer) {
    }

    actual fun destroyMaterial(material: Material) {
    }

    actual fun destroyMaterialInstance(materialInstance: MaterialInstance) {
    }

    actual fun getRenderableManager(): RenderableManager {
        TODO("Not yet implemented")
    }

    actual fun getTransformManager(): TransformManager {
        TODO("Not yet implemented")
    }

    actual fun close() {
    }

    actual companion object {
        actual fun create(config: EngineConfig): Engine {
            TODO("Not yet implemented")
        }
    }
}