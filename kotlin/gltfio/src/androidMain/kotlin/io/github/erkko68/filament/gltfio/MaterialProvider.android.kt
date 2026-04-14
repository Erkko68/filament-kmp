package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine

actual interface MaterialProvider {
    actual fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance?
    actual fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material?
    actual fun getMaterials(): Array<io.github.erkko68.filament.Material>
    actual fun needsDummyData(attrib: Int): Boolean
    actual fun destroyMaterials()
    actual fun destroy()
    
    fun getNativeProvider(): com.google.android.filament.gltfio.MaterialProvider
}

actual class UbershaderProvider actual constructor(engine: Engine) : MaterialProvider {
    private val nativeObject = com.google.android.filament.gltfio.UbershaderProvider(engine.nativeEngine)

    actual override fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance? {
        val nativeInstance = nativeObject.createMaterialInstance(config.nativeObject, uvmap, label, extras) ?: return null
        return io.github.erkko68.filament.MaterialInstance(nativeInstance)
    }

    actual override fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material? {
        val nativeMaterial = nativeObject.getMaterial(config.nativeObject, uvmap, label) ?: return null
        return io.github.erkko68.filament.Material(nativeMaterial)
    }

    actual override fun getMaterials(): Array<io.github.erkko68.filament.Material> {
        val natives = nativeObject.materials
        return Array(natives.size) { i -> io.github.erkko68.filament.Material(natives[i]) }
    }

    actual override fun needsDummyData(attrib: Int): Boolean = nativeObject.needsDummyData(attrib)

    actual override fun destroyMaterials() {
        nativeObject.destroyMaterials()
    }

    actual override fun destroy() {
        nativeObject.destroy()
    }

    override fun getNativeProvider(): com.google.android.filament.gltfio.MaterialProvider = nativeObject
}

actual class JitShaderProvider actual constructor(engine: Engine) : MaterialProvider {
    private val delegate = UbershaderProvider(engine)

    actual override fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance? {
        return delegate.createMaterialInstance(config, uvmap, label, extras)
    }

    actual override fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material? {
        return delegate.getMaterial(config, uvmap, label)
    }

    actual override fun getMaterials(): Array<io.github.erkko68.filament.Material> {
        return delegate.getMaterials()
    }

    actual override fun needsDummyData(attrib: Int): Boolean = delegate.needsDummyData(attrib)

    actual override fun destroyMaterials() {
        delegate.destroyMaterials()
    }

    actual override fun destroy() {
        delegate.destroy()
    }

    override fun getNativeProvider(): com.google.android.filament.gltfio.MaterialProvider = delegate.getNativeProvider()
}
