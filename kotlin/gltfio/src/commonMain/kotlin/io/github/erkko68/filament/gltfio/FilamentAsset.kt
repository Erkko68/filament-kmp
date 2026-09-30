package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Aabb
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Entity
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.Scene
import io.github.erkko68.filament.aabb
import io.github.erkko68.filament.gltfio.capi.*
import io.github.erkko68.filament.interop.*

/**
 * FilamentAsset owns a loaded glTF 2.0 asset and all its Filament objects.
 *
 * A FilamentAsset represents a complete glTF scene with a hierarchy of entities, each having
 * a Transform component. Some entities also have Renderable, Light, Camera, or animation components.
 *
 * Assets own strong references to VertexBuffer, IndexBuffer, Texture objects, and optionally
 * an Animator for skeletal animations. External resource loading (textures, buffer data) is
 * handled separately via ResourceLoader.
 *
 * **Note:** Only the default glTF scene is loaded; other glTF scenes are ignored.
 *
 * @see AssetLoader
 * @see FilamentInstance
 * @see ResourceLoader
 */
class FilamentAsset @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Gets the transform root of the asset, an extra entity with no matching glTF node.
     *
     * It exists so the entire asset can be transformed as one. For instanced assets this is a
     * "super root" whose children are the per-instance roots, allowing all instances to be
     * moved en masse.
     */
    val root: Entity get() = FilaGltfioFilamentAsset_getRoot(nativeHandle)

    /**
     * Pops a ready renderable off the async-load queue, or returns 0 if none is ready.
     *
     * Allows progressive reveal: add renderables to the scene as their textures become ready
     * during [ResourceLoader.asyncBeginLoad], e.g. `while ((e = popRenderable()) != 0) scene.addEntity(e)`.
     * Use [ResourceLoader.asyncGetLoadProgress] for the overall progress. Progressive reveal is
     * not supported for dynamically added instances.
     */
    fun popRenderable(): Entity = FilaGltfioFilamentAsset_popRenderable(nativeHandle)

    /**
     * Pops up to `entities.size` ready renderables off the async-load queue into [entities].
     *
     * @return the number of entities written.
     * @see popRenderable
     */
    fun popRenderables(entities: IntArray): Int =
        entities.usePinned { FilaGltfioFilamentAsset_popRenderables(nativeHandle, it, entities.size) }

    /**
     * Gets the list of entities, one per glTF node. All have a Transform component; some also
     * have a Renderable and/or Light component.
     */
    val entities: IntArray get() = readInts(FilaGltfioFilamentAsset_getEntities(nativeHandle), entityCount)

    /** Gets the number of entities returned by [entities]. */
    val entityCount: Int get() = FilaGltfioFilamentAsset_getEntityCount(nativeHandle)

    /** Gets the entities representing lights. All of these have a Light component. */
    val lightEntities: IntArray get() = readInts(FilaGltfioFilamentAsset_getLightEntities(nativeHandle), lightEntityCount)

    val lightEntityCount: Int get() = FilaGltfioFilamentAsset_getLightEntityCount(nativeHandle)

    /** Gets the entities that have Renderable components. */
    val renderableEntities: IntArray get() = readInts(FilaGltfioFilamentAsset_getRenderableEntities(nativeHandle), renderableEntityCount)

    val renderableEntityCount: Int get() = FilaGltfioFilamentAsset_getRenderableEntityCount(nativeHandle)

    /**
     * Gets the entities representing cameras. All of these have a Camera component.
     *
     * gltfio always sets a perspective projection with aspect ratio 1.0 and then applies the
     * glTF file's aspect ratio through the camera's *scaling* matrix, so clients can adjust
     * the aspect ratio independently of the projection:
     * `camera.setScaling(1.0 / newAspectRatio, 1.0)`.
     */
    val cameraEntities: IntArray get() = readInts(FilaGltfioFilamentAsset_getCameraEntities(nativeHandle), cameraEntityCount)

    val cameraEntityCount: Int get() = FilaGltfioFilamentAsset_getCameraEntityCount(nativeHandle)

    /**
     * Gets up to `entities.size` entities whose name label matches [name] exactly into [entities].
     *
     * @return the number of entities written.
     */
    fun getEntitiesByName(name: String, entities: IntArray): Int =
        name.useCString { s -> entities.usePinned { FilaGltfioFilamentAsset_getEntitiesByName(nativeHandle, s, it, entities.size) } }

    /**
     * Gets up to `entities.size` entities whose name label starts with [prefix] into [entities].
     *
     * @return the number of entities written.
     */
    fun getEntitiesByPrefix(prefix: String, entities: IntArray): Int =
        prefix.useCString { s -> entities.usePinned { FilaGltfioFilamentAsset_getEntitiesByPrefix(nativeHandle, s, it, entities.size) } }

    /** Returns the first entity with the given name, or 0 if none exists. */
    fun getFirstEntityByName(name: String): Entity = name.useCString { FilaGltfioFilamentAsset_getFirstEntityByName(nativeHandle, it) }

    /** Returns every [FilamentInstance] created from this asset. */
    val assetInstances: List<FilamentInstance> get() =
        readPointers(FilaGltfioFilamentAsset_getAssetInstances(nativeHandle), assetInstanceCount).map { FilamentInstance(it) }

    /** Returns the number of instances created from this asset (>= 1 unless detached). */
    val assetInstanceCount: Int get() = FilaGltfioFilamentAsset_getAssetInstanceCount(nativeHandle)

    /**
     * Gets the bounding box computed from the min/max values in the glTF accessors.
     *
     * This is a straightforward load-time AABB over the asset data — it does not account for
     * per-instance transforms (see [FilamentInstance.boundingBox] for that).
     */
    val boundingBox: Aabb get() = aabb { FilaGltfioFilamentAsset_getBoundingBox(nativeHandle, it) }

    /** Gets the name label for the given entity, or null if it has none. */
    fun getName(entity: Entity): String? = stringFromInterop(FilaGltfioFilamentAsset_getName(nativeHandle, entity))

    /** Gets the glTF `extras` string for the given node entity (or for the asset itself), if any. */
    fun getExtras(entity: Entity): String? = stringFromInterop(FilaGltfioFilamentAsset_getExtras(nativeHandle, entity))

    /** Gets the name of morph target [targetIndex] declared on the given entity. */
    fun getMorphTargetNameAt(entity: Entity, targetIndex: Int): String? =
        stringFromInterop(FilaGltfioFilamentAsset_getMorphTargetNameAt(nativeHandle, entity, targetIndex))

    /** Gets the number of morph targets declared on the given entity. */
    fun getMorphTargetCountAt(entity: Entity): Int = FilaGltfioFilamentAsset_getMorphTargetCountAt(nativeHandle, entity)

    /** Gets the URIs of all externally-referenced buffers/textures (to feed [ResourceLoader]). */
    val resourceUris: List<String> get() =
        readPointers(FilaGltfioFilamentAsset_getResourceUris(nativeHandle), resourceUriCount).map { stringFromInterop(it) ?: "" }

    val resourceUriCount: Int get() = FilaGltfioFilamentAsset_getResourceUriCount(nativeHandle)

    /** Lazily creates a single LINES renderable that draws the transformed bounding-box hierarchy. */
    val wireframe: Entity get() = FilaGltfioFilamentAsset_getWireframe(nativeHandle)

    /**
     * Reclaims CPU-side memory for URI strings, binding lists, and raw animation data.
     *
     * Call only after [ResourceLoader.loadResources]. On an instanced asset this prevents the
     * creation of new instances.
     */
    fun releaseSourceData() {
        FilaGltfioFilamentAsset_releaseSourceData(nativeHandle)
    }

    /** Returns the [Engine] associated with the [AssetLoader] that created this asset. */
    val engine: Engine get() =
        io.github.erkko68.filament.Engine(FilaGltfioFilamentAsset_getEngine(nativeHandle))

    /** Gets the number of glTF scenes in the asset. */
    val sceneCount: Int get() = FilaGltfioFilamentAsset_getSceneCount(nativeHandle)

    /** Gets the name of glTF scene [sceneIndex], or null if it has none. */
    fun getSceneName(sceneIndex: Int): String? = stringFromInterop(FilaGltfioFilamentAsset_getSceneName(nativeHandle, sceneIndex))

    /**
     * Adds the [entities] that belong to one of the glTF scenes in [sceneFilter] (a bit mask of
     * scene indices) to [targetScene].
     */
    fun addEntitiesToScene(targetScene: Scene, entities: IntArray, sceneFilter: Int) =
        entities.usePinned { FilaGltfioFilamentAsset_addEntitiesToScene(nativeHandle, targetScene.nativeObject, it, entities.size, sceneFilter) }

    /**
     * Releases ownership of the Filament components (renderables, lights, …) so they outlive
     * [AssetLoader.destroyAsset]; the client destroys them.
     */
    fun detachFilamentComponents() = FilaGltfioFilamentAsset_detachFilamentComponents(nativeHandle)

    val areFilamentComponentsDetached: Boolean get() = FilaGltfioFilamentAsset_areFilamentComponentsDetached(nativeHandle)

    /** Convenience accessor for the first instance ([assetInstances]`[0]`). */
    val instance: FilamentInstance get() =
        FilamentInstance(FilaGltfioFilamentAsset_getInstance(nativeHandle))
}
