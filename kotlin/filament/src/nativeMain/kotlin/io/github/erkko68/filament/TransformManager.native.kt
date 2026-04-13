@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaTransformManager

actual class TransformManager internal constructor(internal var nativeHandle: CPointer<FilaTransformManager>?) {
    actual fun hasComponent(entity: Entity): Boolean = FilaTransformManager_hasComponent(nativeHandle, entity.toUInt())
    actual fun getInstance(entity: Entity): EntityInstance = FilaTransformManager_getInstance(nativeHandle, entity.toUInt()).toInt()
    
    actual fun create(entity: Entity): EntityInstance = FilaTransformManager_create(nativeHandle, entity.toUInt()).toInt()
    
    actual fun create(entity: Entity, parent: EntityInstance, localTransform: FloatArray?): EntityInstance {
        return if (localTransform != null) {
            localTransform.usePinned { 
                FilaTransformManager_createWithParent(nativeHandle, entity.toUInt(), parent.toUInt(), it.addressOf(0)).toInt()
            }
        } else {
            FilaTransformManager_createWithParent(nativeHandle, entity.toUInt(), parent.toUInt(), null).toInt()
        }
    }
        
    actual fun create(entity: Entity, parent: EntityInstance, localTransform: DoubleArray?): EntityInstance {
        return if (localTransform != null) {
            localTransform.usePinned { 
                FilaTransformManager_createWithParentFp64(nativeHandle, entity.toUInt(), parent.toUInt(), it.addressOf(0)).toInt()
            }
        } else {
            FilaTransformManager_createWithParentFp64(nativeHandle, entity.toUInt(), parent.toUInt(), null).toInt()
        }
    }
    
    actual fun destroy(entity: Entity) = FilaTransformManager_destroy(nativeHandle, entity.toUInt())
    
    actual fun setParent(instance: EntityInstance, newParent: EntityInstance) = 
        FilaTransformManager_setParent(nativeHandle, instance.toUInt(), newParent.toUInt())
        
    actual fun getParent(instance: EntityInstance): Entity = FilaTransformManager_getParent(nativeHandle, instance.toUInt()).toInt()
    
    actual fun getChildCount(instance: EntityInstance): Int = FilaTransformManager_getChildCount(nativeHandle, instance.toUInt()).toInt()
    
    actual fun getChildren(instance: EntityInstance, outEntities: IntArray?): IntArray {
        val count = getChildCount(instance)
        val result = outEntities ?: IntArray(count)
        if (count > 0) {
            result.usePinned { 
                FilaTransformManager_getChildren(nativeHandle, instance.toUInt(), it.addressOf(0).reinterpret(), count.toULong())
            }
        }
        return result
    }
    
    actual fun setTransform(instance: EntityInstance, localTransform: FloatArray) {
        localTransform.usePinned { 
            FilaTransformManager_setTransform(nativeHandle, instance.toUInt(), it.addressOf(0))
        }
    }
        
    actual fun setTransform(instance: EntityInstance, localTransform: DoubleArray) {
        localTransform.usePinned { 
            FilaTransformManager_setTransformFp64(nativeHandle, instance.toUInt(), it.addressOf(0))
        }
    }
    
    actual fun getTransform(instance: EntityInstance, outLocalTransform: FloatArray?): FloatArray {
        val result = outLocalTransform ?: FloatArray(16)
        result.usePinned { 
            FilaTransformManager_getTransform(nativeHandle, instance.toUInt(), it.addressOf(0))
        }
        return result
    }
        
    actual fun getTransform(instance: EntityInstance, outLocalTransform: DoubleArray?): DoubleArray {
        val result = outLocalTransform ?: DoubleArray(16)
        result.usePinned { 
            FilaTransformManager_getTransformFp64(nativeHandle, instance.toUInt(), it.addressOf(0))
        }
        return result
    }
    
    actual fun getWorldTransform(instance: EntityInstance, outWorldTransform: FloatArray?): FloatArray {
        val result = outWorldTransform ?: FloatArray(16)
        result.usePinned { 
            FilaTransformManager_getWorldTransform(nativeHandle, instance.toUInt(), it.addressOf(0))
        }
        return result
    }
        
    actual fun getWorldTransform(instance: EntityInstance, outWorldTransform: DoubleArray?): DoubleArray {
        val result = outWorldTransform ?: DoubleArray(16)
        result.usePinned { 
            FilaTransformManager_getWorldTransformFp64(nativeHandle, instance.toUInt(), it.addressOf(0))
        }
        return result
    }
    
    actual fun openLocalTransformTransaction() = FilaTransformManager_openLocalTransformTransaction(nativeHandle)
    actual fun commitLocalTransformTransaction() = FilaTransformManager_commitLocalTransformTransaction(nativeHandle)
    
    actual fun setAccurateTranslationsEnabled(enable: Boolean) = 
        FilaTransformManager_setAccurateTranslationsEnabled(nativeHandle, enable)
        
    actual fun isAccurateTranslationsEnabled(): Boolean = 
        FilaTransformManager_isAccurateTranslationsEnabled(nativeHandle)
}
