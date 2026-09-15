package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.ffm.FilamentC
import java.lang.foreign.MemorySegment
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap
import io.github.erkko68.filament.nativeObject

actual class IBLPrefilterContext actual constructor(engine: Engine) : AutoCloseable {
    internal val nativeHandle: MemorySegment = FilamentC.FilaIBLPrefilterContext_create(engine.nativeObject)
    actual override fun close() = destroy()

    actual fun destroy() = FilamentC.FilaIBLPrefilterContext_destroy(nativeHandle)
}

actual class EquirectangularToCubemap actual constructor(context: IBLPrefilterContext) : AutoCloseable {
    private val nativeHandle: MemorySegment = FilamentC.FilaIBLPrefilterEquirectangularToCubemap_create(context.nativeHandle)
    actual override fun close() = destroy()

    actual fun destroy() = FilamentC.FilaIBLPrefilterEquirectangularToCubemap_destroy(nativeHandle)
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — returns the input texture unchanged; filament.js does not expose IBLPrefilterContext.")
    actual fun run(equirect: Texture): Texture =
        Texture(FilamentC.FilaIBLPrefilterEquirectangularToCubemap_run(nativeHandle, equirect.nativeObject))
}

actual class SpecularFilter actual constructor(context: IBLPrefilterContext) : AutoCloseable {
    private val nativeHandle: MemorySegment = FilamentC.FilaIBLPrefilterSpecularFilter_create(context.nativeHandle)
    actual override fun close() = destroy()

    actual fun destroy() = FilamentC.FilaIBLPrefilterSpecularFilter_destroy(nativeHandle)
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — returns the input texture unchanged; filament.js does not expose IBLPrefilterContext.")
    actual fun run(skybox: Texture): Texture =
        Texture(FilamentC.FilaIBLPrefilterSpecularFilter_run(nativeHandle, skybox.nativeObject))
}
