package dev.filament.kmp

/**
 * Kotlin-style wrapper over Filament's Engine lifecycle.
 */
expect class Engine {
    val isValid: Boolean

    /**
     * Creates a {@link Renderer}.
     * @return a newly created {@link Renderer}
     * @exception IllegalStateException can be thrown if the {@link Renderer} couldn't be created
     */
    fun createRenderer(): Renderer

    /**
     * Destroys a {@link Renderer} and frees all its associated resources.
     * @param renderer the {@link Renderer} to destroy
     */
    fun destroyRenderer(renderer: Renderer)

    /**
     * Creates a {@link Scene}.
     * @return a newly created {@link Scene}
     * @exception IllegalStateException can be thrown if the {@link Scene} couldn't be created
     */
    fun createScene(): Scene

    /**
     * Destroys a {@link Scene} and frees all its associated resources.
     * @param scene the {@link Scene} to destroy
     */
    fun destroyScene(scene: Scene)

    /**
     * Creates a {@link View}.
     * @return a newly created {@link View}
     * @exception IllegalStateException can be thrown if the {@link View} couldn't be created
     */
    fun createView(): View

    /**
     * Destroys a {@link View} and frees all its associated resources.
     * @param view the {@link View} to destroy
     */
    fun destroyView(view: View)

    /**
     * Creates a Camera component and associates it with the given entity.
     */
    fun createCamera(@Entity entity: Int): Camera

    /**
     * Returns the Camera component associated with the given entity, or null.
     */
    fun getCameraComponent(@Entity entity: Int): Camera?

    /**
     * Destroys the Camera component associated with the given entity.
     */
    fun destroyCameraComponent(@Entity entity: Int)

    /**
     * Creates a {@link SwapChain} from the given OS native window handle.
     *
     * @param surface on Android, <b>must be</b> an instance of {@link android.view.Surface}
     *
     * @param flags configuration flags, see {@link SwapChainFlags}
     *
     * @return a newly created {@link SwapChain} object
     *
     * @exception IllegalStateException can be thrown if the SwapChain couldn't be created
     *
     * @see SwapChainFlags#CONFIG_DEFAULT
     * @see SwapChainFlags#CONFIG_TRANSPARENT
     * @see SwapChainFlags#CONFIG_READABLE
     *
     */
    fun createSwapChain(surface: Any, flags: Long = SwapChainFlags.CONFIG_DEFAULT): SwapChain

    /**
     * Creates a headless {@link SwapChain}
     *
     * @param width  width of the rendering buffer
     * @param height height of the rendering buffer
     * @param flags  configuration flags, see {@link SwapChainFlags}
     *
     * @return a newly created {@link SwapChain} object
     *
     * @exception IllegalStateException can be thrown if the SwapChain couldn't be created
     *
     * @see SwapChainFlags#CONFIG_DEFAULT
     * @see SwapChainFlags#CONFIG_TRANSPARENT
     * @see SwapChainFlags#CONFIG_READABLE
     *
     */
    fun createSwapChain(width: Int, height: Int, flags: Long = SwapChainFlags.CONFIG_DEFAULT): SwapChain

    /**
     * Destroys a {@link SwapChain} and frees all its associated resources.
     * @param swapChain the {@link SwapChain} to destroy
     */
    fun destroySwapChain(swapChain: SwapChain)

    /**
     * Destroys a {@link RenderTarget} and frees all its associated resources.
     * @param target the {@link RenderTarget} to destroy
     */
    fun destroyRenderTarget(target: RenderTarget)

    /**
     * Destroys a {@link Texture} and frees all its associated resources.
     * @param texture the {@link Texture} to destroy
     */
    fun destroyTexture(texture: Texture)

    /**
     * Destroys a Skybox and frees all its associated resources.
     */
    fun destroySkybox(skybox: Skybox)

    /**
     * Destroys an IndirectLight and frees all its associated resources.
     */
    fun destroyIndirectLight(indirectLight: IndirectLight)

    /**
     * Destroys a ColorGrading and frees all its associated resources.
     */
    fun destroyColorGrading(colorGrading: ColorGrading)

    /**
     * Destroys a {@link IndexBuffer} and frees all its associated resources.
     * @param indexBuffer the {@link IndexBuffer} to destroy
     */
    fun destroyIndexBuffer(indexBuffer: IndexBuffer)

    /**
     * Destroys a {@link VertexBuffer} and frees all its associated resources.
     * @param vertexBuffer the {@link VertexBuffer} to destroy
     */
    fun destroyVertexBuffer(vertexBuffer: VertexBuffer)

    /**
     * Destroys a Material and frees all its associated resources.
     */
    fun destroyMaterial(material: Material)

    /**
     * Destroys a MaterialInstance and frees all its associated resources.
     */
    fun destroyMaterialInstance(materialInstance: MaterialInstance)

    /**
     * @return the {@link RenderableManager} used by this {@link Engine}
     */
    fun getRenderableManager(): RenderableManager

    /**
     * @return the LightManager used by this Engine
     */
    fun getLightManager(): LightManager

    /**
     * @return the {@link TransformManager} used by this {@link Engine}
     */
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
