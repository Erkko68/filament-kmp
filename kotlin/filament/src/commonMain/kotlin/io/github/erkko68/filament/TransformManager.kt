package io.github.erkko68.filament

import io.github.erkko68.filament.interop.*

/**
 * TransformManager allows managing transforms (4x4 matrices) for entities.
 *
 * Entities can have a transform component that defines their position, rotation, and scale in
 * world space. Transforms can be organized in a hierarchy where each entity can have a parent,
 * allowing relative transformations.
 *
 * The TransformManager maintains the transform hierarchy and automatically computes world
 * transforms from local transforms and parent transforms.
 *
 * @see EntityManager, Scene
 */
class TransformManager @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Checks if an entity has a transform component.
     *
     * @param entity The entity to check
     * @return true if the entity has a transform component, false otherwise
     */
    fun hasComponent(entity: Entity): Boolean = FilaTransformManager_hasComponent(nativeHandle, entity)
    /**
     * Gets the transform instance for an entity.
     *
     * @param entity The entity
     * @return The transform instance for this entity
     */
    fun getInstance(entity: Entity): EntityInstance = FilaTransformManager_getInstance(nativeHandle, entity)
    
    /**
     * Creates a transform component for an entity with identity transform.
     *
     * @param entity The entity to add a transform to
     * @return The newly created transform instance
     */
    fun create(entity: Entity): EntityInstance = FilaTransformManager_create(nativeHandle, entity)
    
    /**
     * Creates a transform component for an entity with a local transform and parent.
     *
     * @param entity The entity to add a transform to
     * @param parent The parent transform instance (use NULL_ENTITY for root)
     * @param localTransform The local transform as a 4x4 matrix in row-major order (or null)
     * @return The newly created transform instance
     */
    fun create(entity: Entity, parent: EntityInstance, localTransform: FloatArray?): EntityInstance {
        return if (localTransform != null) {
            localTransform.usePinned { 
                FilaTransformManager_createWithParent(nativeHandle, entity, parent, it)
            }
        } else {
            FilaTransformManager_createWithParent(nativeHandle, entity, parent, NullPointer)
        }
    }
        
    /**
     * Creates a transform component for an entity with a local transform and parent.
     *
     * @param entity The entity to add a transform to
     * @param parent The parent transform instance (use NULL_ENTITY for root)
     * @param localTransform The local transform as a 4x4 matrix in row-major order (or null)
     * @return The newly created transform instance
     */
    fun create(entity: Entity, parent: EntityInstance, localTransform: DoubleArray?): EntityInstance {
        return if (localTransform != null) {
            localTransform.usePinned { 
                FilaTransformManager_createWithParentFp64(nativeHandle, entity, parent, it)
            }
        } else {
            FilaTransformManager_createWithParentFp64(nativeHandle, entity, parent, NullPointer)
        }
    }
    
    /**
     * Destroys the transform component for an entity.
     *
     * @param entity The entity whose transform to destroy
     */
    fun destroy(entity: Entity) = FilaTransformManager_destroy(nativeHandle, entity)
    
    /**
     * Sets the parent of a transform.
     *
     * @param instance The transform instance
     * @param newParent The new parent transform instance
     */
    fun setParent(instance: EntityInstance, newParent: EntityInstance) = 
        FilaTransformManager_setParent(nativeHandle, instance, newParent)
        
    /**
     * Gets the parent entity of a transform.
     *
     * @param instance The transform instance
     * @return The parent entity (or NULL_ENTITY if this is a root transform)
     */
    fun getParent(instance: EntityInstance): Entity = FilaTransformManager_getParent(nativeHandle, instance)
    
    /**
     * Gets the number of child transforms.
     *
     * @param instance The transform instance
     * @return The number of direct children
     */
    fun getChildCount(instance: EntityInstance): Int = FilaTransformManager_getChildCount(nativeHandle, instance)
    
    /**
     * Gets the child entities of a transform.
     *
     * @param instance The transform instance
     * @param out Optional array to fill with child entity IDs
     * @return Array of child entity IDs
     */
    fun getChildren(instance: EntityInstance, out: IntArray? = null): IntArray {
        val count = getChildCount(instance)
        val result = out ?: IntArray(count)
        if (count > 0) {
            result.usePinned { 
                FilaTransformManager_getChildren(nativeHandle, instance, it, count)
            }
        }
        return result
    }
    
    /**
     * Sets the local transform for a transform instance.
     *
     * @param instance The transform instance
     * @param localTransform A 4x4 matrix in row-major order
     */
    fun setTransform(instance: EntityInstance, localTransform: FloatArray) {
        localTransform.usePinned { 
            FilaTransformManager_setTransform(nativeHandle, instance, it)
        }
    }
        
    /**
     * Sets the local transform for a transform instance.
     *
     * @param instance The transform instance
     * @param localTransform A 4x4 matrix in row-major order
     */
    fun setTransform(instance: EntityInstance, localTransform: DoubleArray) {
        localTransform.usePinned { 
            FilaTransformManager_setTransformFp64(nativeHandle, instance, it)
        }
    }
    
    /**
     * Gets the local transform for a transform instance.
     *
     * @param instance The transform instance
     * @param out Optional array to fill with the local transform (or null)
     * @return A 4x4 matrix in row-major order
     */
    fun getTransform(instance: EntityInstance, out: FloatArray? = null): FloatArray {
        val result = out ?: FloatArray(16)
        result.usePinned { 
            FilaTransformManager_getTransform(nativeHandle, instance, it)
        }
        return result
    }
        
    /**
     * Gets the local transform for a transform instance.
     *
     * @param instance The transform instance
     * @param out Optional array to fill with the local transform (or null)
     * @return A 4x4 matrix in row-major order
     */
    fun getTransform(instance: EntityInstance, out: DoubleArray? = null): DoubleArray {
        val result = out ?: DoubleArray(16)
        result.usePinned { 
            FilaTransformManager_getTransformFp64(nativeHandle, instance, it)
        }
        return result
    }
    
    /**
     * Gets the world transform for a transform instance (accounting for parent transforms).
     *
     * @param instance The transform instance
     * @param out Optional array to fill with the world transform (or null)
     * @return A 4x4 matrix in row-major order
     */
    fun getWorldTransform(instance: EntityInstance, out: FloatArray? = null): FloatArray {
        val result = out ?: FloatArray(16)
        result.usePinned { 
            FilaTransformManager_getWorldTransform(nativeHandle, instance, it)
        }
        return result
    }
        
    /**
     * Gets the world transform for a transform instance (accounting for parent transforms).
     *
     * @param instance The transform instance
     * @param out Optional array to fill with the world transform (or null)
     * @return A 4x4 matrix in row-major order
     */
    fun getWorldTransform(instance: EntityInstance, out: DoubleArray? = null): DoubleArray {
        val result = out ?: DoubleArray(16)
        result.usePinned { 
            FilaTransformManager_getWorldTransformFp64(nativeHandle, instance, it)
        }
        return result
    }
    
    /**
     * Opens a local transform transaction.
     *
     * Allows multiple transform updates to be batched together for efficiency. Call
     * commitLocalTransformTransaction() to commit the changes.
     */
    fun openLocalTransformTransaction() = FilaTransformManager_openLocalTransformTransaction(nativeHandle)
    /**
     * Commits a local transform transaction.
     *
     * Must be called after openLocalTransformTransaction() to apply batched changes.
     */
    fun commitLocalTransformTransaction() = FilaTransformManager_commitLocalTransformTransaction(nativeHandle)
    
    /**
     * Enables or disables accurate translations (high precision translation for large worlds).
     *
     * When enabled, allows for more precise transforms for entities far from the origin.
     * This may have a small performance cost. Default: false.
     */
    var isAccurateTranslationsEnabled: Boolean
        get() = FilaTransformManager_isAccurateTranslationsEnabled(nativeHandle)
        set(value) { FilaTransformManager_setAccurateTranslationsEnabled(nativeHandle, value) }
}

@ExternalSymbolName("FilaTransformManager_commitLocalTransformTransaction")
private external fun FilaTransformManager_commitLocalTransformTransaction(tm: NativePointer)

@ExternalSymbolName("FilaTransformManager_create")
private external fun FilaTransformManager_create(tm: NativePointer, entity: Int): Int

@ExternalSymbolName("FilaTransformManager_createWithParent")
private external fun FilaTransformManager_createWithParent(tm: NativePointer, entity: Int, parent: Int, localTransform: NativePointer): Int

@ExternalSymbolName("FilaTransformManager_createWithParentFp64")
private external fun FilaTransformManager_createWithParentFp64(tm: NativePointer, entity: Int, parent: Int, localTransform: NativePointer): Int

@ExternalSymbolName("FilaTransformManager_destroy")
private external fun FilaTransformManager_destroy(tm: NativePointer, entity: Int)

@ExternalSymbolName("FilaTransformManager_getChildCount")
private external fun FilaTransformManager_getChildCount(tm: NativePointer, instance: Int): Int

@ExternalSymbolName("FilaTransformManager_getChildren")
private external fun FilaTransformManager_getChildren(tm: NativePointer, instance: Int, outEntities: NativePointer, count: Int)

@ExternalSymbolName("FilaTransformManager_getInstance")
private external fun FilaTransformManager_getInstance(tm: NativePointer, entity: Int): Int

@ExternalSymbolName("FilaTransformManager_getParent")
private external fun FilaTransformManager_getParent(tm: NativePointer, instance: Int): Int

@ExternalSymbolName("FilaTransformManager_getTransform")
private external fun FilaTransformManager_getTransform(tm: NativePointer, instance: Int, out: NativePointer)

@ExternalSymbolName("FilaTransformManager_getTransformFp64")
private external fun FilaTransformManager_getTransformFp64(tm: NativePointer, instance: Int, out: NativePointer)

@ExternalSymbolName("FilaTransformManager_getWorldTransform")
private external fun FilaTransformManager_getWorldTransform(tm: NativePointer, instance: Int, out: NativePointer)

@ExternalSymbolName("FilaTransformManager_getWorldTransformFp64")
private external fun FilaTransformManager_getWorldTransformFp64(tm: NativePointer, instance: Int, out: NativePointer)

@ExternalSymbolName("FilaTransformManager_hasComponent")
private external fun FilaTransformManager_hasComponent(tm: NativePointer, entity: Int): Boolean

@ExternalSymbolName("FilaTransformManager_isAccurateTranslationsEnabled")
private external fun FilaTransformManager_isAccurateTranslationsEnabled(tm: NativePointer): Boolean

@ExternalSymbolName("FilaTransformManager_openLocalTransformTransaction")
private external fun FilaTransformManager_openLocalTransformTransaction(tm: NativePointer)

@ExternalSymbolName("FilaTransformManager_setAccurateTranslationsEnabled")
private external fun FilaTransformManager_setAccurateTranslationsEnabled(tm: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaTransformManager_setParent")
private external fun FilaTransformManager_setParent(tm: NativePointer, instance: Int, newParent: Int)

@ExternalSymbolName("FilaTransformManager_setTransform")
private external fun FilaTransformManager_setTransform(tm: NativePointer, instance: Int, matrix: NativePointer)

@ExternalSymbolName("FilaTransformManager_setTransformFp64")
private external fun FilaTransformManager_setTransformFp64(tm: NativePointer, instance: Int, matrix: NativePointer)
