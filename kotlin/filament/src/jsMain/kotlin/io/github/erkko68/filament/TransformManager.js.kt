package io.github.erkko68.filament

actual class TransformManager {
    actual fun hasComponent(entity: Entity): Boolean {
        TODO("Not yet implemented")
    }

    actual fun getInstance(entity: Entity): EntityInstance {
        TODO("Not yet implemented")
    }

    actual fun create(entity: Entity): EntityInstance {
        TODO("Not yet implemented")
    }

    actual fun create(
        entity: Entity,
        parent: EntityInstance,
        localTransform: FloatArray?
    ): EntityInstance {
        TODO("Not yet implemented")
    }

    actual fun create(
        entity: Entity,
        parent: EntityInstance,
        localTransform: DoubleArray?
    ): EntityInstance {
        TODO("Not yet implemented")
    }

    actual fun destroy(entity: Entity) {
    }

    actual fun setParent(
        instance: EntityInstance,
        newParent: EntityInstance
    ) {
    }

    actual fun getParent(instance: EntityInstance): Entity {
        TODO("Not yet implemented")
    }

    actual fun getChildCount(instance: EntityInstance): Int {
        TODO("Not yet implemented")
    }

    actual fun getChildren(
        instance: EntityInstance,
        outEntities: IntArray?
    ): IntArray {
        TODO("Not yet implemented")
    }

    actual fun setTransform(instance: EntityInstance, localTransform: FloatArray) {
    }

    actual fun setTransform(instance: EntityInstance, localTransform: DoubleArray) {
    }

    actual fun getTransform(
        instance: EntityInstance,
        outLocalTransform: FloatArray?
    ): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun getTransform(
        instance: EntityInstance,
        outLocalTransform: DoubleArray?
    ): DoubleArray {
        TODO("Not yet implemented")
    }

    actual fun getWorldTransform(
        instance: EntityInstance,
        outWorldTransform: FloatArray?
    ): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun getWorldTransform(
        instance: EntityInstance,
        outWorldTransform: DoubleArray?
    ): DoubleArray {
        TODO("Not yet implemented")
    }

    actual fun openLocalTransformTransaction() {
    }

    actual fun commitLocalTransformTransaction() {
    }

    actual fun setAccurateTranslationsEnabled(enable: Boolean) {
    }

    actual fun isAccurateTranslationsEnabled(): Boolean {
        TODO("Not yet implemented")
    }
}