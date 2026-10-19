package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.*
import io.github.erkko68.filament.Aabb
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene
import io.github.erkko68.filament.compose.OnFrame
import io.github.erkko68.filament.compose.internal.logWarn
import io.github.erkko68.filament.compose.internal.rememberOwned
import io.github.erkko68.filament.compose.internal.setParent
import io.github.erkko68.filament.compose.internal.transformMatrix
import io.github.erkko68.filament.compose.noFilamentEngine
import io.github.erkko68.filament.compose.noFilamentScene
import io.github.erkko68.filament.gltfio.FilamentAsset
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
 *   origin at one corner. With [fitToSize], it is in the fitted space, where the model is centred on the origin.
 * @param fitToSize Scales the model so its largest side is this many units and centres it on [position], before
 *   [rotation] and [scale] apply — for showing a model whose size and origin you don't know. Null (the default)
 *   keeps the model as authored.
 * @param animationState Optional hoisted, auto-advancing playback state from
 *   [rememberAnimationState]. When non-null it drives the animator every frame (with cross-fade
 *   blending); pause it and call [AnimationState.seek] to hold a pose. Instances sharing one state play in step. Null leaves the animator
 *   alone, for [onUpdate] to drive by hand.
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
    fitToSize: Float? = null,
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

    val instance = remember(asset) { asset.assetLoader.createInstance(asset.filamentAsset) }
        // createInstance failed (platform limitation): fall back to the asset's built-in primary instance, but
        // only for one GltfInstance at a time — aliasing it under several composables would leave them fighting
        // over one transform/animator. The claim is given back when this GltfInstance leaves the composition.
        ?: rememberOwned(engine, asset, create = {
            if (!asset.primaryInstanceClaimed) {
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
        }) { asset.primaryInstanceClaimed = false }
        ?: return

    if (!asset.isReady) return

    // Scene membership tracks `visible`; onCreate fires once, on the first time the instance
    // actually enters the scene.
    val createdFired = remember(instance) { booleanArrayOf(false) }
    DisposableEffect(instance, effectiveVisible) {
        if (effectiveVisible) {
            if (!createdFired[0]) instance.boundUnboundedRenderables(engine)
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
    val fit = remember(asset, fitToSize) { fitToSize?.let { asset.filamentAsset.boundingBox.fitTo(it) } }
    DisposableEffect(instance, position, rotation, scale, pivot, fit) {
        val tm = engine.transformManager
        val root = instance.root
        if (tm.hasComponent(root)) {
            val matrix = if (fit == null) transformMatrix(position, rotation, scale, pivot) else {
                // The fit S(f)·T(-centre) runs first: it folds into the scale, and the pivot moves from fitted to mesh space.
                val (f, c) = fit
                transformMatrix(position, rotation, scale * f, Position(c.x + pivot.x / f, c.y + pivot.y / f, c.z + pivot.z / f))
            }
            tm.setTransform(tm.getInstance(root), matrix)
        }
        onDispose { }
    }

    // Reparent the asset root to the surrounding Group, if any. gltfio always creates a
    // transform component on the root, so no need to create one here.
    DisposableEffect(instance, parent) {
        engine.setParent(instance.root, parent)
        onDispose { }
    }

    // Auto-advancing, cross-fading playback driven by the hoisted AnimationState. Only with one: a frame loop keeps
    // the window rendering every vsync.
    if (animationState != null) {
        OnFrame { frame -> animationState.apply(instance.animator, frame.deltaSeconds, frame.frameTimeNanos) }
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

    // Shadow-flag overrides on every renderable. null keeps the asset's authored values, remembered at the
    // first override so that going back to null restores them.
    val authoredShadows = remember(instance) { HashMap<Int, Pair<Boolean, Boolean>>() }
    DisposableEffect(instance, castShadows, receiveShadows) {
        if (castShadows != null || receiveShadows != null || authoredShadows.isNotEmpty()) {
            val rm = engine.renderableManager
            for (entity in instance.entities) {
                if (!rm.hasComponent(entity)) continue
                val ri = rm.getInstance(entity)
                val (cast, receive) = authoredShadows.getOrPut(entity) { rm.isShadowCaster(ri) to rm.isShadowReceiver(ri) }
                rm.setCastShadows(ri, castShadows ?: cast)
                rm.setReceiveShadows(ri, receiveShadows ?: receive)
            }
        }
        onDispose { }
    }

    SideEffect {
        GltfInstanceScopeImpl(instance, asset.filamentAsset, engine).onUpdate()
    }
}

/** The uniform factor that makes this box's largest side [size], and its centre; null for a box with no finite extent. */
private fun Aabb.fitTo(size: Float): Pair<Float, Position>? {
    val h = extent()
    val side = 2f * maxOf(h[0], h[1], h[2])
    if (!side.isFinite() || side <= 0f) return null
    val c = center()
    return size / side to Position(c[0], c[1], c[2])
}

/**
 * gltfio gives the renderables of an instance it computed no bounds for an infinite box ("Missing bounding box in
 * ..."). Culled through the instance's transform, that box turns to NaNs, so the renderable is drawn or dropped
 * depending on its rotation: a turning model blinks. Such boxes are rebuilt from the vertices, which needs the
 * resources loaded and the source data still there.
 */
private fun FilamentInstance.boundUnboundedRenderables(engine: Engine) {
    val rm = engine.renderableManager
    val unbounded = entities.any { entity ->
        rm.hasComponent(entity) && rm.getAxisAlignedBoundingBox(rm.getInstance(entity)).halfExtent.any { !it.isFinite() }
    }
    if (unbounded) recomputeBoundingBoxes()
}
