package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Aabb
import io.github.erkko68.filament.Entity
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.aabb
import io.github.erkko68.filament.gltfio.capi.*
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
    val root: Entity get() = FilaGltfioFilamentInstance_getRoot(nativeHandle)

    /**
     * Gets the entities of this instance, one per glTF node. All have a Transform component;
     * some also have a Renderable or Name component.
     */
    val entities: IntArray get() = readInts(FilaGltfioFilamentInstance_getEntities(nativeHandle), entityCount)

    /** Gets the number of entities returned by [entities]. */
    val entityCount: Int get() = FilaGltfioFilamentInstance_getEntityCount(nativeHandle)

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
        val handle = FilaGltfioFilamentInstance_getAnimator(nativeHandle)
        check(handle != NullPointer) { ANIMATOR_NOT_LOADED }
        return Animator(handle)
    }

    /**
     * Gets the axis-aligned bounding box from the min/max values in the glTF accessors,
     * transformed for this instance.
     */
    val boundingBox: Aabb get() = aabb { FilaGltfioFilamentInstance_getBoundingBox(nativeHandle, it) }

    /** Recomputes [boundingBox] from the current joint and morph state, for skinned or morphed meshes. */
    fun recomputeBoundingBoxes() = FilaGltfioFilamentInstance_recomputeBoundingBoxes(nativeHandle)

    /** Gets the [FilamentAsset] that owns this instance. */
    val asset: FilamentAsset get() = FilamentAsset(FilaGltfioFilamentInstance_getAsset(nativeHandle))

    /** Gets the number of skins declared in the asset. */
    val skinCount: Int get() = FilaGltfioFilamentInstance_getSkinCount(nativeHandle)

    /** Gets the name of the skin at [skinIndex], or null if it has none. */
    fun getSkinNameAt(skinIndex: Int): String? = stringFromInterop(FilaGltfioFilamentInstance_getSkinNameAt(nativeHandle, skinIndex))

    /**
     * Attaches the given skin to the given node entity, which must have an associated mesh with
     * BONE_INDICES and BONE_WEIGHTS attributes. No-op if the skin index or target is invalid.
     */
    fun attachSkin(skinIndex: Int, target: Entity) {
        FilaGltfioFilamentInstance_attachSkin(nativeHandle, skinIndex, target)
    }

    /** Detaches the given skin from the given node entity. No-op if skin index or target is invalid. */
    fun detachSkin(skinIndex: Int, target: Entity) {
        FilaGltfioFilamentInstance_detachSkin(nativeHandle, skinIndex, target)
    }

    /** Gets the number of joints in the skin at [skinIndex]. */
    fun getJointCountAt(skinIndex: Int): Int = FilaGltfioFilamentInstance_getJointCountAt(nativeHandle, skinIndex)

    /** Gets the joint entities of the skin at [skinIndex]. */
    fun getJointsAt(skinIndex: Int): IntArray =
        readInts(FilaGltfioFilamentInstance_getJointsAt(nativeHandle, skinIndex), getJointCountAt(skinIndex))

    /** Gets the inverse bind matrices of the skin at [skinIndex]: 16 column-major floats per joint. */
    fun getInverseBindMatricesAt(skinIndex: Int): FloatArray =
        readFloats(FilaGltfioFilamentInstance_getInverseBindMatricesAt(nativeHandle, skinIndex), 16 * getJointCountAt(skinIndex))

    /**
     * Applies the material variant at [variantIndex] to all primitives in this instance.
     * Ignored if the index is out of bounds.
     *
     * @see getMaterialVariantName
     */
    fun applyMaterialVariant(variantIndex: Int) {
        FilaGltfioFilamentInstance_applyMaterialVariant(nativeHandle, variantIndex)
    }

    /** Gets all material instances of this instance. These are already bound to renderables. */
    val materialInstances: List<MaterialInstance> get() =
        readPointers(FilaGltfioFilamentInstance_getMaterialInstances(nativeHandle), materialInstanceCount).map { MaterialInstance(it) }

    val materialInstanceCount: Int get() = FilaGltfioFilamentInstance_getMaterialInstanceCount(nativeHandle)

    /**
     * Releases ownership of the material instances so they outlive the asset; the client
     * destroys them.
     */
    fun detachMaterialInstances() = FilaGltfioFilamentInstance_detachMaterialInstances(nativeHandle)

    /** Gets the number of material variants declared in the asset. */
    val materialVariantCount: Int get() = FilaGltfioFilamentInstance_getMaterialVariantCount(nativeHandle)

    /** Gets the name of the material variant at [variantIndex]. */
    fun getMaterialVariantName(variantIndex: Int): String? =
        stringFromInterop(FilaGltfioFilamentInstance_getMaterialVariantName(nativeHandle, variantIndex))

    @InternalFilamentApi
    constructor(nativeHandle: NativePointer) : this() {
        this.nativeHandle = nativeHandle
    }
}

/** Message for the [FilamentInstance.getAnimator] guard each platform applies. */
internal const val ANIMATOR_NOT_LOADED: String =
    "FilamentInstance.animator is only available once the asset's resources are loaded — " +
        "call ResourceLoader.loadResources(asset), or wait for an async load to finish, first."
