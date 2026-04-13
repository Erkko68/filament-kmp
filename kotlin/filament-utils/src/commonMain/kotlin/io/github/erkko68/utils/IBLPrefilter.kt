package io.github.erkko68.utils

import io.github.erkko68.Engine
import io.github.erkko68.Texture

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
