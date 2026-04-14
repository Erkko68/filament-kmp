package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine

expect interface MaterialProvider {
    fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String? = null, extras: String? = null): io.github.erkko68.filament.MaterialInstance?
    fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String? = null): io.github.erkko68.filament.Material?
    fun getMaterials(): Array<io.github.erkko68.filament.Material>
    fun needsDummyData(attrib: Int): Boolean
    fun destroyMaterials()
    fun destroy()
}

expect class UbershaderProvider : MaterialProvider {
    constructor(engine: Engine)

    override fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance?
    override fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material?
    override fun getMaterials(): Array<io.github.erkko68.filament.Material>
    override fun needsDummyData(attrib: Int): Boolean
    override fun destroyMaterials()
    override fun destroy()
}

expect class JitShaderProvider : MaterialProvider {
    constructor(engine: Engine)

    override fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance?
    override fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material?
    override fun getMaterials(): Array<io.github.erkko68.filament.Material>
    override fun needsDummyData(attrib: Int): Boolean
    override fun destroyMaterials()
    override fun destroy()
}
