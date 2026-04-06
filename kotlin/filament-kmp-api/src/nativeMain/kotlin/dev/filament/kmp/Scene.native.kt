@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaScene

actual class Scene internal constructor(internal var nativeHandle: CPointer<FilaScene>?) {
    private var skybox: Skybox? = null
    private var indirectLight: IndirectLight? = null

    actual fun setSkybox(skybox: Skybox?) {
        this.skybox = skybox
        FilaScene_setSkybox(nativeHandle, skybox?.nativeHandle)
    }
    actual fun getSkybox(): Skybox? = skybox

    actual fun setIndirectLight(ibl: IndirectLight?) {
        this.indirectLight = ibl
        FilaScene_setIndirectLight(nativeHandle, ibl?.nativeHandle)
    }
    actual fun getIndirectLight(): IndirectLight? = indirectLight

    actual fun addEntity(entity: Int) = FilaScene_addEntity(nativeHandle, entity.toUInt())
    actual fun removeEntity(entity: Int) = FilaScene_remove(nativeHandle, entity.toUInt())
    actual fun getEntityCount(): Int = FilaScene_getEntityCount(nativeHandle).toInt()
    actual fun getRenderableCount(): Int = FilaScene_getRenderableCount(nativeHandle).toInt()
    actual fun getLightCount(): Int = FilaScene_getLightCount(nativeHandle).toInt()
    actual fun hasEntity(entity: Int): Boolean = FilaScene_hasEntity(nativeHandle, entity.toUInt())
}
