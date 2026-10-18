package io.github.erkko68.filament.compose.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.Scene
import io.github.erkko68.filament.compose.scene.GltfAsset
import io.github.erkko68.filament.compose.scene.StandardMaterial
import io.github.erkko68.filament.gltfio.AssetConfiguration
import io.github.erkko68.filament.gltfio.AssetLoader
import io.github.erkko68.filament.gltfio.FilamentAsset
import io.github.erkko68.filament.gltfio.Gltfio
import io.github.erkko68.filament.gltfio.MaterialProvider
import io.github.erkko68.filament.gltfio.ResourceConfiguration
import io.github.erkko68.filament.gltfio.ResourceLoader
import io.github.erkko68.filament.gltfio.createUbershaderProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Base fixture for compose tests: a [Engine.Backend.NOOP] engine and a [Scene], created and destroyed per test.
 * NOOP runs everything but the rasterizer on every target, so materials, geometry, glTF and environments all work.
 * Materials and glTF assets made through the helpers are destroyed in [tearDown].
 */
open class ComposeTestFixture {
    protected lateinit var engine: Engine
    protected lateinit var scene: Scene

    private val materials = mutableListOf<Material>()
    private val materialInstances = mutableListOf<MaterialInstance>()
    private val gltfAssets = mutableListOf<FilamentAsset>()
    private val gltfLoaders = mutableListOf<AssetLoader>()
    private val gltfProviders = mutableListOf<MaterialProvider>()

    /** On JS, awaits skiko's async WASM load before `runComposeUiTest` builds its Skia surface; see [GraphicsReady]. */
    @BeforeTest
    fun awaitGraphics(): GraphicsReady = awaitGraphicsReady()

    @BeforeTest
    fun setUp() {
        Filament.init()
        engine = Engine.create(Engine.Backend.NOOP)!!
        scene = engine.createScene()
    }

    @AfterTest
    fun tearDown() {
        if (!::engine.isInitialized) return
        // glTF first: assets belong to their loader, and the loader's materials to the provider.
        gltfAssets.forEachIndexed { i, a -> gltfLoaders[i].destroyAsset(a) }
        gltfLoaders.forEach { AssetLoader.destroy(it) }
        gltfProviders.forEach { it.destroy() }
        materialInstances.forEach { engine.destroy(it) }
        materials.forEach { engine.destroy(it) }
        if (::scene.isInitialized) engine.destroy(scene)
        engine.flushAndWait()
        Engine.destroy(engine)
    }

    /**
     * Loads a glb into a ready [GltfAsset] synchronously on the test thread. Not `rememberGltfAsset`: its effects
     * run on the UI dispatcher, and gltfio's `ResourceLoader` is thread-affine (see [withUiThreadFilamentScene]).
     */
    protected fun gltfAsset(bytes: ByteArray): GltfAsset {
        Gltfio.init()
        val provider = createUbershaderProvider(engine).also { gltfProviders += it }
        val loader = AssetLoader.create(AssetConfiguration(engine, provider, engine.entityManager)).also { gltfLoaders += it }
        val filamentAsset = checkNotNull(loader.createAsset(bytes)).also { gltfAssets += it }
        val resourceLoader = ResourceLoader(ResourceConfiguration(engine, true))
        try {
            resourceLoader.asyncBeginLoad(filamentAsset)
            var guard = 0 // an embedded glb settles in a few steps; this only stops a stuck load hanging the suite
            while (resourceLoader.asyncGetLoadProgress() < 1.0f && guard++ < 1000) resourceLoader.asyncUpdateLoad()
        } finally {
            resourceLoader.destroy()
        }
        return GltfAsset(filamentAsset, loader).apply { isReady = true }
    }

    /** A [MaterialInstance] of the built-in emissive material, outliving any composable that uses it. */
    protected fun materialInstance(): MaterialInstance {
        val material = StandardMaterial.Emissive.build(engine).also { materials += it }
        return material.createInstance().also { materialInstances += it }
    }
}
