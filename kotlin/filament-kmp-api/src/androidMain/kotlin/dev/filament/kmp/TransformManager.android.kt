package dev.filament.kmp

import com.google.android.filament.TransformManager as AndroidTransformManager

actual class TransformManager internal constructor(
    internal val androidTransformManager: AndroidTransformManager,
) {
    actual fun hasComponent(@Entity entity: Int): Boolean = androidTransformManager.hasComponent(entity)

    @EntityInstance
    actual fun getInstance(@Entity entity: Int): Int = androidTransformManager.getInstance(entity)

    actual fun setAccurateTranslationsEnabled(enable: Boolean) {
        androidTransformManager.setAccurateTranslationsEnabled(enable)
    }

    actual fun isAccurateTranslationsEnabled(): Boolean = androidTransformManager.isAccurateTranslationsEnabled

    @EntityInstance
    actual fun create(@Entity entity: Int): Int = androidTransformManager.create(entity)

    @EntityInstance
    actual fun create(@Entity entity: Int, @EntityInstance parent: Int, localTransform: FloatArray?): Int {
        return androidTransformManager.create(entity, parent, localTransform)
    }

    @EntityInstance
    actual fun create(@Entity entity: Int, @EntityInstance parent: Int, localTransform: DoubleArray?): Int {
        return androidTransformManager.create(entity, parent, localTransform)
    }

    actual fun destroy(@Entity entity: Int) {
        androidTransformManager.destroy(entity)
    }

    actual fun setParent(@EntityInstance i: Int, @EntityInstance newParent: Int) {
        androidTransformManager.setParent(i, newParent)
    }

    @Entity
    actual fun getParent(@EntityInstance i: Int): Int = androidTransformManager.getParent(i)

    actual fun getChildCount(@EntityInstance i: Int): Int = androidTransformManager.getChildCount(i)

    @Entity
    actual fun getChildren(@EntityInstance i: Int, outEntities: IntArray?): IntArray = androidTransformManager.getChildren(i, outEntities)

    actual fun setTransform(@EntityInstance i: Int, localTransform: FloatArray) {
        androidTransformManager.setTransform(i, localTransform)
    }

    actual fun setTransform(@EntityInstance i: Int, localTransform: DoubleArray) {
        androidTransformManager.setTransform(i, localTransform)
    }

    actual fun getTransform(@EntityInstance i: Int, outLocalTransform: FloatArray?): FloatArray {
        return androidTransformManager.getTransform(i, outLocalTransform)
    }

    actual fun getTransform(@EntityInstance i: Int, outLocalTransform: DoubleArray?): DoubleArray {
        return androidTransformManager.getTransform(i, outLocalTransform)
    }

    actual fun getWorldTransform(@EntityInstance i: Int, outWorldTransform: FloatArray?): FloatArray {
        return androidTransformManager.getWorldTransform(i, outWorldTransform)
    }

    actual fun getWorldTransform(@EntityInstance i: Int, outWorldTransform: DoubleArray?): DoubleArray {
        return androidTransformManager.getWorldTransform(i, outWorldTransform)
    }

    actual fun openLocalTransformTransaction() {
        androidTransformManager.openLocalTransformTransaction()
    }

    actual fun commitLocalTransformTransaction() {
        androidTransformManager.commitLocalTransformTransaction()
    }

    actual fun getNativeObject(): Long = androidTransformManager.nativeObject
}

