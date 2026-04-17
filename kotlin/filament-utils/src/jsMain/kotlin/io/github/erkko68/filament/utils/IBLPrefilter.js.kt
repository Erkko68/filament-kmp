package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture

actual class IBLPrefilterContext actual constructor(engine: Engine) {
    actual fun destroy() {
    }
}

actual class EquirectangularToCubemap actual constructor(context: IBLPrefilterContext) {
    actual fun destroy() {
    }

    actual fun run(equirect: Texture): Texture {
        TODO("Not yet implemented")
    }
}

actual class SpecularFilter actual constructor(context: IBLPrefilterContext) {
    actual fun destroy() {
    }

    actual fun run(skybox: Texture): Texture {
        TODO("Not yet implemented")
    }
}