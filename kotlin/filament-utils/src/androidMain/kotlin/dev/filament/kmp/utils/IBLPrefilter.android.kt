package dev.filament.kmp.utils

import dev.filament.kmp.Engine
import dev.filament.kmp.Texture
import com.google.android.filament.utils.IBLPrefilterContext as AndroidIBLPrefilterContext

actual class IBLPrefilterContext actual constructor(engine: Engine) {
    internal val androidHandle = AndroidIBLPrefilterContext(engine.nativeEngine)

    actual fun destroy() {
        androidHandle.destroy()
    }
}

actual class EquirectangularToCubemap actual constructor(context: IBLPrefilterContext) {
    private val helper = AndroidIBLPrefilterContext.EquirectangularToCubemap(context.androidHandle)

    actual fun destroy() {
        helper.destroy()
    }

    actual fun run(equirect: Texture): Texture {
        return Texture(helper.run(equirect.nativeTexture))
    }
}

actual class SpecularFilter actual constructor(context: IBLPrefilterContext) {
    private val helper = AndroidIBLPrefilterContext.SpecularFilter(context.androidHandle)

    actual fun destroy() {
        helper.destroy()
    }

    actual fun run(skybox: Texture): Texture {
        return Texture(helper.run(skybox.nativeTexture))
    }
}
