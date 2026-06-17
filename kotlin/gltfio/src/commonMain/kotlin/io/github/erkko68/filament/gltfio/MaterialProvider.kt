package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine

/**
 * MaterialProvider supplies materials to glTF assets during loading.
 *
 * Implementations determine how glTF materials are rendered:
 * - UbershaderProvider: Uses pre-compiled ubershader materials (recommended)
 * - Custom providers: Can implement custom material mapping strategies
 *
 * @see UbershaderProvider
 * @see AssetLoader
 */
expect interface MaterialProvider {
    fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String? = null, extras: String? = null): io.github.erkko68.filament.MaterialInstance?
    fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String? = null): io.github.erkko68.filament.Material?
    fun getMaterials(): Array<io.github.erkko68.filament.Material>
    fun needsDummyData(attrib: Int): Boolean
    fun destroyMaterials()
    fun destroy()
}

/**
 * UbershaderProvider uses pre-compiled ubershader materials.
 *
 * This is the recommended MaterialProvider for most use cases. It uses a small set of
 * pre-compiled, flexible materials that cover most glTF 2.0 features, avoiding the overhead
 * of JIT compilation while maintaining broad compatibility.
 *
 * @see MaterialProvider
 */
expect class UbershaderProvider : MaterialProvider {
    /**
     * Create an UbershaderProvider.
     *
     * @param engine Filament Engine to use for material creation.
     */
    constructor(engine: Engine)

    override fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance?
    override fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material?
    override fun getMaterials(): Array<io.github.erkko68.filament.Material>
    override fun needsDummyData(attrib: Int): Boolean
    override fun destroyMaterials()
    override fun destroy()
}
