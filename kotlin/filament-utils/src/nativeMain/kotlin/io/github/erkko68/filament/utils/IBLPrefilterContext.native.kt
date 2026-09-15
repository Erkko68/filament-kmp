@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.cinterop.*
import kotlinx.cinterop.*
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap
import io.github.erkko68.filament.nativeObject

actual class IBLPrefilterContext actual constructor(engine: Engine) : AutoCloseable {
    internal val nativeHandle = FilaIBLPrefilterContext_create(engine.nativeObject)!!

    actual override fun close() = destroy()

    actual fun destroy() {
        FilaIBLPrefilterContext_destroy(nativeHandle)
    }
}

actual class EquirectangularToCubemap actual constructor(context: IBLPrefilterContext) : AutoCloseable {
    private val nativeHandle = FilaIBLPrefilterEquirectangularToCubemap_create(context.nativeHandle)!!

    actual override fun close() = destroy()

    actual fun destroy() {
        FilaIBLPrefilterEquirectangularToCubemap_destroy(nativeHandle)
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — returns the input texture unchanged; filament.js does not expose IBLPrefilterContext.")
    actual fun run(equirect: Texture): Texture {
        val handle = FilaIBLPrefilterEquirectangularToCubemap_run(nativeHandle, equirect.nativeObject)
        return Texture(handle!!)
    }
}

actual class SpecularFilter actual constructor(context: IBLPrefilterContext) : AutoCloseable {
    private val nativeHandle = FilaIBLPrefilterSpecularFilter_create(context.nativeHandle)!!

    actual override fun close() = destroy()

    actual fun destroy() {
        FilaIBLPrefilterSpecularFilter_destroy(nativeHandle)
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — returns the input texture unchanged; filament.js does not expose IBLPrefilterContext.")
    actual fun run(skybox: Texture): Texture {
        val handle = FilaIBLPrefilterSpecularFilter_run(nativeHandle, skybox.nativeObject)
        return Texture(handle!!)
    }
}
