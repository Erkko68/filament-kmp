package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.jni.IBLPrefilterContext as JniIBLPrefilterContext

actual class IBLPrefilterContext actual constructor(engine: Engine) {
    val jni = JniIBLPrefilterContext(engine.nativeEngine)
    actual fun destroy() = jni.destroy()
}

actual class EquirectangularToCubemap actual constructor(context: IBLPrefilterContext) {
    private val jni = JniIBLPrefilterContext.EquirectangularToCubemap(context.jni)
    actual fun destroy() = jni.destroy()
    actual fun run(equirect: Texture): Texture = Texture(jni.run(equirect.nativeTexture))
}

actual class SpecularFilter actual constructor(context: IBLPrefilterContext) {
    private val jni = JniIBLPrefilterContext.SpecularFilter(context.jni)
    actual fun destroy() = jni.destroy()
    actual fun run(skybox: Texture): Texture = Texture(jni.run(skybox.nativeTexture))
}
