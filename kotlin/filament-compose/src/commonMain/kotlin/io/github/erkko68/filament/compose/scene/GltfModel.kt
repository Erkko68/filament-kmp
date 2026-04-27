package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.*
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.LocalAssetLoader
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene
import io.github.erkko68.filament.compose.internal.transformMatrix
import io.github.erkko68.filament.gltfio.FilamentAsset
import io.github.erkko68.filament.gltfio.ResourceLoader
import io.github.erkko68.filament.utils.Quaternion

/**
 * Scope for interacting with a loaded glTF asset via low-level Filament APIs.
 */
interface GltfModelScope {
    /** The raw Filament asset instance. */
    val asset: FilamentAsset
    /** The Filament engine, needed for transform, material, and manager operations. */
    val engine: Engine
}

private class GltfModelScopeImpl(
    override val asset: FilamentAsset,
    override val engine: Engine,
) : GltfModelScope

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
 * @param animationIndex Index of the skeletal animation to play. Null to disable.
 * @param animationTime Current time in seconds for the animation.
 * @param onCreate Called once when the asset is loaded and added to the scene. Use this for
 *   one-time setup such as swapping materials or finding entities. Cleaned up automatically
 *   when the asset is destroyed.
 * @param onUpdate Called on every recomposition. Use this for per-frame updates such as
 *   driving shader parameters or morph targets from external state.
 */
@Composable
fun GltfModel(
    bytes: ByteArray,
    position: Position = Position(0f),
    rotation: Quaternion = Quaternion(),
    scale: Scale = Scale(1f),
    animationIndex: Int? = null,
    animationTime: Float = 0f,
    onCreate: GltfModelScope.() -> Unit = {},
    onUpdate: GltfModelScope.() -> Unit = {},
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
        GltfModelScopeImpl(asset, engine).onCreate()
        onDispose {
            scene.removeEntities(asset.getEntities())
            assetLoader.destroyAsset(asset)
            resourceLoader.destroy()
        }
    }

    SideEffect {
        val a = asset ?: return@SideEffect

        val tm = engine.getTransformManager()
        val root = a.getRoot()
        if (tm.hasComponent(root)) {
            tm.setTransform(tm.getInstance(root), transformMatrix(position, rotation, scale))
        }

        if (animationIndex != null) {
            val animator = a.getInstance().getAnimator()
            if (animationIndex >= 0 && animationIndex < animator.getAnimationCount()) {
                animator.applyAnimation(animationIndex, animationTime)
                animator.updateBoneMatrices()
            }
        }

        GltfModelScopeImpl(a, engine).onUpdate()
    }
}
