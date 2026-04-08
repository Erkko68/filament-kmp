package dev.filament.kmp.utils

import dev.filament.kmp.Engine
import dev.filament.kmp.Texture

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
