package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.*
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene
import io.github.erkko68.filament.compose.internal.transformMatrix
import io.github.erkko68.filament.gltfio.FilamentAsset
import io.github.erkko68.filament.compose.OnFrame
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
 * @param pivot The point in mesh space that rotation and scale revolve around, and that
 *   ends up at [position] in world space. Defaults to `(0,0,0)` — the glTF's own root origin.
 *   Use this to e.g. rotate a model around its centre when the glTF was authored with the
 *   origin at one corner.
 * @param animationIndex Index of the skeletal animation to play. Null to disable. Ignored when
 *   [animationState] is supplied.
 * @param animationTime Current time in seconds for the animation. Ignored when [animationState]
 *   is supplied.
 * @param animationState Optional hoisted, auto-advancing playback state from
 *   [rememberAnimationState]. When non-null it drives the animator every frame (with cross-fade
 *   blending) and takes precedence over [animationIndex]/[animationTime].
 * @param morphWeights Optional vertex morph-target weights applied to every renderable in the
 *   instance that has morph targets. Pass null to leave morph weights untouched.
 * @param onCreate Called once when the instance is added to the scene. Use for one-time setup
 *   such as swapping materials or finding entities by name.
 * @param onUpdate Called on every recomposition. Use for per-frame updates such as driving
 *   shader parameters, morph targets, or reading animation state.
 */
@Composable
fun FilamentSceneScope.GltfInstance(
    asset: GltfAsset?,
    position: Position = Position(0f),
    rotation: Quaternion = Quaternion(),
    scale: Scale = Scale(1f),
    pivot: Position = Position(0f),
    animationIndex: Int? = null,
    animationTime: Float = 0f,
    animationState: AnimationState? = null,
    morphWeights: FloatArray? = null,
    onCreate: GltfInstanceScope.() -> Unit = {},
    onUpdate: GltfInstanceScope.() -> Unit = {},
) {
    if (asset == null) return

    val engine = LocalFilamentEngine.current
    val scene = LocalFilamentScene.current
    val parent = LocalParentEntity.current

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

    // Push the transform to Filament only when the inputs actually change. SideEffect
    // would re-run on every recomposition, but the transform math and JNI call are
    // wasted if nothing moved.
    DisposableEffect(instance, position, rotation, scale, pivot) {
        val tm = engine.getTransformManager()
        val root = instance.getRoot()
        if (tm.hasComponent(root)) {
            tm.setTransform(tm.getInstance(root), transformMatrix(position, rotation, scale, pivot))
        }
        onDispose { }
    }

    // Reparent the asset root to the surrounding Group, if any. gltfio always creates a
    // transform component on the root, so no need to create one here.
    DisposableEffect(instance, parent) {
        if (parent != null) {
            val tm = engine.getTransformManager()
            val root = instance.getRoot()
            if (tm.hasComponent(root)) {
                tm.setParent(tm.getInstance(root), tm.getInstance(parent))
            }
        }
        onDispose { }
    }

    // Manual single-shot animation. Skipped when a hoisted AnimationState is driving playback.
    DisposableEffect(instance, animationIndex, animationTime, animationState) {
        if (animationState == null && animationIndex != null) {
            val animator = instance.getAnimator()
            if (animationIndex >= 0 && animationIndex < animator.getAnimationCount()) {
                animator.applyAnimation(animationIndex, animationTime)
                animator.updateBoneMatrices()
            }
        }
        onDispose { }
    }

    // Auto-advancing, cross-fading playback driven by the hoisted AnimationState.
    OnFrame { frame ->
        animationState?.apply(instance.getAnimator(), frame.deltaSeconds)
    }

    // Vertex morph-target weights, applied to every renderable that has morph targets. Keyed on
    // the weights' contents (toList) so a new array with equal values doesn't re-push.
    DisposableEffect(instance, morphWeights?.toList()) {
        if (morphWeights != null) {
            val rm = engine.getRenderableManager()
            for (entity in instance.getEntities()) {
                if (!rm.hasComponent(entity)) continue
                val ri = rm.getInstance(entity)
                if (rm.getMorphTargetCount(ri) > 0) rm.setMorphWeights(ri, morphWeights, 0)
            }
        }
        onDispose { }
    }

    SideEffect {
        GltfInstanceScopeImpl(instance, asset.filamentAsset, engine).onUpdate()
    }
}
