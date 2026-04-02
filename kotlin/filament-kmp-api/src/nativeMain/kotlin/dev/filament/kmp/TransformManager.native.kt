package dev.filament.kmp

actual class TransformManager {
    actual fun hasComponent(entity: Int): Boolean = TODO("Not yet implemented")

    actual fun getInstance(entity: Int): Int = TODO("Not yet implemented")

    actual fun create(entity: Int): Int = TODO("Not yet implemented")

    actual fun create(entity: Int, parent: Int, localTransform: FloatArray?): Int = TODO("Not yet implemented")

    actual fun destroy(entity: Int) {
        TODO("Not yet implemented")
    }

    actual fun setParent(i: Int, newParent: Int) {
        TODO("Not yet implemented")
    }

    actual fun getParent(i: Int): Int = TODO("Not yet implemented")

    actual fun getChildCount(i: Int): Int = TODO("Not yet implemented")

    actual fun getChildren(i: Int, outEntities: IntArray?): IntArray = TODO("Not yet implemented")

    actual fun setTransform(i: Int, localTransform: FloatArray) {
        TODO("Not yet implemented")
    }

    actual fun getTransform(i: Int, outLocalTransform: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun getWorldTransform(i: Int, outWorldTransform: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun getNativeObject(): Long = TODO("Not yet implemented")
}

