package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap
import io.github.erkko68.filament.nativeObject
import io.github.erkko68.filament.VertexBuffer

actual interface MaterialProvider : AutoCloseable {
    actual fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance?
    actual fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material?
    actual val materials: List<io.github.erkko68.filament.Material>
    actual fun needsDummyData(attrib: VertexBuffer.VertexAttribute): Boolean
    actual fun destroyMaterials()
    actual fun destroy()
    
    fun getNativeProvider(): com.google.android.filament.gltfio.MaterialProvider
}

@PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "createMaterialInstance/getMaterial throw — filament.js does not expose the ubershader material provider; use precompiled .filamat materials on web.")
actual class UbershaderProvider actual constructor(engine: Engine) : MaterialProvider {
    private val nativeObject = com.google.android.filament.gltfio.UbershaderProvider(engine.nativeObject)

    actual override fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance? {
        val nativeInstance = nativeObject.createMaterialInstance(config.toAndroid(), uvmap, label, extras) ?: return null
        return io.github.erkko68.filament.MaterialInstance(nativeInstance)
    }

    actual override fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material? {
        val nativeMaterial = nativeObject.getMaterial(config.toAndroid(), uvmap, label) ?: return null
        return io.github.erkko68.filament.Material(nativeMaterial)
    }

    actual override val materials: List<io.github.erkko68.filament.Material> get() {
        val natives = nativeObject.materials
        return List(natives.size) { i -> io.github.erkko68.filament.Material(natives[i]) }
    }

    actual override fun needsDummyData(attrib: VertexBuffer.VertexAttribute): Boolean = nativeObject.needsDummyData(attrib.ordinal)

    actual override fun destroyMaterials() {
        nativeObject.destroyMaterials()
    }

    actual override fun close() = destroy()

    actual override fun destroy() {
        nativeObject.destroy()
    }

    override fun getNativeProvider(): com.google.android.filament.gltfio.MaterialProvider = nativeObject
}
