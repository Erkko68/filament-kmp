package io.github.erkko68.filament

import io.github.erkko68.filament.jni.TransformManager as JniTransformManager

actual class TransformManager(val nativeTransformManager: JniTransformManager) {
    actual fun hasComponent(entity: Int): Boolean = nativeTransformManager.hasComponent(entity)
    actual fun getInstance(entity: Int): Int = nativeTransformManager.getInstance(entity)
    
    actual fun create(entity: Int): Int = nativeTransformManager.create(entity)
    actual fun create(entity: Int, parent: Int, localTransform: FloatArray?): Int = nativeTransformManager.create(entity, parent, localTransform)
    actual fun create(entity: Int, parent: Int, localTransform: DoubleArray?): Int = nativeTransformManager.create(entity, parent, localTransform)
    
    actual fun destroy(entity: Int) { nativeTransformManager.destroy(entity) }
    
    actual fun setParent(instance: Int, newParent: Int) { nativeTransformManager.setParent(instance, newParent) }
    actual fun getParent(instance: Int): Int = nativeTransformManager.getParent(instance)
    
    actual fun getChildCount(instance: Int): Int = nativeTransformManager.getChildCount(instance)
    actual fun getChildren(instance: Int, outEntities: IntArray?): IntArray = nativeTransformManager.getChildren(instance, outEntities)
    
    actual fun setTransform(instance: Int, localTransform: FloatArray) { nativeTransformManager.setTransform(instance, localTransform) }
    actual fun setTransform(instance: Int, localTransform: DoubleArray) { nativeTransformManager.setTransform(instance, localTransform) }
    
    actual fun getTransform(instance: Int, outLocalTransform: FloatArray?): FloatArray = nativeTransformManager.getTransform(instance, outLocalTransform)
    actual fun getTransform(instance: Int, outLocalTransform: DoubleArray?): DoubleArray = nativeTransformManager.getTransform(instance, outLocalTransform)
    
    actual fun getWorldTransform(instance: Int, outWorldTransform: FloatArray?): FloatArray = nativeTransformManager.getWorldTransform(instance, outWorldTransform)
    actual fun getWorldTransform(instance: Int, outWorldTransform: DoubleArray?): DoubleArray = nativeTransformManager.getWorldTransform(instance, outWorldTransform)
    
    actual fun openLocalTransformTransaction() { nativeTransformManager.openLocalTransformTransaction() }
    actual fun commitLocalTransformTransaction() { nativeTransformManager.commitLocalTransformTransaction() }
    
    actual var isAccurateTranslationsEnabled: Boolean
        get() = nativeTransformManager.isAccurateTranslationsEnabled()
        set(value) { nativeTransformManager.setAccurateTranslationsEnabled(value) }
}
