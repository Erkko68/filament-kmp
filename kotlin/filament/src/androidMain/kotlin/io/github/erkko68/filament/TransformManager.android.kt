package io.github.erkko68.filament

import com.google.android.filament.TransformManager as AndroidTransformManager

actual class TransformManager internal constructor(val nativeTransformManager: AndroidTransformManager) {
    actual fun hasComponent(entity: Entity): Boolean = nativeTransformManager.hasComponent(entity)
    actual fun getInstance(entity: Entity): EntityInstance = nativeTransformManager.getInstance(entity)
    
    actual fun create(entity: Entity): EntityInstance = nativeTransformManager.create(entity)
    
    actual fun create(entity: Entity, parent: EntityInstance, localTransform: FloatArray?): EntityInstance =
        nativeTransformManager.create(entity, parent, localTransform)
        
    actual fun create(entity: Entity, parent: EntityInstance, localTransform: DoubleArray?): EntityInstance =
        nativeTransformManager.create(entity, parent, localTransform)
    
    actual fun destroy(entity: Entity) = nativeTransformManager.destroy(entity)
    
    actual fun setParent(instance: EntityInstance, newParent: EntityInstance) = 
        nativeTransformManager.setParent(instance, newParent)
        
    actual fun getParent(instance: EntityInstance): Entity = nativeTransformManager.getParent(instance)
    
    actual fun getChildCount(instance: EntityInstance): Int = nativeTransformManager.getChildCount(instance)
    
    actual fun getChildren(instance: EntityInstance, outEntities: IntArray?): IntArray = 
        nativeTransformManager.getChildren(instance, outEntities)
    
    actual fun setTransform(instance: EntityInstance, localTransform: FloatArray) = 
        nativeTransformManager.setTransform(instance, localTransform)
        
    actual fun setTransform(instance: EntityInstance, localTransform: DoubleArray) = 
        nativeTransformManager.setTransform(instance, localTransform)
    
    actual fun getTransform(instance: EntityInstance, outLocalTransform: FloatArray?): FloatArray = 
        nativeTransformManager.getTransform(instance, outLocalTransform)
        
    actual fun getTransform(instance: EntityInstance, outLocalTransform: DoubleArray?): DoubleArray = 
        nativeTransformManager.getTransform(instance, outLocalTransform)
    
    actual fun getWorldTransform(instance: EntityInstance, outWorldTransform: FloatArray?): FloatArray = 
        nativeTransformManager.getWorldTransform(instance, outWorldTransform)
        
    actual fun getWorldTransform(instance: EntityInstance, outWorldTransform: DoubleArray?): DoubleArray = 
        nativeTransformManager.getWorldTransform(instance, outWorldTransform)
    
    actual fun openLocalTransformTransaction() = nativeTransformManager.openLocalTransformTransaction()
    actual fun commitLocalTransformTransaction() = nativeTransformManager.commitLocalTransformTransaction()
    
    actual fun setAccurateTranslationsEnabled(enable: Boolean) = 
        nativeTransformManager.setAccurateTranslationsEnabled(enable)
        
    actual fun isAccurateTranslationsEnabled(): Boolean = 
        nativeTransformManager.isAccurateTranslationsEnabled()
}
