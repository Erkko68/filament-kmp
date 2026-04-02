package dev.filament.kmp

actual class Material {
    actual fun createInstance(): MaterialInstance = TODO("Not yet implemented")

    actual fun createInstance(name: String): MaterialInstance = TODO("Not yet implemented")

    actual fun getDefaultInstance(): MaterialInstance = TODO("Not yet implemented")

    actual fun getName(): String = TODO("Not yet implemented")

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() {
    }

    actual class Builder {
        actual fun payload(buffer: Any, size: Int): Builder = TODO("Not yet implemented")

        actual fun build(engine: Engine): Material = TODO("Not yet implemented")
    }

    actual enum class CullingMode {
        NONE,
        FRONT,
        BACK,
        FRONT_AND_BACK,
    }

    actual enum class TransparencyMode {
        DEFAULT,
        TWO_PASSES_ONE_SIDE,
        TWO_PASSES_TWO_SIDES,
    }
}

