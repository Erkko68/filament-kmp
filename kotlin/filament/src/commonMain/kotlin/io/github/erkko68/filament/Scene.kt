package io.github.erkko68.filament

import io.github.erkko68.filament.interop.ExternalSymbolName
import io.github.erkko68.filament.interop.NativePointer
import io.github.erkko68.filament.interop.NullPointer
import io.github.erkko68.filament.interop.interopScope

/**
 * A Scene is a collection of Renderables and Lights to be rendered together.
 *
 * A Scene must be associated with a View to be rendered. Renderables, Lights, Skyboxes, and
 * IndirectLight can be added to and removed from a Scene. Scenes are relatively lightweight
 * objects that mainly manage entity membership.
 *
 * @see View, Renderer, RenderableManager, LightManager
 */
class Scene @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Sets the skybox for this scene.
     *
     * A scene can have at most one skybox. The skybox is rendered last (after all renderables)
     * and doesn't participate in lighting or shadow receiving.
     */
    var skybox: Skybox? = null
        set(value) {
            field = value
            FilaScene_setSkybox(nativeHandle, value?.nativeHandle ?: NullPointer)
        }

    /**
     * Sets the indirect light (IBL) for this scene.
     *
     * The indirect light provides environmental lighting (irradiance and reflections). A scene
     * can have at most one indirect light. If multiple IndirectLight objects exist, only the one
     * most recently set is active.
     */
    var indirectLight: IndirectLight? = null
        set(value) {
            field = value
            FilaScene_setIndirectLight(nativeHandle, value?.nativeHandle ?: NullPointer)
        }

    /**
     * Adds a single entity to this scene.
     *
     * A Renderable entity added to a Scene can be rendered when the Scene is rendered.
     * Adding the same entity twice has no effect.
     *
     * @param entity The entity to add
     */
    fun addEntity(entity: Entity) = FilaScene_addEntity(nativeHandle, entity)

    /**
     * Adds multiple entities to this scene.
     *
     * @param entities Array of entity IDs to add
     */
    fun addEntities(entities: IntArray) = interopScope {
        FilaScene_addEntities(nativeHandle, toInterop(entities), entities.size)
    }

    /**
     * Removes a single entity from this scene.
     *
     * @param entity The entity to remove
     */
    fun removeEntity(entity: Entity) = FilaScene_remove(nativeHandle, entity)

    /**
     * Removes a single entity from this scene.
     *
     * This is a synonym for removeEntity(). Both have the same effect.
     *
     * @param entity The entity to remove
     */
    fun remove(entity: Entity) = FilaScene_remove(nativeHandle, entity)

    /**
     * Removes multiple entities from this scene.
     *
     * @param entities Array of entity IDs to remove
     */
    fun removeEntities(entities: IntArray) = interopScope {
        FilaScene_removeEntities(nativeHandle, toInterop(entities), entities.size)
    }

    /**
     * Gets the number of entities in this scene.
     *
     * @return Number of entities (renderables + lights)
     */
    val entityCount: Int get() = FilaScene_getEntityCount(nativeHandle)

    /**
     * Gets the number of renderable entities in this scene.
     *
     * @return Number of renderables
     */
    val renderableCount: Int get() = FilaScene_getRenderableCount(nativeHandle)

    /**
     * Gets the number of light entities in this scene.
     *
     * @return Number of lights
     */
    val lightCount: Int get() = FilaScene_getLightCount(nativeHandle)

    /**
     * Tests whether an entity is in this scene.
     *
     * @param entity The entity to test
     * @return true if the entity is in this scene, false otherwise
     */
    fun hasEntity(entity: Entity): Boolean = FilaScene_hasEntity(nativeHandle, entity)

    /**
     * Gets all entities in this scene.
     *
     * @param out Optional array to fill with entity IDs; a new array is allocated if null.
     * @return Array of all entity IDs in the scene
     */
    fun getEntities(out: IntArray? = null): IntArray {
        val count = entityCount
        val result = if (out != null && out.size >= count) out else IntArray(count)
        if (count > 0) interopScope {
            val ptr = toInterop(result)
            FilaScene_getEntities(nativeHandle, ptr, count)
            ptr.fromInterop(result)
        }
        return result
    }

    /**
     * Iterates over all entities in this scene.
     *
     * @param block Lambda function to call for each entity
     */
    fun forEach(block: (Entity) -> Unit) = getEntities().forEach(block)
}

@ExternalSymbolName("FilaScene_setSkybox")
private external fun FilaScene_setSkybox(scene: NativePointer, skybox: NativePointer)

@ExternalSymbolName("FilaScene_setIndirectLight")
private external fun FilaScene_setIndirectLight(scene: NativePointer, indirectLight: NativePointer)

@ExternalSymbolName("FilaScene_addEntity")
private external fun FilaScene_addEntity(scene: NativePointer, entity: Int)

@ExternalSymbolName("FilaScene_addEntities")
private external fun FilaScene_addEntities(scene: NativePointer, entities: NativePointer, count: Int)

@ExternalSymbolName("FilaScene_remove")
private external fun FilaScene_remove(scene: NativePointer, entity: Int)

@ExternalSymbolName("FilaScene_removeEntities")
private external fun FilaScene_removeEntities(scene: NativePointer, entities: NativePointer, count: Int)

@ExternalSymbolName("FilaScene_getEntityCount")
private external fun FilaScene_getEntityCount(scene: NativePointer): Int

@ExternalSymbolName("FilaScene_getRenderableCount")
private external fun FilaScene_getRenderableCount(scene: NativePointer): Int

@ExternalSymbolName("FilaScene_getLightCount")
private external fun FilaScene_getLightCount(scene: NativePointer): Int

@ExternalSymbolName("FilaScene_hasEntity")
private external fun FilaScene_hasEntity(scene: NativePointer, entity: Int): Boolean

@ExternalSymbolName("FilaScene_getEntities")
private external fun FilaScene_getEntities(scene: NativePointer, out: NativePointer, length: Int)
