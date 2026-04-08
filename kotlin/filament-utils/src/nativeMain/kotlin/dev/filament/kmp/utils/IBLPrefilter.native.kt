@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp.utils

import dev.filament.kmp.Engine
import dev.filament.kmp.Texture
import dev.filament.kmp.utils.cinterop.*
import kotlinx.cinterop.*

actual class IBLPrefilterContext actual constructor(engine: Engine) {
    internal val nativeHandle = FilaIBLPrefilterContext_create(engine.nativeHandle)!!

    actual fun destroy() {
        FilaIBLPrefilterContext_destroy(nativeHandle)
    }
}

actual class EquirectangularToCubemap actual constructor(context: IBLPrefilterContext) {
    private val nativeHandle = FilaIBLPrefilterEquirectangularToCubemap_create(context.nativeHandle)!!

    actual fun destroy() {
        FilaIBLPrefilterEquirectangularToCubemap_destroy(nativeHandle)
    }

    actual fun run(equirect: Texture): Texture {
        val handle = FilaIBLPrefilterEquirectangularToCubemap_run(nativeHandle, equirect.nativeHandle)
        return Texture(handle!!)
    }
}

actual class SpecularFilter actual constructor(context: IBLPrefilterContext) {
    private val nativeHandle = FilaIBLPrefilterSpecularFilter_create(context.nativeHandle)!!

    actual fun destroy() {
        FilaIBLPrefilterSpecularFilter_destroy(nativeHandle)
    }

    actual fun run(skybox: Texture): Texture {
        val handle = FilaIBLPrefilterSpecularFilter_run(nativeHandle, skybox.nativeHandle)
        return Texture(handle!!)
    }
}
