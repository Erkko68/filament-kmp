package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.*
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene
import io.github.erkko68.filament.compose.internal.transformMatrix
import io.github.erkko68.filament.gltfio.FilamentAsset
import io.github.erkko68.filament.gltfio.FilamentInstance
import io.github.erkko68.filament.utils.Quaternion

/**
 * Scope for interacting with a specific glTF instance via low-level Filament APIs.
 */
interface GltfInstanceScope {
    /** This instance's own animator, material instances, and entity list. */
    val instance: FilamentInstance
    /** The shared asset — use for bounding box, name lookups, extras, morph target names. */
    val asset: FilamentAsset
    /** The Filament engine, needed for manager operations. */
    val engine: Engine
}

private class GltfInstanceScopeImpl(
    override val instance: FilamentInstance,
    override val asset: FilamentAsset,
    override val engine: Engine,
) : GltfInstanceScope

/**
 * Places one copy of a [GltfAsset] in the scene.
 *
 * Each call creates its own [FilamentInstance] with independent transform, animation state,
 * and material overrides. GPU resources (geometry, textures) are shared from the asset.
 *
 * Typical usage:
 * ```kotlin
 * val tree = rememberGltfAsset(treeBytes)
 * GltfInstance(tree, position = Position(0f, 0f, 0f))
 * GltfInstance(tree, position = Position(5f, 0f, 0f))
 * ```
 *
 * @param asset A loaded asset obtained from [rememberGltfAsset].
 * @param position World-space translation.
 * @param rotation World-space rotation as a quaternion.
 * @param scale Per-axis scale.
 * @param animationIndex Index of the skeletal animation to play. Null to disable.
 * @param animationTime Current time in seconds for the animation.
 * @param onCreate Called once when the instance is added to the scene. Use for one-time setup
 *   such as swapping materials or finding entities by name.
 * @param onUpdate Called on every recomposition. Use for per-frame updates such as driving
 *   shader parameters, morph targets, or reading animation state.
 */
@Composable
fun GltfInstance(
    asset: GltfAsset,
    position: Position = Position(0f),
    rotation: Quaternion = Quaternion(),
    scale: Scale = Scale(1f),
    animationIndex: Int? = null,
    animationTime: Float = 0f,
    onCreate: GltfInstanceScope.() -> Unit = {},
    onUpdate: GltfInstanceScope.() -> Unit = {},
) {
    val engine = LocalFilamentEngine.current
    val scene = LocalFilamentScene.current

    val instance = remember(asset) {
        asset.assetLoader.createInstance(asset.filamentAsset)
            ?: asset.filamentAsset.getInstance()
    }

    DisposableEffect(instance) {
        scene.addEntities(instance.getEntities())
        GltfInstanceScopeImpl(instance, asset.filamentAsset, engine).onCreate()
        onDispose {
            scene.removeEntities(instance.getEntities())
        }
    }

    SideEffect {
        val tm = engine.getTransformManager()
        val root = instance.getRoot()
        if (tm.hasComponent(root)) {
            tm.setTransform(tm.getInstance(root), transformMatrix(position, rotation, scale))
        }

        if (animationIndex != null) {
            val animator = instance.getAnimator()
            if (animationIndex >= 0 && animationIndex < animator.getAnimationCount()) {
                animator.applyAnimation(animationIndex, animationTime)
                animator.updateBoneMatrices()
            }
        }

        GltfInstanceScopeImpl(instance, asset.filamentAsset, engine).onUpdate()
    }
}
