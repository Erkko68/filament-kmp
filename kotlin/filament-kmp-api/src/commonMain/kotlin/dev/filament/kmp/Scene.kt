package dev.filament.kmp

/**
 * A Scene is a flat container of [RenderableManager] and [LightManager] components.
 */
expect class Scene {

    /**
     * @return the Skybox or null if none is set
     */
    fun getSkybox(): Skybox?

    /**
     * Sets the Skybox.
     */
    fun setSkybox(skybox: Skybox?)

    /**
     * @return the IndirectLight or null if none is set
     */
    fun getIndirectLight(): IndirectLight?

    /**
     * Sets the IndirectLight to use when rendering the Scene.
     */
    fun setIndirectLight(ibl: IndirectLight?)

    /**
     * Adds an Entity to the Scene.
     */
    fun addEntity(entity: Int)

    /**
     * Adds a list of entities to the Scene.
     */
    fun addEntities(entities: IntArray)

    /**
     * Removes an Entity from the Scene.
     */
    fun removeEntity(entity: Int)

    /**
     * @deprecated See removeEntity(int)
     */
    @Deprecated("Use removeEntity instead", ReplaceWith("removeEntity(entity)"))
    fun remove(entity: Int)

    /**
     * Removes a list of entities from the Scene.
     */
    fun removeEntities(entities: IntArray)

    /**
     * Returns the total number of Entities in the Scene, whether alive or not.
     */
    fun getEntityCount(): Int

    /**
     * Returns the number of active (alive) RenderableManager components in the Scene.
     */
    fun getRenderableCount(): Int

    /**
     * Returns the number of active (alive) LightManager components in the Scene.
     */
    fun getLightCount(): Int

    /**
     * Returns true if the given entity is in the Scene.
     */
    fun hasEntity(entity: Int): Boolean

    /**
     * Returns the list of all entities in the Scene.
     */
    fun getEntities(outArray: IntArray?): IntArray

    /**
     * Returns the list of all entities in the Scene in a newly allocated array.
     */
    fun getEntities(): IntArray

    interface EntityProcessor {
        fun process(entity: Int)
    }

    /**
     * Invokes user functor on each entity in the scene.
     */
    fun forEach(entityProcessor: EntityProcessor)
}
