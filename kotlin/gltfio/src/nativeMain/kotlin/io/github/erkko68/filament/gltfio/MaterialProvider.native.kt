@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.gltfio

import kotlinx.cinterop.*
import io.github.erkko68.filament.*
import io.github.erkko68.filament.cinterop.*
import io.github.erkko68.filament.gltfio.cinterop.*
import cnames.structs.FilaMaterialProvider
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.nativeObject

actual interface MaterialProvider : AutoCloseable {
    actual fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance?
    actual fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material?
    actual val materials: List<io.github.erkko68.filament.Material>
    actual fun needsDummyData(attrib: Int): Boolean
    actual fun destroyMaterials()
    actual fun destroy()
    
    @InternalFilamentApi fun nativeObject(): CPointer<FilaMaterialProvider>?
}

@PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "createMaterialInstance/getMaterial throw — filament.js does not expose the ubershader material provider; use precompiled .filamat materials on web.")
actual class UbershaderProvider actual constructor(engine: Engine) : MaterialProvider {
    public var nativeHandle: CPointer<FilaMaterialProvider>? = FilaMaterialProvider_createUbershaderProvider(engine.nativeObject, null, 0u)

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

    actual override val materials: List<io.github.erkko68.filament.Material> get() {
        val count = FilaMaterialProvider_getMaterialsCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        memScoped {
            val materials = allocArray<CPointerVar<cnames.structs.FilaMaterial>>(count)
            FilaMaterialProvider_getMaterials(nativeHandle, materials)
            return List(count) { io.github.erkko68.filament.Material(materials[it]) }
        }
    }

    actual override fun needsDummyData(attrib: Int): Boolean {
        return FilaMaterialProvider_needsDummyData(nativeHandle, attrib)
    }

    actual override fun destroyMaterials() {
        FilaMaterialProvider_destroyMaterials(nativeHandle)
    }

    actual override fun close() = destroy()

    actual override fun destroy() {
        FilaMaterialProvider_destroy(nativeHandle)
        nativeHandle = null
    }

    @InternalFilamentApi override fun nativeObject(): CPointer<FilaMaterialProvider>? = nativeHandle
}
