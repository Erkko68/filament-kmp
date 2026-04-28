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
 * Accepts a nullable [asset] so it composes naturally with [rememberGltfAsset]'s suspend-lambda
 * overload, which returns null while loading. When [asset] is null this composable is a no-op.
 *
 * Typical usage:
 * ```kotlin
 * // single static model — loading handled internally
 * GltfInstance(
 *     asset = rememberGltfAsset { Res.readBytes("files/models/Duck.glb") },
 *     position = Position(0f, 0f, 0f),
 * )
 *
 * // multiple copies from a shared asset
 * val tree = rememberGltfAsset { Res.readBytes("files/models/Tree.glb") }
 * GltfInstance(tree, position = Position(0f, 0f, 0f))
 * GltfInstance(tree, position = Position(5f, 0f, 0f))
 * ```
 * ### Lifecycle Note
 * Filament's `gltfio` does not have a `destroyInstance` API; instances are automatically
 * destroyed when the parent [GltfAsset] is destroyed. To avoid memory pressure, avoid
 * creating hundreds of dynamic instances from a single long-lived asset.
 *
 * @param asset A loaded asset from [rememberGltfAsset], or null while still loading.
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
    asset: GltfAsset?,
    position: Position = Position(0f),
    rotation: Quaternion = Quaternion(),
    scale: Scale = Scale(1f),
    animationIndex: Int? = null,
    animationTime: Float = 0f,
    onCreate: GltfInstanceScope.() -> Unit = {},
    onUpdate: GltfInstanceScope.() -> Unit = {},
) {
    if (asset == null) return

    val engine = LocalFilamentEngine.current
    val scene = LocalFilamentScene.current

    val instance = remember(asset) {
        asset.assetLoader.createInstance(asset.filamentAsset)
            ?: asset.filamentAsset.getInstance()
    }

    if (!asset.isReady) return

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
