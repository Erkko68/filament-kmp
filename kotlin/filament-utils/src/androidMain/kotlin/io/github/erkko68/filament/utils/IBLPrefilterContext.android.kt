package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import com.google.android.filament.utils.IBLPrefilterContext as AndroidIBLPrefilterContext
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap
import io.github.erkko68.filament.nativeObject

actual class IBLPrefilterContext actual constructor(engine: Engine) : AutoCloseable {
    init { com.google.android.filament.utils.Utils.init() }
    internal val androidHandle = AndroidIBLPrefilterContext(engine.nativeObject)

    actual override fun close() = destroy()

    actual fun destroy() {
        androidHandle.destroy()
    }
}

actual class EquirectangularToCubemap actual constructor(context: IBLPrefilterContext) : AutoCloseable {
    private val helper = AndroidIBLPrefilterContext.EquirectangularToCubemap(context.androidHandle)

    actual override fun close() = destroy()

    actual fun destroy() {
        helper.destroy()
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — returns the input texture unchanged; filament.js does not expose IBLPrefilterContext.")
    actual fun run(equirect: Texture): Texture {
        return Texture(helper.run(equirect.nativeObject))
    }
}

actual class SpecularFilter actual constructor(context: IBLPrefilterContext) : AutoCloseable {
    private val helper = AndroidIBLPrefilterContext.SpecularFilter(context.androidHandle)

    actual override fun close() = destroy()

    actual fun destroy() {
        helper.destroy()
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — returns the input texture unchanged; filament.js does not expose IBLPrefilterContext.")
    actual fun run(skybox: Texture): Texture {
        return Texture(helper.run(skybox.nativeObject))
    }
}
