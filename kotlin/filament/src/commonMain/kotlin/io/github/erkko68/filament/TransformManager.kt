package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/**
 * TransformManager allows managing transforms (4x4 matrices) for entities.
 *
 * Entities can have a transform component that defines their position, rotation, and scale in
 * world space. Transforms can be organized in a hierarchy where each entity can have a parent,
 * allowing relative transformations.
 *
 * The TransformManager maintains the transform hierarchy and automatically computes world
 * transforms from local transforms and parent transforms. Matrices are 16 values in column-major order.
 *
 * @see EntityManager, Scene
 */
class TransformManager @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Returns whether a particular Entity is associated with a component of this TransformManager.
     *
     * @param e An Entity.
     * @return true if this Entity has a component associated with this manager.
     */
    fun hasComponent(e: Entity): Boolean = FilaTransformManager_hasComponent(nativeHandle, e)

    /**
     * Gets an Instance representing the transform component associated with the given Entity.
     *
     * @param e An Entity.
     * @return The component's Instance, or 0 if it has none.
     */
    fun getInstance(e: Entity): EntityInstance = FilaTransformManager_getInstance(nativeHandle, e)

    /** The number of components in this manager. */
    val componentCount: Int get() = FilaTransformManager_getComponentCount(nativeHandle)

    /** Whether this manager has no components. */
    fun empty(): Boolean = FilaTransformManager_empty(nativeHandle)

    /** Retrieves the Entity of the component from its Instance [i]. */
    fun getEntity(i: EntityInstance): Entity = FilaTransformManager_getEntity(nativeHandle, i)

    /** All the entities managed by this manager, in no particular order. */
    val entities: IntArray get() = readInts(FilaTransformManager_getEntities(nativeHandle), componentCount)

    /**
     * Enables or disables the accurate translation mode (disabled by default).
     *
     * The translation component of all transforms is then kept at double precision. This is only
     * useful with the DoubleArray [setTransform] and [getTransformAccurate].
     */
    var isAccurateTranslationsEnabled: Boolean
        get() = FilaTransformManager_isAccurateTranslationsEnabled(nativeHandle)
        set(value) { FilaTransformManager_setAccurateTranslationsEnabled(nativeHandle, value) }

    /**
     * Creates a transform component and associates it with the given entity. If this component
     * already exists on the entity, it is first destroyed as if [destroy] was called.
     *
     * @param entity An Entity to associate a transform component to.
     * @param parent The Instance of the parent transform, or 0 if no parent.
     */
    fun create(entity: Entity, parent: EntityInstance = 0) = FilaTransformManager_create(nativeHandle, entity, parent)

    /**
     * Creates a transform component, initialized with [localTransform] (relative to the parent).
     *
     * @param entity An Entity to associate a transform component to.
     * @param parent The Instance of the parent transform, or 0 if no parent.
     * @param localTransform The transform to initialize the transform component with.
     */
    fun create(entity: Entity, parent: EntityInstance, localTransform: FloatArray) =
        localTransform.usePinned { FilaTransformManager_create_mat4f(nativeHandle, entity, parent, it) }

    /** Double-precision [create]. */
    fun create(entity: Entity, parent: EntityInstance, localTransform: DoubleArray) =
        localTransform.usePinned { FilaTransformManager_create_mat4(nativeHandle, entity, parent, it) }

    /**
     * Destroys this component from the given entity; its children are orphaned.
     *
     * @param e An entity.
     */
    fun destroy(e: Entity) = FilaTransformManager_destroy(nativeHandle, e)

    /**
     * Re-parents an entity to a new one. Re-parenting to a descendant is undefined behaviour.
     *
     * @param i The instance of the transform component to re-parent
     * @param newParent The instance of the new parent transform
     */
    fun setParent(i: EntityInstance, newParent: EntityInstance) = FilaTransformManager_setParent(nativeHandle, i, newParent)

    /**
     * Returns the parent of a transform component, or NULL_ENTITY if it has none.
     *
     * @param i The instance of the transform component to query.
     */
    fun getParent(i: EntityInstance): Entity = FilaTransformManager_getParent(nativeHandle, i)

    /**
     * Returns the number of children of a transform component.
     *
     * @param i The instance of the transform component to query.
     */
    fun getChildCount(i: EntityInstance): Int = FilaTransformManager_getChildCount(nativeHandle, i)

    /**
     * Gets up to `children.size` children of a transform component.
     *
     * @param i The instance of the transform component to query.
     * @param children Receives the children.
     * @return The number of children written.
     */
    fun getChildren(i: EntityInstance, children: IntArray): Int =
        children.usePinned { FilaTransformManager_getChildren(nativeHandle, i, it, children.size) }

    /**
     * Sets a local transform of a transform component (relative to the parent).
     *
     * @param ci The instance of the transform component to set the local transform to.
     * @param localTransform The local transform.
     */
    fun setTransform(ci: EntityInstance, localTransform: FloatArray) =
        localTransform.usePinned { FilaTransformManager_setTransform_mat4f(nativeHandle, ci, it) }

    /** Double-precision [setTransform]. */
    fun setTransform(ci: EntityInstance, localTransform: DoubleArray) =
        localTransform.usePinned { FilaTransformManager_setTransform_mat4(nativeHandle, ci, it) }

    /**
     * Returns the local transform of a transform component (relative to the parent).
     *
     * @param ci The instance of the transform component to query.
     * @param out Receives the transform; a new array by default.
     */
    fun getTransform(ci: EntityInstance, out: FloatArray = FloatArray(16)): FloatArray =
        out.also { o -> o.usePinned { FilaTransformManager_getTransform(nativeHandle, ci, it) } }

    /** Double-precision [getTransform]. */
    fun getTransformAccurate(ci: EntityInstance, out: DoubleArray = DoubleArray(16)): DoubleArray =
        out.also { o -> o.usePinned { FilaTransformManager_getTransformAccurate(nativeHandle, ci, it) } }

    /**
     * Returns the world transform of a transform component.
     *
     * @param ci The instance of the transform component to query.
     * @param out Receives the transform; a new array by default.
     */
    fun getWorldTransform(ci: EntityInstance, out: FloatArray = FloatArray(16)): FloatArray =
        out.also { o -> o.usePinned { FilaTransformManager_getWorldTransform(nativeHandle, ci, it) } }

    /** Double-precision [getWorldTransform]. */
    fun getWorldTransformAccurate(ci: EntityInstance, out: DoubleArray = DoubleArray(16)): DoubleArray =
        out.also { o -> o.usePinned { FilaTransformManager_getWorldTransformAccurate(nativeHandle, ci, it) } }

    /**
     * Opens a local transform transaction: [setTransform] then doesn't update world transforms
     * until [commitLocalTransformTransaction], which is faster when setting many of them.
     */
    fun openLocalTransformTransaction() = FilaTransformManager_openLocalTransformTransaction(nativeHandle)

    /** Commits the local transform transaction and updates all world transforms. */
    fun commitLocalTransformTransaction() = FilaTransformManager_commitLocalTransformTransaction(nativeHandle)
}
