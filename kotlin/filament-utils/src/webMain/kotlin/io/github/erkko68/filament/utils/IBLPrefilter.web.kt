package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap

actual class IBLPrefilterContext actual constructor(engine: Engine) : AutoCloseable {
    actual override fun close() = destroy()

    actual fun destroy() {
    }
}

actual class EquirectangularToCubemap actual constructor(context: IBLPrefilterContext) : AutoCloseable {
    actual override fun close() = destroy()

    actual fun destroy() {
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — returns the input texture unchanged; filament.js does not expose IBLPrefilterContext.")
    actual fun run(equirect: Texture): Texture {
        return equirect
    }
}

actual class SpecularFilter actual constructor(context: IBLPrefilterContext) : AutoCloseable {
    actual override fun close() = destroy()

    actual fun destroy() {
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — returns the input texture unchanged; filament.js does not expose IBLPrefilterContext.")
    actual fun run(skybox: Texture): Texture {
        return skybox
    }
}