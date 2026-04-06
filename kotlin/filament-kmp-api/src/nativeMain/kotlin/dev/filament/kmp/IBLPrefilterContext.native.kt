package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class IBLPrefilterContext actual constructor(engine: Engine) {
    internal val nativeObject = FilaIBLPrefilterContext_create(engine.nativeObject)!!

    actual fun destroy() {
        FilaIBLPrefilterContext_destroy(nativeObject)
    }

    actual class EquirectangularToCubemap actual constructor(context: IBLPrefilterContext) {
        internal val nativeObject = FilaIBLPrefilterEquirectangularToCubemap_create(context.nativeObject)!!

        actual fun run(equirect: Texture): Texture {
            val nativeTexture = FilaIBLPrefilterEquirectangularToCubemap_run(nativeObject, equirect.nativeObject)!!
            return Texture(nativeTexture)
        }

        actual fun destroy() {
            FilaIBLPrefilterEquirectangularToCubemap_destroy(nativeObject)
        }
    }

    actual class SpecularFilter actual constructor(context: IBLPrefilterContext) {
        internal val nativeObject = FilaIBLPrefilterSpecularFilter_create(context.nativeObject)!!

        actual fun run(skybox: Texture): Texture {
            val nativeTexture = FilaIBLPrefilterSpecularFilter_run(nativeObject, skybox.nativeObject)!!
            return Texture(nativeTexture)
        }

        actual fun destroy() {
            FilaIBLPrefilterSpecularFilter_destroy(nativeObject)
        }
    }
}
