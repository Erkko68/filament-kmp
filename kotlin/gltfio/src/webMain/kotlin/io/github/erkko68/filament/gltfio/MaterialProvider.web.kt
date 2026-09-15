package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap
import io.github.erkko68.filament.web.gltfio_UbershaderProvider
import io.github.erkko68.filament.VertexBuffer
import io.github.erkko68.filament.nativeObject

actual interface MaterialProvider : AutoCloseable {
    actual fun createMaterialInstance(
        config: MaterialKey,
        uvmap: IntArray,
        label: String?,
        extras: String?
    ): MaterialInstance?

    actual fun getMaterial(
        config: MaterialKey,
        uvmap: IntArray,
        label: String?
    ): Material?

    actual val materials: List<Material>
    actual fun needsDummyData(attrib: VertexBuffer.VertexAttribute): Boolean
    actual fun destroyMaterials()
    actual fun destroy()
}

@PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "createMaterialInstance/getMaterial throw — filament.js does not expose the ubershader material provider; use precompiled .filamat materials on web.")
actual class UbershaderProvider actual constructor(engine: Engine) : MaterialProvider {
    private val jsProvider = gltfio_UbershaderProvider(engine.nativeObject)
    // A second destroyMaterials() on the same provider aborts in the wasm heap.
    private var materialsDestroyed = false

    actual override fun createMaterialInstance(
        config: MaterialKey,
        uvmap: IntArray,
        label: String?,
        extras: String?
    ): MaterialInstance? {
        // TODO(js): default ubershader path not exposed by Filament.js.
        throw UnsupportedOperationException(
            "MaterialProvider.createMaterialInstance is not supported on the JS/Web target. The " +
            "default ubershader path is not exposed by Filament.js. Supply your own precompiled " +
            "materials via Material.Builder().payload(...)."
        )
    }

    actual override fun getMaterial(
        config: MaterialKey,
        uvmap: IntArray,
        label: String?
    ): Material? {
        // TODO(js): default ubershader path not exposed by Filament.js.
        throw UnsupportedOperationException(
            "MaterialProvider.getMaterial is not supported on the JS/Web target. The default " +
            "ubershader path is not exposed by Filament.js. Supply your own precompiled materials " +
            "via Material.Builder().payload(...)."
        )
    }

    // gltfio$UbershaderProvider binds only destroyMaterials(), so there is no material
    // cache to report and no dummy-data query to forward.
    actual override val materials: List<Material> get() = emptyList()

    actual override fun needsDummyData(attrib: VertexBuffer.VertexAttribute): Boolean = false

    actual override fun destroyMaterials() {
        if (!materialsDestroyed) {
            jsProvider.destroyMaterials()
            materialsDestroyed = true
        }
    }

    actual override fun close() = destroy()
    actual override fun destroy() = destroyMaterials()
}
