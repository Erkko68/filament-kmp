package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.*
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.noFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene
import io.github.erkko68.filament.compose.noFilamentScene
import io.github.erkko68.filament.compose.internal.logWarn
import io.github.erkko68.filament.compose.internal.transformMatrix
import io.github.erkko68.filament.gltfio.FilamentAsset
import io.github.erkko68.filament.compose.OnFrame
import io.github.erkko68.filament.gltfio.FilamentInstance

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
 * ### Emitting from a list
 * Wrap each call in `key()` when the collection can change:
 * ```kotlin
 * enemies.forEach { enemy -> key(enemy.id) { GltfInstance(enemyAsset, enemy.position) } }
 * ```
 * Without it, identity follows the slot rather than the item, so inserting or reordering makes
 * every later entry destroy its instance and build a new one — restarting animation playback and
 * re-running [onCreate]. Appending to the end is the only safe unkeyed case.
 *
 * ### Lifecycle Note
 * Filament's `gltfio` does not have a `destroyInstance` API; instances are automatically
 * destroyed when the parent [GltfAsset] is destroyed. To avoid memory pressure, avoid
 * creating hundreds of dynamic instances from a single long-lived asset.
 *
 * @param asset A loaded asset from [rememberGltfAsset], or null while still loading.
 * @param position World-space translation.
 * @param rotation World-space rotation. Build one with [Rotation.axisAngle]/[Rotation.euler].
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
 * @param visible Whether the instance's entities are in the scene. False removes them (cheaply,
 *   keeping the instance and its state alive) — a show/hide toggle.
 * @param castShadows Overrides shadow casting for every renderable in the instance; null (the
 *   default) keeps what the asset authored — matching the primitives' `castShadows` toggle.
 * @param receiveShadows Overrides shadow receiving for every renderable in the instance; null
 *   (the default) keeps what the asset authored.
 * @param onCreate Called once when the instance is first added to the scene. Use for one-time
 *   setup such as swapping materials or finding entities by name.
 * @param onUpdate Called on every **recomposition** (not every frame — a static scene never
 *   recomposes). Use it to react to Compose state changes with low-level calls; for genuinely
 *   per-frame work use [OnFrame] or `rememberFilamentScene`'s `FilamentEffect`.
 */
@Composable
fun FilamentSceneScope.GltfInstance(
    asset: GltfAsset?,
    position: Position = Position(0f),
    rotation: Rotation = Rotation.Identity,
    scale: Scale = Scale(1f),
    pivot: Position = Position(0f),
    animationIndex: Int? = null,
    animationTime: Float = 0f,
    animationState: AnimationState? = null,
    morphWeights: FloatArray? = null,
    visible: Boolean = true,
    castShadows: Boolean? = null,
    receiveShadows: Boolean? = null,
    onCreate: GltfInstanceScope.() -> Unit = {},
    onUpdate: GltfInstanceScope.() -> Unit = {},
) {
    if (asset == null) return

    val engine = LocalFilamentEngine.current ?: noFilamentEngine()
    val scene = LocalFilamentScene.current ?: noFilamentScene()
    val parent = LocalParentEntity.current
    // A hidden enclosing Group hides its whole subtree.
    val effectiveVisible = visible && LocalGroupVisible.current

    val instance = remember(asset) {
        asset.assetLoader.createInstance(asset.filamentAsset)
            ?: if (!asset.primaryInstanceClaimed) {
                // createInstance failed (platform limitation): fall back to the asset's built-in
                // primary instance, but only for one GltfInstance — aliasing it under several
                // composables would leave them fighting over one transform/animator.
                asset.primaryInstanceClaimed = true
                asset.filamentAsset.instance
            } else {
                logWarn(
                    "GltfInstance could not create an additional instance for this asset " +
                        "(createInstance failed and the primary instance is already in use) — " +
                        "this GltfInstance renders nothing.",
                )
                null
            }
    } ?: return

    if (!asset.isReady) return

    // Scene membership tracks `visible`; onCreate fires once, on the first time the instance
    // actually enters the scene.
    val createdFired = remember(instance) { booleanArrayOf(false) }
    DisposableEffect(instance, effectiveVisible) {
        if (effectiveVisible) {
            scene.addEntities(instance.entities)
            if (!createdFired[0]) {
                createdFired[0] = true
                GltfInstanceScopeImpl(instance, asset.filamentAsset, engine).onCreate()
            }
        }
        onDispose {
            if (effectiveVisible) scene.removeEntities(instance.entities)
        }
    }

    // Push the transform to Filament only when the inputs actually change. SideEffect
    // would re-run on every recomposition, but the transform math and JNI call are
    // wasted if nothing moved.
    DisposableEffect(instance, position, rotation, scale, pivot) {
        val tm = engine.transformManager
        val root = instance.root
        if (tm.hasComponent(root)) {
            tm.setTransform(tm.getInstance(root), transformMatrix(position, rotation, scale, pivot))
        }
        onDispose { }
    }

    // Reparent the asset root to the surrounding Group, if any. gltfio always creates a
    // transform component on the root, so no need to create one here.
    DisposableEffect(instance, parent) {
        if (parent != null) {
            val tm = engine.transformManager
            val root = instance.root
            if (tm.hasComponent(root)) {
                tm.setParent(tm.getInstance(root), tm.getInstance(parent))
            }
        }
        onDispose { }
    }

    // Manual single-shot animation. Skipped when a hoisted AnimationState is driving playback.
    DisposableEffect(instance, animationIndex, animationTime, animationState) {
        if (animationState == null && animationIndex != null) {
            val animator = instance.animator
            if (animationIndex >= 0 && animationIndex < animator.animationCount) {
                animator.applyAnimation(animationIndex, animationTime)
                animator.updateBoneMatrices()
            }
        }
        onDispose { }
    }

    // Auto-advancing, cross-fading playback driven by the hoisted AnimationState.
    OnFrame { frame ->
        animationState?.apply(instance.animator, frame.deltaSeconds)
    }

    // Vertex morph-target weights, applied to every renderable that has morph targets.
    //
    // Content-compared rather than keyed: FloatArray is an unstable Compose parameter type, so this
    // composable recomposes with its parent even when nothing changed, and morph weights are
    // animated per frame by nature. Keying on `morphWeights?.toList()` would box every float on
    // every one of those recompositions just to decide whether to skip a push — allocating more
    // than the gating saves. The remembered copy compares exactly and only allocates when the
    // weights actually change.
    val lastMorphWeights = remember(instance) { arrayOfNulls<FloatArray>(1) }
    SideEffect {
        if (morphWeights != null && !morphWeights.contentEquals(lastMorphWeights[0])) {
            lastMorphWeights[0] = morphWeights.copyOf()
            val rm = engine.renderableManager
            for (entity in instance.entities) {
                if (!rm.hasComponent(entity)) continue
                val ri = rm.getInstance(entity)
                if (rm.getMorphTargetCount(ri) > 0) rm.setMorphWeights(ri, morphWeights, 0)
            }
        }
    }

    // Shadow-flag overrides on every renderable. null keeps the asset's authored values.
    DisposableEffect(instance, castShadows, receiveShadows) {
        if (castShadows != null || receiveShadows != null) {
            val rm = engine.renderableManager
            for (entity in instance.entities) {
                if (!rm.hasComponent(entity)) continue
                val ri = rm.getInstance(entity)
                if (castShadows != null) rm.setCastShadows(ri, castShadows)
                if (receiveShadows != null) rm.setReceiveShadows(ri, receiveShadows)
            }
        }
        onDispose { }
    }

    SideEffect {
        GltfInstanceScopeImpl(instance, asset.filamentAsset, engine).onUpdate()
    }
}
