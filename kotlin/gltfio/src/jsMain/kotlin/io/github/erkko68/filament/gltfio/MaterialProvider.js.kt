package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance

actual interface MaterialProvider {
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

    actual fun getMaterials(): Array<Material>
    actual fun needsDummyData(attrib: Int): Boolean
    actual fun destroyMaterials()
    actual fun destroy()
}

actual class UbershaderProvider actual constructor(engine: Engine) : MaterialProvider {
    private val materials = mutableListOf<Material>()

    actual override fun createMaterialInstance(
        config: MaterialKey,
        uvmap: IntArray,
        label: String?,
        extras: String?
    ): MaterialInstance? {
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
        throw UnsupportedOperationException(
            "MaterialProvider.getMaterial is not supported on the JS/Web target. The default " +
            "ubershader path is not exposed by Filament.js. Supply your own precompiled materials " +
            "via Material.Builder().payload(...)."
        )
    }

    actual override fun getMaterials(): Array<Material> {
        return materials.toTypedArray()
    }

    actual override fun needsDummyData(attrib: Int): Boolean {
        return false
    }

    actual override fun destroyMaterials() {
        materials.clear()
    }

    actual override fun destroy() {
        materials.clear()
    }
}