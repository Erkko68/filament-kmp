package dev.filament.kmp

/**
 * A <code>Scene</code> is a flat container of {@link RenderableManager} and {@link LightManager}
 * components.
 * <br>
 * <p>A <code>Scene</code> doesn't provide a hierarchy of objects, i.e.: it's not a scene-graph.
 * However, it manages the list of objects to render and the list of lights. These can
 * be added or removed from a <code>Scene</code> at any time.
 * Moreover clients can use {@link TransformManager} to create a graph of transforms.</p>
 * <br>
 * <p>A {@link RenderableManager} component <b>must</b> be added to a <code>Scene</code> in order
 * to be rendered, and the <code>Scene</code> must be provided to a {@link View}.</p>
 *
 * <h1>Creation and Destruction</h1>
 *
 * A <code>Scene</code> is created using {@link Engine#createScene} and destroyed using
 * {@link Engine#destroyScene(Scene)}.
 *
 * @see View
 * @see LightManager
 * @see RenderableManager
 * @see TransformManager
 */
expect class Scene {
    /**
     * Returns whether this Scene wrapper currently points to a valid native instance.
     */
    val isValid: Boolean

    /**
     * @return the {@link Skybox} or <code>null</code> if none is set
     * @see #setSkybox(Skybox)
     */
    fun getSkybox(): Skybox?

    /**
     * Sets the {@link Skybox}.
     *
     * The {@link Skybox} is drawn last and covers all pixels not touched by geometry.
     *
     * @param skybox the {@link Skybox} to use to fill untouched pixels,
     *               or <code>null</code> to unset the {@link Skybox}.
     */
    fun setSkybox(skybox: Skybox?)

    /**
     * @return the {@link IndirectLight} or <code>null</code> if none is set
     * @see #setIndirectLight(IndirectLight)
     */
    fun getIndirectLight(): IndirectLight?

    /**
     * Sets the {@link IndirectLight} to use when rendering the <code>Scene</code>.
     *
     * Currently, a <code>Scene</code> may only have a single {@link IndirectLight}.
     * This call replaces the current {@link IndirectLight}.
     *
     * @param ibl the {@link IndirectLight} to use when rendering the <code>Scene</code>
     *            or <code>null</code> to unset.
     */
    fun setIndirectLight(ibl: IndirectLight?)

    /**
     * Adds an {@link Entity} to the <code>Scene</code>.
     *
     * @param entity the entity is ignored if it doesn't have a {@link RenderableManager} component
     *               or {@link LightManager} component.<br>
     *               A given {@link Entity} object can only be added once to a <code>Scene</code>.
     */
    fun addEntity(@Entity entity: Int)

    /**
     * Adds a list of entities to the <code>Scene</code>.
     *
     * @param entities array containing entities to add to the <code>Scene</code>.
     */
    fun addEntities(@Entity entities: IntArray)

    /**
     * Removes an {@link Entity} from the <code>Scene</code>.
     *
     * @param entity the {@link Entity} to remove from the <code>Scene</code>. If the specified
     *                   <code>entity</code> doesn't exist, this call is ignored.
     */
    fun removeEntity(@Entity entity: Int)

    /**
     * @deprecated See {@link #removeEntity(int)}
     */
    @Deprecated("See removeEntity(entity)")
    fun remove(@Entity entity: Int)

    /**
     * Removes a list of entities from the <code>Scene</code>.
     *
     * This is equivalent to calling remove in a loop.
     * If any of the specified entities do not exist in the scene, they are skipped.
     *
     * @param entities array containing entities to remove from the <code>Scene</code>.
     */
    fun removeEntities(@Entity entities: IntArray)

    /**
     * Returns the total number of Entities in the <code>Scene</code>, whether alive or not.
     *
     * @return the total number of Entities in the <code>Scene</code>.
     */
    fun getEntityCount(): Int

    /**
     * Returns the number of active (alive) {@link RenderableManager} components in the
     * <code>Scene</code>.
     */
    fun getRenderableCount(): Int

    /**
     * Returns the number of active (alive) {@link LightManager} components in the
     * <code>Scene</code>.
     */
    fun getLightCount(): Int

    /**
     * Returns true if the given entity is in the Scene.
     */
    fun hasEntity(@Entity entity: Int): Boolean

    /**
     * Returns the list of all entities in the Scene.
     * If outArray is provided and large enough,
     * it is used to store the list and returned, otherwise a new array is allocated and returned.
     * @param outArray an array to store the list of entities in the scene.
     * @return outArray if it was used or a newly allocated array.
     * @see #getEntityCount
     */
    fun getEntities(outArray: IntArray?): IntArray

    /**
     * Returns the list of all entities in the Scene in a newly allocated array.
     * @return an array containing the list of all entities in the scene.
     * @see #getEntityCount
     */
    fun getEntities(): IntArray

    interface EntityProcessor {
        fun process(@Entity entity: Int)
    }

    /**
     * Invokes user functor on each entity in the scene.
     *
     * It is not allowed to add or remove an entity from the scene within the functor.
     *
     * @param entityProcessor User provided functor called for each entity in the scene
     */
    fun forEach(entityProcessor: EntityProcessor)

    fun getNativeObject(): Long

    internal fun invalidate()
}

