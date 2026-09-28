package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Entity
import io.github.erkko68.filament.*
import io.github.erkko68.filament.interop.*
import io.github.erkko68.filament.InternalFilamentApi

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
    val root: Entity get() = FilaFilamentAsset_getRoot(nativeHandle)

    /**
     * Pops a ready renderable off the async-load queue, or returns 0 if none is ready.
     *
     * Allows progressive reveal: add renderables to the scene as their textures become ready
     * during [ResourceLoader.asyncBeginLoad], e.g. `while ((e = popRenderable()) != 0) scene.addEntity(e)`.
     * Use [ResourceLoader.asyncGetLoadProgress] for the overall progress. Progressive reveal is
     * not supported for dynamically added instances.
     */
    fun popRenderable(): Entity = FilaFilamentAsset_popRenderable(nativeHandle)

    /**
     * Pops up to `entities.size` ready renderables off the async-load queue into [entities].
     *
     * @return the number of entities written.
     * @see popRenderable
     */
    fun popRenderables(entities: IntArray): Int =
        entities.usePinned { FilaFilamentAsset_popRenderables(nativeHandle, it, entities.size) }

    /**
     * Gets the list of entities, one per glTF node. All have a Transform component; some also
     * have a Renderable and/or Light component.
     */
    val entities: IntArray get() = entityArray(FilaFilamentAsset_getEntityCount(nativeHandle)) { FilaFilamentAsset_getEntities(nativeHandle, it) }

    /** Gets the entities representing lights. All of these have a Light component. */
    val lightEntities: IntArray get() = entityArray(FilaFilamentAsset_getLightEntityCount(nativeHandle)) { FilaFilamentAsset_getLightEntities(nativeHandle, it) }

    /** Gets the entities that have Renderable components. */
    val renderableEntities: IntArray get() = entityArray(FilaFilamentAsset_getRenderableEntityCount(nativeHandle)) { FilaFilamentAsset_getRenderableEntities(nativeHandle, it) }

    /**
     * Gets the entities representing cameras. All of these have a Camera component.
     *
     * gltfio always sets a perspective projection with aspect ratio 1.0 and then applies the
     * glTF file's aspect ratio through the camera's *scaling* matrix, so clients can adjust
     * the aspect ratio independently of the projection:
     * `camera.setScaling(1.0 / newAspectRatio, 1.0)`.
     */
    val cameraEntities: IntArray get() = entityArray(FilaFilamentAsset_getCameraEntityCount(nativeHandle)) { FilaFilamentAsset_getCameraEntities(nativeHandle, it) }

    /** Gets all entities whose name label matches [name] exactly. */
    fun getEntitiesByName(name: String): IntArray {
        val found = IntArray(entityCount)
        val count = name.useCString { s -> found.usePinned { FilaFilamentAsset_getEntitiesByName(nativeHandle, s, it, found.size) } }
        return found.copyOf(count)
    }

    /** Gets all entities whose name label starts with [prefix]. */
    fun getEntitiesByPrefix(prefix: String): IntArray {
        val found = IntArray(entityCount)
        val count = prefix.useCString { s -> found.usePinned { FilaFilamentAsset_getEntitiesByPrefix(nativeHandle, s, it, found.size) } }
        return found.copyOf(count)
    }

    /** Returns the first entity with the given name, or 0 if none exists. */
    fun getFirstEntityByName(name: String): Entity = name.useCString { FilaFilamentAsset_getFirstEntityByName(nativeHandle, it) }

    /** Gets the number of entities returned by [getEntities]. */
    val entityCount: Int get() = FilaFilamentAsset_getEntityCount(nativeHandle)

    /** Returns the number of instances created from this asset (>= 1 unless detached). */
    val assetInstanceCount: Int get() = FilaFilamentAsset_getAssetInstanceCount(nativeHandle)

    /** Returns every [FilamentInstance] created from this asset. */
    val assetInstances: List<FilamentInstance> get() =
        List(assetInstanceCount) { FilamentInstance(FilaFilamentAsset_getAssetInstanceAt(nativeHandle, it)) }

    /**
     * Gets the bounding box computed from the min/max values in the glTF accessors.
     *
     * This is a straightforward load-time AABB over the asset data — it does not account for
     * per-instance transforms (see [FilamentInstance.getBoundingBox] for that).
     */
    val boundingBox: Box get() {
        val box = Box()
        box.center.usePinned { c -> box.halfExtent.usePinned { h -> FilaFilamentAsset_getBoundingBox(nativeHandle, c, h) } }
        return box
    }

    /** Gets the name label for the given entity, or null if it has none. */
    fun getName(entity: Entity): String? = stringFromInterop(FilaFilamentAsset_getName(nativeHandle, entity))

    /** Gets the glTF `extras` string for the given node entity (or for the asset itself), if any. */
    fun getExtras(entity: Entity): String? = stringFromInterop(FilaFilamentAsset_getExtras(nativeHandle, entity))

    /** Gets the morph target names declared on the given entity, in target order. */
    fun getMorphTargetNames(entity: Entity): List<String> =
        List(FilaFilamentAsset_getMorphTargetCountAt(nativeHandle, entity)) {
            stringFromInterop(FilaFilamentAsset_getMorphTargetNameAt(nativeHandle, entity, it)) ?: ""
        }

    /** Gets the URIs of all externally-referenced buffers/textures (to feed [ResourceLoader]). */
    val resourceUris: List<String> get() =
        List(FilaFilamentAsset_getResourceUriCount(nativeHandle)) { stringFromInterop(FilaFilamentAsset_getResourceUriAt(nativeHandle, it)) ?: "" }

    /**
     * Reclaims CPU-side memory for URI strings, binding lists, and raw animation data.
     *
     * Call only after [ResourceLoader.loadResources]. On an instanced asset this prevents the
     * creation of new instances.
     */
    fun releaseSourceData() {
        FilaFilamentAsset_releaseSourceData(nativeHandle)
    }

    /** Returns the [Engine] associated with the [AssetLoader] that created this asset. */
    val engine: Engine get() =
        io.github.erkko68.filament.Engine(FilaFilamentAsset_getEngine(nativeHandle))

    /** Convenience accessor for the first instance ([getAssetInstances]`[0]`). */
    val instance: FilamentInstance get() =
        FilamentInstance(FilaFilamentAsset_getInstance(nativeHandle))
}

/** [count] entities that [fill] writes through the pointer it's given. */
internal inline fun entityArray(count: Int, fill: (NativePointer) -> Unit): IntArray =
    if (count == 0) IntArray(0) else IntArray(count).also { a -> a.usePinned(fill) }

@ExternalSymbolName("FilaFilamentAsset_getRoot")
private external fun FilaFilamentAsset_getRoot(asset: NativePointer): Int

@ExternalSymbolName("FilaFilamentAsset_popRenderable")
private external fun FilaFilamentAsset_popRenderable(asset: NativePointer): Int

@ExternalSymbolName("FilaFilamentAsset_popRenderables")
private external fun FilaFilamentAsset_popRenderables(asset: NativePointer, entities: NativePointer, count: Int): Int

@ExternalSymbolName("FilaFilamentAsset_getEntityCount")
private external fun FilaFilamentAsset_getEntityCount(asset: NativePointer): Int

@ExternalSymbolName("FilaFilamentAsset_getEntities")
private external fun FilaFilamentAsset_getEntities(asset: NativePointer, entities: NativePointer)

@ExternalSymbolName("FilaFilamentAsset_getLightEntityCount")
private external fun FilaFilamentAsset_getLightEntityCount(asset: NativePointer): Int

@ExternalSymbolName("FilaFilamentAsset_getLightEntities")
private external fun FilaFilamentAsset_getLightEntities(asset: NativePointer, entities: NativePointer)

@ExternalSymbolName("FilaFilamentAsset_getRenderableEntityCount")
private external fun FilaFilamentAsset_getRenderableEntityCount(asset: NativePointer): Int

@ExternalSymbolName("FilaFilamentAsset_getRenderableEntities")
private external fun FilaFilamentAsset_getRenderableEntities(asset: NativePointer, entities: NativePointer)

@ExternalSymbolName("FilaFilamentAsset_getCameraEntityCount")
private external fun FilaFilamentAsset_getCameraEntityCount(asset: NativePointer): Int

@ExternalSymbolName("FilaFilamentAsset_getCameraEntities")
private external fun FilaFilamentAsset_getCameraEntities(asset: NativePointer, entities: NativePointer)

@ExternalSymbolName("FilaFilamentAsset_getEntitiesByName")
private external fun FilaFilamentAsset_getEntitiesByName(asset: NativePointer, name: NativePointer, entities: NativePointer, maxCount: Int): Int

@ExternalSymbolName("FilaFilamentAsset_getEntitiesByPrefix")
private external fun FilaFilamentAsset_getEntitiesByPrefix(asset: NativePointer, prefix: NativePointer, entities: NativePointer, maxCount: Int): Int

@ExternalSymbolName("FilaFilamentAsset_getFirstEntityByName")
private external fun FilaFilamentAsset_getFirstEntityByName(asset: NativePointer, name: NativePointer): Int

@ExternalSymbolName("FilaFilamentAsset_getAssetInstanceCount")
private external fun FilaFilamentAsset_getAssetInstanceCount(asset: NativePointer): Int

@ExternalSymbolName("FilaFilamentAsset_getBoundingBox")
private external fun FilaFilamentAsset_getBoundingBox(asset: NativePointer, center: NativePointer, halfExtent: NativePointer)

@ExternalSymbolName("FilaFilamentAsset_getName")
private external fun FilaFilamentAsset_getName(asset: NativePointer, entity: Int): NativePointer

@ExternalSymbolName("FilaFilamentAsset_getExtras")
private external fun FilaFilamentAsset_getExtras(asset: NativePointer, entity: Int): NativePointer

@ExternalSymbolName("FilaFilamentAsset_getMorphTargetCountAt")
private external fun FilaFilamentAsset_getMorphTargetCountAt(asset: NativePointer, entity: Int): Int

@ExternalSymbolName("FilaFilamentAsset_getMorphTargetNameAt")
private external fun FilaFilamentAsset_getMorphTargetNameAt(asset: NativePointer, entity: Int, targetIndex: Int): NativePointer

@ExternalSymbolName("FilaFilamentAsset_getResourceUriCount")
private external fun FilaFilamentAsset_getResourceUriCount(asset: NativePointer): Int

@ExternalSymbolName("FilaFilamentAsset_getResourceUriAt")
private external fun FilaFilamentAsset_getResourceUriAt(asset: NativePointer, index: Int): NativePointer

@ExternalSymbolName("FilaFilamentAsset_releaseSourceData")
private external fun FilaFilamentAsset_releaseSourceData(asset: NativePointer)

@ExternalSymbolName("FilaFilamentAsset_getEngine")
private external fun FilaFilamentAsset_getEngine(asset: NativePointer): NativePointer

@ExternalSymbolName("FilaFilamentAsset_getInstance")
private external fun FilaFilamentAsset_getInstance(asset: NativePointer): NativePointer

@ExternalSymbolName("FilaFilamentAsset_getAssetInstanceAt")
internal external fun FilaFilamentAsset_getAssetInstanceAt(asset: NativePointer, index: Int): NativePointer
