package dev.filament.kmp

/**
 * <code>TransformManager</code> is used to add transform components to entities.
 *
 * <p>A transform component gives an entity a position and orientation in space in the coordinate
 * space of its parent transform. The <code>TransformManager</code> takes care of computing the
 * world-space transform of each component (i.e. its transform relative to the root).</p>
 *
 * <h1>Creation and destruction</h1>
 *
 * A transform component is created using {@link TransformManager#create} and destroyed by calling
 * {@link TransformManager#destroy}.
 */
expect class TransformManager {
    /**
     * Returns whether a particular {@link Entity} is associated with a component of this
     * <code>TransformManager</code>
     *
     * @param entity an {@link Entity}
     * @return true if this {@link Entity} has a component associated with this manager
     */
    fun hasComponent(@Entity entity: Int): Boolean

    /**
     * Gets an {@link EntityInstance} representing the transform component associated with the
     * given {@link Entity}.
     *
     * @param entity an {@link Entity}
     * @return an {@link EntityInstance}, which represents the transform component associated with
     * the {@link Entity} <code>entity</code>
     * @see #hasComponent
     */
    @EntityInstance
    fun getInstance(@Entity entity: Int): Int

    /**
     * Enables or disable the accurate translation mode. Disabled by default.
     *
     * When accurate translation mode is active, the translation component of all transforms is
     * maintained at double precision. This is only useful if the mat4 version of setTransform()
     * is used, as well as getTransformAccurate().
     *
     * @param enable true to enable the accurate translation mode, false to disable.
     *
     * @see #isAccurateTranslationsEnabled
     * @see #create(int, int, double[])
     * @see #setTransform(int, double[])
     * @see #getTransform(int, double[])
     * @see #getWorldTransform(int, double[])
     */
    fun setAccurateTranslationsEnabled(enable: Boolean)

    /**
     * Returns whether the high precision translation mode is active.
     *
     * @return true if accurate translations mode is active, false otherwise
     * @see #setAccurateTranslationsEnabled
     */
    fun isAccurateTranslationsEnabled(): Boolean

    /**
     * Creates a transform component and associates it with the given entity. The component is
     * initialized with the identity transform.
     * If this component already exists on the given entity, it is first
     * destroyed as if {@link #destroy} was called.
     *
     * @param entity an {@link Entity} to associate a transform component to.
     * @see #destroy
     */
    @EntityInstance
    fun create(@Entity entity: Int): Int

    /**
     * Creates a transform component with a parent and associates it with the given entity.
     * If this component already exists on the given entity, it is first
     * destroyed as if {@link #destroy} was called.
     *
     * @param entity         an {@link Entity} to associate a transform component to.
     * @param parent         the  {@link EntityInstance} of the parent transform
     * @param localTransform the transform, relative to the parent, to initialize the transform
     *                       component with.
     * @see #destroy
     */
    @EntityInstance
    fun create(@Entity entity: Int, @EntityInstance parent: Int, localTransform: FloatArray?): Int

    /**
     * Creates a transform component with a parent and associates it with the given entity.
     * If this component already exists on the given entity, it is first
     * destroyed as if {@link #destroy} was called.
     *
     * @param entity         an {@link Entity} to associate a transform component to.
     * @param parent         the  {@link EntityInstance} of the parent transform
     * @param localTransform the transform, relative to the parent, to initialize the transform
     *                       component with.
     * @see #destroy
     */
    @EntityInstance
    fun create(@Entity entity: Int, @EntityInstance parent: Int, localTransform: DoubleArray?): Int

    /**
     * Destroys this component from the given entity, children are orphaned.
     *
     * @param entity an {@link Entity}.
     *               If this transform had children, these are orphaned, which means their local
     *               transform becomes a world transform. Usually it's nonsensical.
     *               It's recommended to make sure that a destroyed transform doesn't have children.
     * @see #create
     */
    fun destroy(@Entity entity: Int)

    /**
     * Re-parents an entity to a new one.
     *
     * @param i         the {@link EntityInstance} of the transform component to re-parent
     * @param newParent the {@link EntityInstance} of the new parent transform.
     *                  It is an error to re-parent an entity to a descendant and will cause
     *                  undefined behaviour.
     * @see #getInstance
     */
    fun setParent(@EntityInstance i: Int, @EntityInstance newParent: Int)

    /**
     * Returns the actual parent entity of an {@link EntityInstance} originally defined
     * by {@link #setParent(int, int)}.
     *
     * @param i the {@link EntityInstance} of the transform component to get the parent from.
     * @return the parent {@link Entity}.
     * @see #getInstance
     */
    @Entity
    fun getParent(@EntityInstance i: Int): Int

    /**
     * Returns the number of children of an {@link EntityInstance}.
     *
     * @param i the {@link EntityInstance} of the transform component to query.
     * @return The number of children of the queried component.
     */
    fun getChildCount(@EntityInstance i: Int): Int

    /**
     * Gets a list of children for a transform component.
     *
     * @param i           the {@link EntityInstance} of the transform component to get the children
     *                    from.
     * @param outEntities array to receive the result sized to the maximum number of children to
     *                    retrieve. If <code>null</code> is given, a new suitable array sized to
     *                    {@link #getChildCount(int)} is allocated.
     * @return Array of retrieved children {@link Entity}.
     */
    @Entity
    fun getChildren(@EntityInstance i: Int, outEntities: IntArray?): IntArray

    /**
     * Sets a local transform of a transform component.
     * <p>This operation can be slow if the hierarchy of transform is too deep, and this
     * will be particularly bad when updating a lot of transforms. In that case,
     * consider using {@link #openLocalTransformTransaction} / {@link #commitLocalTransformTransaction}.</p>
     *
     * @param i              the {@link EntityInstance} of the transform component to set the local
     *                       transform to.
     * @param localTransform the local transform (i.e. relative to the parent).
     * @see #getTransform
     */
    fun setTransform(@EntityInstance i: Int, localTransform: FloatArray)

    /**
     * Sets a local transform of a transform component.
     * <p>This operation can be slow if the hierarchy of transform is too deep, and this
     * will be particularly bad when updating a lot of transforms. In that case,
     * consider using {@link #openLocalTransformTransaction} / {@link #commitLocalTransformTransaction}.</p>
     *
     * @param i              the {@link EntityInstance} of the transform component to set the local
     *                       transform to.
     * @param localTransform the local transform (i.e. relative to the parent).
     * @see #getTransform(int, double[])
     * @see #getWorldTransform(int, double[])
     */
    fun setTransform(@EntityInstance i: Int, localTransform: DoubleArray)

    /**
     * Returns the local transform of a transform component.
     *
     * @param i                 the {@link EntityInstance} of the transform component to query the
     *                          local transform from.
     * @param outLocalTransform a 16 <code>float</code> array to receive the result.
     *                          If <code>null</code> is given,  a new suitable array is allocated.
     * @return the local transform of the component (i.e. relative to the parent). This always
     * returns the value set by setTransform().
     * @see #setTransform
     */
    fun getTransform(@EntityInstance i: Int, outLocalTransform: FloatArray?): FloatArray

    /**
     * Returns the local transform of a transform component.
     *
     * @param i                 the {@link EntityInstance} of the transform component to query the
     *                          local transform from.
     * @param outLocalTransform a 16 <code>float</code> array to receive the result.
     *                          If <code>null</code> is given,  a new suitable array is allocated.
     * @return the local transform of the component (i.e. relative to the parent). This always
     * returns the value set by setTransform().
     * @see #setTransform
     */
    fun getTransform(@EntityInstance i: Int, outLocalTransform: DoubleArray?): DoubleArray

    /**
     * Returns the world transform of a transform component.
     */
    fun getWorldTransform(@EntityInstance i: Int, outWorldTransform: FloatArray?): FloatArray

    /**
     * Returns the world transform of a transform component.
     *
     * @param i                 the {@link EntityInstance} of the transform component to query the
     *                          world transform from.
     * @param outWorldTransform a 16 <code>float</code> array to receive the result.
     *                          If <code>null</code> is given,  a new suitable array is allocated
     * @return The world transform of the component (i.e. relative to the root). This is the
     * composition of this component's local transform with its parent's world transform.
     * @see #setTransform
     */
    fun getWorldTransform(@EntityInstance i: Int, outWorldTransform: DoubleArray?): DoubleArray

    /**
     * Opens a local transform transaction.
     * During a transaction, {@link #getWorldTransform} can
     * return an invalid transform until {@link #commitLocalTransformTransaction} is called.
     * However, {@link #setTransform} will perform significantly better and in constant time.
     *
     * <p>If the local transform transaction is already open, this is a no-op.</p>
     *
     * @see #commitLocalTransformTransaction
     * @see #setTransform
     */
    fun openLocalTransformTransaction()

    /**
     * Commits the currently open local transform transaction.
     *
     * <p>If the local transform transaction is not open, this is a no-op.</p>
     *
     * @see #openLocalTransformTransaction
     * @see #setTransform
     */
    fun commitLocalTransformTransaction()

    fun getNativeObject(): Long
}

