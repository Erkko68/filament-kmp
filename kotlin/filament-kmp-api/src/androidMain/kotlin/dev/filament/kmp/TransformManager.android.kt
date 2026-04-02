package dev.filament.kmp

import com.google.android.filament.TransformManager as AndroidTransformManager

actual class TransformManager internal constructor(
    internal val androidTransformManager: AndroidTransformManager,
) {
    actual fun hasComponent(entity: Int): Boolean = androidTransformManager.hasComponent(entity)

    actual fun getInstance(entity: Int): Int = androidTransformManager.getInstance(entity)

    actual fun create(entity: Int): Int = androidTransformManager.create(entity)

    actual fun create(entity: Int, parent: Int, localTransform: FloatArray?): Int {
        return androidTransformManager.create(entity, parent, localTransform)
    }

    actual fun destroy(entity: Int) {
        androidTransformManager.destroy(entity)
    }

    actual fun setParent(i: Int, newParent: Int) {
        androidTransformManager.setParent(i, newParent)
    }

    actual fun getParent(i: Int): Int = androidTransformManager.getParent(i)

    actual fun getChildCount(i: Int): Int = androidTransformManager.getChildCount(i)

    actual fun getChildren(i: Int, outEntities: IntArray?): IntArray = androidTransformManager.getChildren(i, outEntities)

    actual fun setTransform(i: Int, localTransform: FloatArray) {
        androidTransformManager.setTransform(i, localTransform)
    }

    actual fun getTransform(i: Int, outLocalTransform: FloatArray?): FloatArray {
        return androidTransformManager.getTransform(i, outLocalTransform)
    }

    actual fun getWorldTransform(i: Int, outWorldTransform: FloatArray?): FloatArray {
        return androidTransformManager.getWorldTransform(i, outWorldTransform)
    }

    actual fun getNativeObject(): Long = androidTransformManager.nativeObject
}

