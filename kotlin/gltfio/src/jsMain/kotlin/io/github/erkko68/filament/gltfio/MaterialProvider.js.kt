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

actual class UbershaderProvider : MaterialProvider {
    actual override fun createMaterialInstance(
        config: MaterialKey,
        uvmap: IntArray,
        label: String?,
        extras: String?
    ): MaterialInstance? {
        TODO("Not yet implemented")
    }

    actual override fun getMaterial(
        config: MaterialKey,
        uvmap: IntArray,
        label: String?
    ): Material? {
        TODO("Not yet implemented")
    }

    actual override fun getMaterials(): Array<Material> {
        TODO("Not yet implemented")
    }

    actual override fun needsDummyData(attrib: Int): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun destroyMaterials() {
    }

    actual override fun destroy() {
    }

    actual constructor(engine: Engine) {
        TODO("Not yet implemented")
    }
}