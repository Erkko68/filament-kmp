package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import io.github.erkko68.filament.compose.LocalAssetLoader
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene
import io.github.erkko68.filament.compose.internal.transformMatrix
import io.github.erkko68.filament.gltfio.ResourceLoader
import io.github.erkko68.filament.utils.Quaternion

/**
 * Loads and displays a glTF/glb model in the scene.
 *
 * The model is loaded once per unique [bytes] reference. Changing [position], [rotation],
 * or [scale] updates the transform without reloading the asset.
 *
 * @param bytes Raw glb/glTF binary data.
 * @param position World-space translation.
 * @param rotation World-space rotation as a quaternion.
 * @param scale Per-axis scale. Use [Scale] with equal components for uniform scale.
 */
@Composable
fun GltfModel(
    bytes: ByteArray,
    position: Position = Position(0f),
    rotation: Quaternion = Quaternion(),
    scale: Scale = Scale(1f),
) {
    val engine = LocalFilamentEngine.current
    val scene = LocalFilamentScene.current
    val assetLoader = LocalAssetLoader.current

    val asset = remember(bytes) { assetLoader.createAsset(bytes) }

    DisposableEffect(asset) {
        asset ?: return@DisposableEffect onDispose {}
        val resourceLoader = ResourceLoader(engine, true)
        resourceLoader.loadResources(asset)
        scene.addEntities(asset.getEntities())
        onDispose {
            scene.removeEntities(asset.getEntities())
            assetLoader.destroyAsset(asset)
            resourceLoader.destroy()
        }
    }

    // SideEffect runs synchronously after every successful recomposition so animated
    // transforms (changing rotation/position every frame) update without coroutine overhead.
    SideEffect {
        val a = asset ?: return@SideEffect
        val tm = engine.getTransformManager()
        val root = a.getRoot()
        if (tm.hasComponent(root)) {
            tm.setTransform(tm.getInstance(root), transformMatrix(position, rotation, scale))
        }
    }
}
