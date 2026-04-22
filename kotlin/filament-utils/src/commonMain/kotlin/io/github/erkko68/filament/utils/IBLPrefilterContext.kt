package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture

expect class IBLPrefilterContext(engine: Engine) {
    fun destroy()
}

expect class EquirectangularToCubemap(context: IBLPrefilterContext) {
    fun destroy()
    fun run(equirect: Texture): Texture
}

expect class SpecularFilter(context: IBLPrefilterContext) {
    fun destroy()
    fun run(skybox: Texture): Texture
}
