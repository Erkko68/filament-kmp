package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap
import io.github.erkko68.filament.web.gltfio_UbershaderProvider
import io.github.erkko68.filament.web.VertexAttribute as JSVertexAttribute
import io.github.erkko68.filament.nativeObject

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

@PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "createMaterialInstance/getMaterial throw — they take a MaterialKey, which filament.js cannot bind (bitfield-packed); the rest of the provider is the real ubershader provider.")
actual class UbershaderProvider actual constructor(engine: Engine) : MaterialProvider {
    internal val jsProvider = gltfio_UbershaderProvider(engine.nativeObject)

    actual override fun createMaterialInstance(
        config: MaterialKey,
        uvmap: IntArray,
        label: String?,
        extras: String?
    ): MaterialInstance? {
        // MaterialKey is a packed bitfield, which embind cannot bind; the rest of the
        // ubershader provider is bound.
        throw UnsupportedOperationException(
            "MaterialProvider.createMaterialInstance is not supported on the JS/Web target: it " +
            "takes a MaterialKey, which filament.js does not bind. Supply your own precompiled " +
            "materials via Material.Builder().payload(...)."
        )
    }

    actual override fun getMaterial(
        config: MaterialKey,
        uvmap: IntArray,
        label: String?
    ): Material? {
        // Same MaterialKey limitation as createMaterialInstance.
        throw UnsupportedOperationException(
            "MaterialProvider.getMaterial is not supported on the JS/Web target: it takes a " +
            "MaterialKey, which filament.js does not bind. Supply your own precompiled materials " +
            "via Material.Builder().payload(...)."
        )
    }

    actual override fun getMaterials(): Array<Material> {
        val jsMaterials = jsProvider.getMaterials()
        return Array(jsMaterials.size) { Material(jsMaterials[it]!!) }
    }

    actual override fun needsDummyData(attrib: Int): Boolean {
        val js = jsVertexAttribute(attrib) ?: return false
        return jsProvider.needsDummyData(js)
    }

    // destroyMaterials() also destroys the provider's dummy texture, so a second call
    // aborts the engine on an already-destroyed handle.
    private var materialsDestroyed = false

    actual override fun destroyMaterials() {
        if (materialsDestroyed) return
        materialsDestroyed = true
        jsProvider.destroyMaterials()
    }

    // filament.js binds no destructor that frees the provider itself; this releases what it owns.
    actual override fun destroy() = destroyMaterials()
}

// filament::VertexAttribute in declaration order — the ordinal the common API passes.
// Read on call, not at module load: the JS enum objects only exist once Filament.init() has run.
private fun jsVertexAttribute(attrib: Int): JSVertexAttribute? = when (attrib) {
    0 -> JSVertexAttribute.POSITION
    1 -> JSVertexAttribute.TANGENTS
    2 -> JSVertexAttribute.COLOR
    3 -> JSVertexAttribute.UV0
    4 -> JSVertexAttribute.UV1
    5 -> JSVertexAttribute.BONE_INDICES
    6 -> JSVertexAttribute.BONE_WEIGHTS
    7 -> JSVertexAttribute.UNUSED
    8 -> JSVertexAttribute.CUSTOM0
    9 -> JSVertexAttribute.CUSTOM1
    10 -> JSVertexAttribute.CUSTOM2
    11 -> JSVertexAttribute.CUSTOM3
    12 -> JSVertexAttribute.CUSTOM4
    13 -> JSVertexAttribute.CUSTOM5
    14 -> JSVertexAttribute.CUSTOM6
    15 -> JSVertexAttribute.CUSTOM7
    else -> null
}
