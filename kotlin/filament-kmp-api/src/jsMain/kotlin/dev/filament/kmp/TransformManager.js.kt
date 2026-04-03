package dev.filament.kmp

actual class TransformManager {
    actual fun hasComponent(@Entity entity: Int): Boolean = TODO("Not yet implemented")

    @EntityInstance
    actual fun getInstance(@Entity entity: Int): Int = TODO("Not yet implemented")

    actual fun setAccurateTranslationsEnabled(enable: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun isAccurateTranslationsEnabled(): Boolean = TODO("Not yet implemented")

    @EntityInstance
    actual fun create(@Entity entity: Int): Int = TODO("Not yet implemented")

    @EntityInstance
    actual fun create(@Entity entity: Int, @EntityInstance parent: Int, localTransform: FloatArray?): Int = TODO("Not yet implemented")

    @EntityInstance
    actual fun create(@Entity entity: Int, @EntityInstance parent: Int, localTransform: DoubleArray?): Int = TODO("Not yet implemented")

    actual fun destroy(@Entity entity: Int) {
        TODO("Not yet implemented")
    }

    actual fun setParent(@EntityInstance i: Int, @EntityInstance newParent: Int) {
        TODO("Not yet implemented")
    }

    @Entity
    actual fun getParent(@EntityInstance i: Int): Int = TODO("Not yet implemented")

    actual fun getChildCount(@EntityInstance i: Int): Int = TODO("Not yet implemented")

    @Entity
    actual fun getChildren(@EntityInstance i: Int, outEntities: IntArray?): IntArray = TODO("Not yet implemented")

    actual fun setTransform(@EntityInstance i: Int, localTransform: FloatArray) {
        TODO("Not yet implemented")
    }

    actual fun setTransform(@EntityInstance i: Int, localTransform: DoubleArray) {
        TODO("Not yet implemented")
    }

    actual fun getTransform(@EntityInstance i: Int, outLocalTransform: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun getTransform(@EntityInstance i: Int, outLocalTransform: DoubleArray?): DoubleArray = TODO("Not yet implemented")

    actual fun getWorldTransform(@EntityInstance i: Int, outWorldTransform: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun getWorldTransform(@EntityInstance i: Int, outWorldTransform: DoubleArray?): DoubleArray = TODO("Not yet implemented")

    actual fun openLocalTransformTransaction() {
        TODO("Not yet implemented")
    }

    actual fun commitLocalTransformTransaction() {
        TODO("Not yet implemented")
    }

    actual fun getNativeObject(): Long = TODO("Not yet implemented")
}

