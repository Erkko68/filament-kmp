package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.utils.TextureLoader

/**
 * Loads and manages a Filament [Material] from binary data.
 *
 * The material is destroyed when it leaves the composition.
 */
@Composable
fun rememberMaterial(bytes: ByteArray): Material {
    val engine = LocalFilamentEngine.current
    val material = remember(bytes) {
        Material.Builder()
            .payload(bytes)
            .build(engine)
    }

    DisposableEffect(material) {
        onDispose {
            engine.destroyMaterial(material)
        }
    }

    return material
}

/**
 * Loads and manages a Filament [Texture] from binary data.
 *
 * Supported formats depend on the platform's [TextureLoader] implementation.
 *
 * @param bytes Raw image data (PNG, JPEG, etc. depending on platform).
 * @param type  Hints the loader about the texture's content (Color, Normal map, etc.).
 */
@Composable
fun rememberTexture(
    bytes: ByteArray,
    type: TextureLoader.TextureType = TextureLoader.TextureType.COLOR
): Texture? {
    val engine = LocalFilamentEngine.current
    val texture = remember(bytes, type) {
        TextureLoader.loadTexture(engine, bytes, type)
    }

    DisposableEffect(texture) {
        onDispose {
            texture?.let { engine.destroyTexture(it) }
        }
    }

    return texture
}

/**
 * Creates and manages a [io.github.erkko68.filament.MaterialInstance] from a [Material].
 *
 * @param material The base material to instantiate.
 */
@Composable
fun rememberMaterialInstance(material: Material): io.github.erkko68.filament.MaterialInstance {
    val engine = LocalFilamentEngine.current
    val instance = remember(material) {
        material.createInstance()
    }

    DisposableEffect(instance) {
        onDispose {
            engine.destroyMaterialInstance(instance)
        }
    }

    return instance
}
