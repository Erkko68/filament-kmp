package dev.filament.kmp

import com.google.android.filament.Material as AndroidMaterial
import java.nio.Buffer

actual class Material internal constructor(
    internal var androidMaterial: AndroidMaterial?,
) {
    actual fun createInstance(): MaterialInstance {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return MaterialInstance(material.createInstance())
    }

    actual fun createInstance(name: String): MaterialInstance {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return MaterialInstance(material.createInstance(name))
    }

    actual fun getDefaultInstance(): MaterialInstance {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return MaterialInstance(material.defaultInstance)
    }

    actual fun getName(): String {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.name
    }

    actual fun getNativeObject(): Long {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.nativeObject
    }

    actual internal fun invalidate() {
        androidMaterial = null
    }

    actual class Builder {
        private val androidBuilder = AndroidMaterial.Builder()

        actual fun payload(buffer: Any, size: Int): Builder {
            androidBuilder.payload(buffer as Buffer, size)
            return this
        }

        actual fun build(engine: Engine): Material {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return Material(androidBuilder.build(androidEngine))
        }
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

internal fun Material.CullingMode.toAndroid(): AndroidMaterial.CullingMode = when (this) {
    Material.CullingMode.NONE -> AndroidMaterial.CullingMode.NONE
    Material.CullingMode.FRONT -> AndroidMaterial.CullingMode.FRONT
    Material.CullingMode.BACK -> AndroidMaterial.CullingMode.BACK
    Material.CullingMode.FRONT_AND_BACK -> AndroidMaterial.CullingMode.FRONT_AND_BACK
}

internal fun Material.TransparencyMode.toAndroid(): AndroidMaterial.TransparencyMode = when (this) {
    Material.TransparencyMode.DEFAULT -> AndroidMaterial.TransparencyMode.DEFAULT
    Material.TransparencyMode.TWO_PASSES_ONE_SIDE -> AndroidMaterial.TransparencyMode.TWO_PASSES_ONE_SIDE
    Material.TransparencyMode.TWO_PASSES_TWO_SIDES -> AndroidMaterial.TransparencyMode.TWO_PASSES_TWO_SIDES
}

