package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.*
import io.github.erkko68.filament.jni.*
import io.github.erkko68.filament.jni.*
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.nativeObject
import io.github.erkko68.filament.VertexBuffer

actual interface MaterialProvider : AutoCloseable {
    actual fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance?
    actual fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material?
    actual val materials: List<io.github.erkko68.filament.Material>
    actual fun needsDummyData(attrib: VertexBuffer.VertexAttribute): Boolean
    actual fun destroyMaterials()
    actual fun destroy()
    
    @InternalFilamentApi fun nativeObject(): Long
}

actual class UbershaderProvider actual constructor(engine: Engine) : MaterialProvider {
    public var nativeHandle: Long = FilaMaterialProvider_createUbershaderProvider(engine.nativeObject, 0, 0)

    actual override fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance? {
        return heapScoped {
            val nativeKey = FilaMaterialKey(alloc(FilaMaterialKey.SIZE))
            val fields = FilaMaterialKeyFields(alloc(FilaMaterialKeyFields.SIZE))
            config.toNative(nativeKey, fields)
            val byteUvMap = ByteArray(8) { uvmap.getOrElse(it) { 0 }.toByte() }
            byteUvMap.usePinned { pinned ->
                val handle = FilaMaterialProvider_createMaterialInstance(
                    nativeHandle, nativeKey.ptr, pinned, label, extras
                )
                handle.takeIf { it != 0L }?.let { io.github.erkko68.filament.MaterialInstance(it) }
            }
        }
    }

    actual override fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material? {
        return heapScoped {
            val nativeKey = FilaMaterialKey(alloc(FilaMaterialKey.SIZE))
            val fields = FilaMaterialKeyFields(alloc(FilaMaterialKeyFields.SIZE))
            config.toNative(nativeKey, fields)
            val byteUvMap = ByteArray(8) { uvmap.getOrElse(it) { 0 }.toByte() }
            byteUvMap.usePinned { pinned ->
                val handle = FilaMaterialProvider_getMaterial(
                    nativeHandle, nativeKey.ptr, pinned, label
                )
                handle.takeIf { it != 0L }?.let { io.github.erkko68.filament.Material(it) }
            }
        }
    }

    actual override val materials: List<io.github.erkko68.filament.Material> get() {
        val count = FilaMaterialProvider_getMaterialsCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        heapScoped {
            val materials = PtrArray(alloc(count * PtrArray.SIZE))
            FilaMaterialProvider_getMaterials(nativeHandle, materials.ptr)
            return List(count) { io.github.erkko68.filament.Material(materials[it]) }
        }
    }

    actual override fun needsDummyData(attrib: VertexBuffer.VertexAttribute): Boolean {
        return FilaMaterialProvider_needsDummyData(nativeHandle, attrib.ordinal)
    }

    actual override fun destroyMaterials() {
        FilaMaterialProvider_destroyMaterials(nativeHandle)
    }

    actual override fun close() = destroy()

    actual override fun destroy() {
        FilaMaterialProvider_destroy(nativeHandle)
        nativeHandle = 0
    }

    @InternalFilamentApi override fun nativeObject(): Long = nativeHandle
}
