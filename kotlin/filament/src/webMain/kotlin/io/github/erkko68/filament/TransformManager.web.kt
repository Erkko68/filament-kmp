package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class TransformManager @InternalFilamentApi constructor(internal var nativeHandle: Int) {
    actual fun hasComponent(entity: Entity): Boolean = FilaTransformManager_hasComponent(nativeHandle, entity)
    actual fun getInstance(entity: Entity): EntityInstance = FilaTransformManager_getInstance(nativeHandle, entity).toInt()
    
    actual fun create(entity: Entity): EntityInstance = FilaTransformManager_create(nativeHandle, entity).toInt()
    
    actual fun create(entity: Entity, parent: EntityInstance, localTransform: FloatArray?): EntityInstance {
        return if (localTransform != null) {
            localTransform.usePinned { 
                FilaTransformManager_createWithParent(nativeHandle, entity, parent, it).toInt()
            }
        } else {
            FilaTransformManager_createWithParent(nativeHandle, entity, parent, 0).toInt()
        }
    }
        
    actual fun create(entity: Entity, parent: EntityInstance, localTransform: DoubleArray?): EntityInstance {
        return if (localTransform != null) {
            localTransform.usePinned { 
                FilaTransformManager_createWithParentFp64(nativeHandle, entity, parent, it).toInt()
            }
        } else {
            FilaTransformManager_createWithParentFp64(nativeHandle, entity, parent, 0).toInt()
        }
    }
    
    actual fun destroy(entity: Entity) = FilaTransformManager_destroy(nativeHandle, entity)
    
    actual fun setParent(instance: EntityInstance, newParent: EntityInstance) = 
        FilaTransformManager_setParent(nativeHandle, instance, newParent)
        
    actual fun getParent(instance: EntityInstance): Entity = FilaTransformManager_getParent(nativeHandle, instance).toInt()
    
    actual fun getChildCount(instance: EntityInstance): Int = FilaTransformManager_getChildCount(nativeHandle, instance).toInt()
    
    actual fun getChildren(instance: EntityInstance, out: IntArray?): IntArray {
        val count = getChildCount(instance)
        val result = out ?: IntArray(count)
        if (count > 0) {
            result.usePinned { 
                FilaTransformManager_getChildren(nativeHandle, instance, it, count)
            }
        }
        return result
    }
    
    actual fun setTransform(instance: EntityInstance, localTransform: FloatArray) {
        localTransform.usePinned { 
            FilaTransformManager_setTransform(nativeHandle, instance, it)
        }
    }
        
    actual fun setTransform(instance: EntityInstance, localTransform: DoubleArray) {
        localTransform.usePinned { 
            FilaTransformManager_setTransformFp64(nativeHandle, instance, it)
        }
    }
    
    actual fun getTransform(instance: EntityInstance, out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        result.usePinned { 
            FilaTransformManager_getTransform(nativeHandle, instance, it)
        }
        return result
    }
        
    actual fun getTransform(instance: EntityInstance, out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        result.usePinned { 
            FilaTransformManager_getTransformFp64(nativeHandle, instance, it)
        }
        return result
    }
    
    actual fun getWorldTransform(instance: EntityInstance, out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        result.usePinned { 
            FilaTransformManager_getWorldTransform(nativeHandle, instance, it)
        }
        return result
    }
        
    actual fun getWorldTransform(instance: EntityInstance, out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        result.usePinned { 
            FilaTransformManager_getWorldTransformFp64(nativeHandle, instance, it)
        }
        return result
    }
    
    actual fun openLocalTransformTransaction() = FilaTransformManager_openLocalTransformTransaction(nativeHandle)
    actual fun commitLocalTransformTransaction() = FilaTransformManager_commitLocalTransformTransaction(nativeHandle)
    
    actual var isAccurateTranslationsEnabled: Boolean
        get() = FilaTransformManager_isAccurateTranslationsEnabled(nativeHandle)
        set(value) { FilaTransformManager_setAccurateTranslationsEnabled(nativeHandle, value) }
}
