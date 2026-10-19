package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import io.github.erkko68.filament.interop.NullPointer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ResourceLoaderTest : GltfioTestFixture() {
    private fun resourceLoader() = ResourceLoader(ResourceConfiguration(engine))

    @Test
    fun theUriCacheHoldsDataUntilEvicted() = resourceLoader().use { loader ->
        assertFalse(loader.hasResourceData("tri.bin"))
        loader.addResourceData("tri.bin", TestGlb.triangleBuffer)
        assertTrue(loader.hasResourceData("tri.bin"))
        loader.evictResourceData()
        assertFalse(loader.hasResourceData("tri.bin"))
    }

    /** Duck's texture decodes over several updates; its renderable is ready once it has. */
    @Test
    fun asyncLoadingProgressesToReadyRenderables() {
        val asset = load(TestGlb.getDuckGlbBytes(), resources = false)
        val before = asset.popRenderable()
        val progress = mutableListOf<Float>()
        val png = createStbProvider(engine)
        resourceLoader().use { loader ->
            loader.addTextureProvider("image/png", png)
            assertTrue(loader.asyncBeginLoad(asset))
            // Textures decode on worker threads: give them time, not a number of updates.
            val deadline = Engine.steadyClockTimeNano + 10_000_000_000L
            while (progress.lastOrNull() != 1f && Engine.steadyClockTimeNano < deadline) {
                loader.asyncUpdateLoad()
                progress += loader.asyncGetLoadProgress()
            }
        }
        png.close()
        val ready = asset.popRenderable()

        assertEquals(0, before, "a renderable waits for its textures")
        assertEquals(1f, progress.last())
        assertEquals(progress.sorted(), progress, "progress only goes up")
        assertEquals(asset.renderableEntities.single(), ready)
        assertEquals(0, asset.popRenderable(), "each renderable is popped once")
    }

    /** Cancelling mid-load leaves an asset that tears down cleanly. */
    @Test
    fun aCancelledLoadTearsDown() {
        val asset = load(TestGlb.getFoxGlbBytes(), resources = false)
        resourceLoader().use { loader ->
            assertTrue(loader.asyncBeginLoad(asset))
            loader.asyncUpdateLoad()
            loader.asyncCancelLoad()
        }
        destroy(asset)
    }

    @Test
    fun destroyingTwiceIsANoOp() {
        val loader = resourceLoader()
        loader.destroy()
        loader.destroy()
        assertEquals(NullPointer, loader.nativeObject)
    }

    @Test
    fun webpProvidersExistWhereWebpIsBuiltIn() {
        val webp = createWebpProvider(engine)
        webp?.close()
        assertEquals(isWebpSupported(), webp != null)
    }
}
