@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.gltfio

import kotlinx.cinterop.*
import io.github.erkko68.filament.*
import io.github.erkko68.filament.cinterop.*
import io.github.erkko68.filament.gltfio.cinterop.*
import cnames.structs.FilaMaterialProvider

actual interface MaterialProvider {
    actual fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance?
    actual fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material?
    actual fun getMaterials(): Array<io.github.erkko68.filament.Material>
    actual fun needsDummyData(attrib: Int): Boolean
    actual fun destroyMaterials()
    actual fun destroy()
    
    fun getNativeHandle(): CPointer<FilaMaterialProvider>?
}

actual class UbershaderProvider actual constructor(engine: Engine) : MaterialProvider {
    public var nativeHandle: CPointer<FilaMaterialProvider>? = FilaMaterialProvider_createUbershaderProvider(engine.nativeHandle, null, 0u)

    actual override fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance? {
        return memScoped {
            val nativeKey = alloc<FilaMaterialKey>()
            val fields = alloc<FilaMaterialKeyFields>()
            config.toNative(nativeKey, fields)
            val byteUvMap = ByteArray(8) { uvmap.getOrElse(it) { 0 }.toByte() }
            byteUvMap.usePinned { pinned ->
                val handle = FilaMaterialProvider_createMaterialInstance(
                    nativeHandle, nativeKey.ptr, pinned.addressOf(0).reinterpret<UByteVar>(), label, extras
                )
                handle?.let { io.github.erkko68.filament.MaterialInstance(it) }
            }
        }
    }

    actual override fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material? {
        return memScoped {
            val nativeKey = alloc<FilaMaterialKey>()
            val fields = alloc<FilaMaterialKeyFields>()
            config.toNative(nativeKey, fields)
            val byteUvMap = ByteArray(8) { uvmap.getOrElse(it) { 0 }.toByte() }
            byteUvMap.usePinned { pinned ->
                val handle = FilaMaterialProvider_getMaterial(
                    nativeHandle, nativeKey.ptr, pinned.addressOf(0).reinterpret<UByteVar>(), label
                )
                handle?.let { io.github.erkko68.filament.Material(it) }
            }
        }
    }

    actual override fun getMaterials(): Array<io.github.erkko68.filament.Material> {
        val count = FilaMaterialProvider_getMaterialsCount(nativeHandle).toInt()
        if (count == 0) return emptyArray()
        memScoped {
            val materials = allocArray<CPointerVar<cnames.structs.FilaMaterial>>(count)
            FilaMaterialProvider_getMaterials(nativeHandle, materials)
            return Array(count) { io.github.erkko68.filament.Material(materials[it]) }
        }
    }

    actual override fun needsDummyData(attrib: Int): Boolean {
        return FilaMaterialProvider_needsDummyData(nativeHandle, attrib)
    }

    actual override fun destroyMaterials() {
        FilaMaterialProvider_destroyMaterials(nativeHandle)
    }

    actual override fun destroy() {
        FilaMaterialProvider_destroy(nativeHandle)
        nativeHandle = null
    }

    override fun getNativeHandle(): CPointer<FilaMaterialProvider>? = nativeHandle
}

actual class JitShaderProvider actual constructor(engine: Engine) : MaterialProvider {
    public var nativeHandle: CPointer<FilaMaterialProvider>? = FilaMaterialProvider_createJitShaderProvider(engine.nativeHandle)

    actual override fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance? {
        return memScoped {
            val nativeKey = alloc<FilaMaterialKey>()
            val fields = alloc<FilaMaterialKeyFields>()
            config.toNative(nativeKey, fields)
            uvmap.usePinned { pinned ->
                val handle = FilaMaterialProvider_createMaterialInstance(
                    nativeHandle, nativeKey.ptr, pinned.addressOf(0).reinterpret(), label, extras
                )
                handle?.let { io.github.erkko68.filament.MaterialInstance(it) }
            }
        }
    }

    actual override fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material? {
        return memScoped {
            val nativeKey = alloc<FilaMaterialKey>()
            val fields = alloc<FilaMaterialKeyFields>()
            config.toNative(nativeKey, fields)
            uvmap.usePinned { pinned ->
                val handle = FilaMaterialProvider_getMaterial(
                    nativeHandle, nativeKey.ptr, pinned.addressOf(0).reinterpret(), label
                )
                handle?.let { io.github.erkko68.filament.Material(it) }
            }
        }
    }

    actual override fun getMaterials(): Array<io.github.erkko68.filament.Material> {
        val count = FilaMaterialProvider_getMaterialsCount(nativeHandle).toInt()
        if (count == 0) return emptyArray()
        memScoped {
            val materials = allocArray<CPointerVar<cnames.structs.FilaMaterial>>(count)
            FilaMaterialProvider_getMaterials(nativeHandle, materials)
            return Array(count) { io.github.erkko68.filament.Material(materials[it]) }
        }
    }

    actual override fun needsDummyData(attrib: Int): Boolean {
        return FilaMaterialProvider_needsDummyData(nativeHandle, attrib)
    }

    actual override fun destroyMaterials() {
        FilaMaterialProvider_destroyMaterials(nativeHandle)
    }

    actual override fun destroy() {
        FilaMaterialProvider_destroy(nativeHandle)
        nativeHandle = null
    }

    override fun getNativeHandle(): CPointer<FilaMaterialProvider>? = nativeHandle
}
