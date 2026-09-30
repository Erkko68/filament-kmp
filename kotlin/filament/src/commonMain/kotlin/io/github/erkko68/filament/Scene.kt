package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.NativePointer
import io.github.erkko68.filament.interop.NullPointer
import io.github.erkko68.filament.interop.interopScope
import io.github.erkko68.filament.interop.usePinned

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
    fun remove(entity: Entity) = FilaScene_remove(nativeHandle, entity)

    /**
     * Removes multiple entities from this scene.
     *
     * @param entities Array of entity IDs to remove
     */
    fun removeEntities(entities: IntArray) = interopScope {
        FilaScene_removeEntities(nativeHandle, toInterop(entities), entities.size)
    }

    /** Removes all Renderables and Lights from this scene. */
    fun removeAllEntities() = FilaScene_removeAllEntities(nativeHandle)

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
     * Invokes [functor] on every entity in this scene.
     *
     * @param functor Function called with each entity
     */
    fun forEach(functor: (Entity) -> Unit) {
        val count = FilaScene_forEach(nativeHandle, NullPointer, 0)
        IntArray(count).also { out -> out.usePinned { FilaScene_forEach(nativeHandle, it, count) } }.forEach(functor)
    }
}
