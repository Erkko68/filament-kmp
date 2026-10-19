package io.github.erkko68.filament.gltfio.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
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
 * A [Engine.Backend.NOOP] engine with an ubershader [provider] and an asset [loader], per test. Assets made with
 * [load] are destroyed in [tearDown], so a failed assertion can't leave live material instances behind.
 */
open class GltfioTestFixture {
    protected lateinit var engine: Engine
    protected lateinit var provider: MaterialProvider
    protected lateinit var loader: AssetLoader
    private val assets = mutableListOf<FilamentAsset>()

    @BeforeTest
    fun setUp() {
        Filament.init()
        Gltfio.init()
        engine = Engine.create(Engine.Backend.NOOP)!!
        provider = createUbershaderProvider(engine)
        loader = AssetLoader.create(AssetConfiguration(engine, provider, engine.entityManager))
    }

    @AfterTest
    fun tearDown() {
        if (!::engine.isInitialized) return
        if (::loader.isInitialized) {
            assets.forEach(loader::destroyAsset)
            AssetLoader.destroy(loader)
        }
        if (::provider.isInitialized) provider.destroy()
        engine.flushAndWait()
        Engine.destroy(engine)
    }

    /** Parses [bytes] and, unless told not to, loads its buffers and textures. */
    protected fun load(bytes: ByteArray, resources: Boolean = true): FilamentAsset {
        val asset = checkNotNull(loader.createAsset(bytes)) { "the glTF did not parse" }.also { assets += it }
        if (resources) ResourceLoader(ResourceConfiguration(engine)).use { check(it.loadResources(asset)) }
        return asset
    }

    /** Destroys [asset] now instead of in [tearDown]. */
    protected fun destroy(asset: FilamentAsset) {
        assets -= asset
        loader.destroyAsset(asset)
    }
}
