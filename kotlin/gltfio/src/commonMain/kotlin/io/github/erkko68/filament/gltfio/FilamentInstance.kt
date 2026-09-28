package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Entity
import io.github.erkko68.filament.*
import io.github.erkko68.filament.interop.*

/**
 * FilamentInstance provides access to a hierarchy of entities instanced from a glTF asset.
 *
 * Every entity has a TransformManager component. Some entities also have Name or Renderable
 * components. Instances share material and geometry data with the parent FilamentAsset but
 * maintain independent entity hierarchies, transforms, and animation states.
 *
 * **Key features:**
 * - Independent entity tree for each instance
 * - Skeletal animation via getAnimator() (independent per-instance or shared from asset)
 * - Material variant switching via applyMaterialVariant()
 * - Skin/skeleton access for joint manipulation
 * - Independent bounding box and transform root
 *
 * **Animation ownership:**
 * Each instance has its own Animator, but it can also be obtained from the parent
 * FilamentAsset. Using the asset's animator means all instances share the same animation frame;
 * using instance animators allows independent control.
 *
 * @see FilamentAsset
 * @see Animator
 * @see AssetLoader
 */
class FilamentInstance {
    internal var nativeHandle: NativePointer = NullPointer

    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Create a new FilamentInstance.
     *
     * Instances are normally created via AssetLoader.createInstance(asset) but can be
     * manually constructed for advanced use cases.
     */
    constructor()

    /** Gets the transform root entity of this instance, which has no matching glTF node. */
    val root: Entity get() = FilaFilamentInstance_getRoot(nativeHandle)

    /**
     * Gets the entities of this instance, one per glTF node. All have a Transform component;
     * some also have a Renderable or Name component.
     */
    val entities: IntArray get() = entityArray(entityCount) { FilaFilamentInstance_getEntities(nativeHandle, it) }

    /** Gets the number of entities returned by [getEntities]. */
    val entityCount: Int get() = FilaFilamentInstance_getEntityCount(nativeHandle)

    /**
     * Returns the animation engine for this instance.
     *
     * An animator can be obtained either from an individual instance (independent per-instance
     * playback) or from the originating [FilamentAsset] (frame shared amongst all instances).
     * The animator is owned by the asset — do not destroy it manually.
     *
     * **Load the asset's resources first.** gltfio creates the animator while loading resources
     * (it needs the animation buffer data), so this is only available after
     * [ResourceLoader.loadResources] — or after an async load has reported completion. Calling it
     * earlier throws; on Android it cannot be detected and returns an animator that crashes on use.
     *
     * @throws IllegalStateException if the asset's resources have not been loaded yet.
     */
    val animator: Animator get() {
        // Null until ResourceLoader has loaded the asset — gltfio creates the animator there.
        val handle = FilaFilamentInstance_getAnimator(nativeHandle)
        check(handle != NullPointer) { ANIMATOR_NOT_LOADED }
        return Animator(handle)
    }

    /**
     * Gets the axis-aligned bounding box from the min/max values in the glTF accessors,
     * transformed for this instance.
     */
    val boundingBox: Box get() {
        val box = Box()
        box.center.usePinned { c -> box.halfExtent.usePinned { h -> FilaFilamentInstance_getBoundingBox(nativeHandle, c, h) } }
        return box
    }

    /** Gets the [FilamentAsset] that owns this instance. */
    val asset: FilamentAsset get() = FilamentAsset(FilaFilamentInstance_getAsset(nativeHandle))

    /** Gets the number of skins declared in the asset. */
    val skinCount: Int get() = FilaFilamentInstance_getSkinCount(nativeHandle)

    /** Gets the names of all skins, in skin-index order. */
    val skinNames: List<String> get() =
        List(skinCount) { stringFromInterop(FilaFilamentInstance_getSkinNameAt(nativeHandle, it)) ?: "" }

    /**
     * Attaches the given skin to the given node entity, which must have an associated mesh with
     * BONE_INDICES and BONE_WEIGHTS attributes. No-op if the skin index or target is invalid.
     */
    fun attachSkin(skinIndex: Int, target: Entity) {
        FilaFilamentInstance_attachSkin(nativeHandle, skinIndex, target)
    }

    /** Detaches the given skin from the given node entity. No-op if skin index or target is invalid. */
    fun detachSkin(skinIndex: Int, target: Entity) {
        FilaFilamentInstance_detachSkin(nativeHandle, skinIndex, target)
    }

    /** Gets the number of joints in the skin at [skinIndex]. */
    fun getJointCountAt(skinIndex: Int): Int = FilaFilamentInstance_getJointCountAt(nativeHandle, skinIndex)

    /** Gets the joint entities of the skin at [skinIndex]. */
    fun getJointsAt(skinIndex: Int): IntArray =
        entityArray(getJointCountAt(skinIndex)) { FilaFilamentInstance_getJointsAt(nativeHandle, skinIndex, it) }

    /**
     * Applies the material variant at [variantIndex] to all primitives in this instance.
     * Ignored if the index is out of bounds.
     *
     * @see getMaterialVariantNames
     */
    fun applyMaterialVariant(variantIndex: Int) {
        FilaFilamentInstance_applyMaterialVariant(nativeHandle, variantIndex)
    }

    /** Gets all material instances of this instance. These are already bound to renderables. */
    val materialInstances: List<io.github.erkko68.filament.MaterialInstance> get() =
        List(FilaFilamentInstance_getMaterialInstanceCount(nativeHandle)) {
            io.github.erkko68.filament.MaterialInstance(FilaFilamentInstance_getMaterialInstanceAt(nativeHandle, it))
        }

    /** Gets the names of all material variants declared in the asset, in variant-index order. */
    val materialVariantNames: List<String> get() =
        List(FilaFilamentInstance_getMaterialVariantCount(nativeHandle)) {
            stringFromInterop(FilaFilamentInstance_getMaterialVariantNameAt(nativeHandle, it)) ?: ""
        }

    @InternalFilamentApi
    constructor(nativeHandle: NativePointer) : this() {
        this.nativeHandle = nativeHandle
    }
}

/** Message for the [FilamentInstance.getAnimator] guard each platform applies. */
internal const val ANIMATOR_NOT_LOADED: String =
    "FilamentInstance.animator is only available once the asset's resources are loaded — " +
        "call ResourceLoader.loadResources(asset), or wait for an async load to finish, first."

@ExternalSymbolName("FilaFilamentInstance_getRoot")
private external fun FilaFilamentInstance_getRoot(instance: NativePointer): Int

@ExternalSymbolName("FilaFilamentInstance_getEntities")
private external fun FilaFilamentInstance_getEntities(instance: NativePointer, entities: NativePointer)

@ExternalSymbolName("FilaFilamentInstance_getEntityCount")
private external fun FilaFilamentInstance_getEntityCount(instance: NativePointer): Int

@ExternalSymbolName("FilaFilamentInstance_getAnimator")
private external fun FilaFilamentInstance_getAnimator(instance: NativePointer): NativePointer

@ExternalSymbolName("FilaFilamentInstance_getBoundingBox")
private external fun FilaFilamentInstance_getBoundingBox(instance: NativePointer, center: NativePointer, halfExtent: NativePointer)

@ExternalSymbolName("FilaFilamentInstance_getAsset")
private external fun FilaFilamentInstance_getAsset(instance: NativePointer): NativePointer

@ExternalSymbolName("FilaFilamentInstance_getSkinCount")
private external fun FilaFilamentInstance_getSkinCount(instance: NativePointer): Int

@ExternalSymbolName("FilaFilamentInstance_getSkinNameAt")
private external fun FilaFilamentInstance_getSkinNameAt(instance: NativePointer, skinIndex: Int): NativePointer

@ExternalSymbolName("FilaFilamentInstance_attachSkin")
private external fun FilaFilamentInstance_attachSkin(instance: NativePointer, skinIndex: Int, entity: Int)

@ExternalSymbolName("FilaFilamentInstance_detachSkin")
private external fun FilaFilamentInstance_detachSkin(instance: NativePointer, skinIndex: Int, entity: Int)

@ExternalSymbolName("FilaFilamentInstance_getJointCountAt")
private external fun FilaFilamentInstance_getJointCountAt(instance: NativePointer, skinIndex: Int): Int

@ExternalSymbolName("FilaFilamentInstance_getJointsAt")
private external fun FilaFilamentInstance_getJointsAt(instance: NativePointer, skinIndex: Int, joints: NativePointer)

@ExternalSymbolName("FilaFilamentInstance_applyMaterialVariant")
private external fun FilaFilamentInstance_applyMaterialVariant(instance: NativePointer, variantIndex: Int)

@ExternalSymbolName("FilaFilamentInstance_getMaterialInstanceCount")
private external fun FilaFilamentInstance_getMaterialInstanceCount(instance: NativePointer): Int

@ExternalSymbolName("FilaFilamentInstance_getMaterialInstanceAt")
private external fun FilaFilamentInstance_getMaterialInstanceAt(instance: NativePointer, index: Int): NativePointer

@ExternalSymbolName("FilaFilamentInstance_getMaterialVariantCount")
private external fun FilaFilamentInstance_getMaterialVariantCount(instance: NativePointer): Int

@ExternalSymbolName("FilaFilamentInstance_getMaterialVariantNameAt")
private external fun FilaFilamentInstance_getMaterialVariantNameAt(instance: NativePointer, variantIndex: Int): NativePointer
