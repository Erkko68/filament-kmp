package io.github.erkko68.filament

import io.github.erkko68.filament.js.TransformManager as JSTransformManager
import io.github.erkko68.filament.js.TransformManager_Instance as JSTransformManagerInstance

actual class TransformManager(internal val jsTransformManager: JSTransformManager) {
    actual fun hasComponent(entity: Entity): Boolean {
        return jsTransformManager.hasComponent(EntityManager.jsEntityOf(entity))
    }

    actual fun getInstance(entity: Entity): EntityInstance {
        return jsTransformManager.getInstance(EntityManager.jsEntityOf(entity)).unsafeCast<EntityInstance>()
    }

    actual fun create(entity: Entity): EntityInstance {
        jsTransformManager.create(EntityManager.jsEntityOf(entity))
        return getInstance(entity)
    }

    actual fun create(
        entity: Entity,
        parent: EntityInstance,
        localTransform: FloatArray?
    ): EntityInstance {
        jsTransformManager.create(EntityManager.jsEntityOf(entity))
        val instance = getInstance(entity)
        setParent(instance, parent)
        if (localTransform != null) {
            setTransform(instance, localTransform)
        }
        return instance
    }

    actual fun create(
        entity: Entity,
        parent: EntityInstance,
        localTransform: DoubleArray?
    ): EntityInstance {
        jsTransformManager.create(EntityManager.jsEntityOf(entity))
        val instance = getInstance(entity)
        setParent(instance, parent)
        if (localTransform != null) {
            setTransform(instance, localTransform)
        }
        return instance
    }

    actual fun destroy(entity: Entity) {
        jsTransformManager.destroy(EntityManager.jsEntityOf(entity))
    }

    actual fun setParent(
        instance: EntityInstance,
        newParent: EntityInstance
    ) {
        jsTransformManager.setParent(instance.unsafeCast<JSTransformManagerInstance>(), newParent.unsafeCast<JSTransformManagerInstance>())
    }

    actual fun getParent(instance: EntityInstance): Entity {
        // JS API doesn't seem to have getParent? d.ts didn't show it.
        return js("{}").unsafeCast<Entity>()
    }

    actual fun getChildCount(instance: EntityInstance): Int {
        return 0
    }

    actual fun getChildren(
        instance: EntityInstance,
        outEntities: IntArray?
    ): IntArray {
        return outEntities ?: IntArray(0)
    }

    actual fun setTransform(instance: EntityInstance, localTransform: FloatArray) {
        jsTransformManager.setTransform(instance.unsafeCast<JSTransformManagerInstance>(), localTransform.toTypedArray() as Array<Number>)
    }

    actual fun setTransform(instance: EntityInstance, localTransform: DoubleArray) {
        jsTransformManager.setTransform(instance.unsafeCast<JSTransformManagerInstance>(), localTransform.toTypedArray() as Array<Number>)
    }

    actual fun getTransform(
        instance: EntityInstance,
        outLocalTransform: FloatArray?
    ): FloatArray {
        val result = outLocalTransform ?: FloatArray(16)
        val jsMatrix = jsTransformManager.getTransform(instance.unsafeCast<JSTransformManagerInstance>()) as Array<Double>
        for (i in 0 until 16) result[i] = jsMatrix[i].toFloat()
        return result
    }

    actual fun getTransform(
        instance: EntityInstance,
        outLocalTransform: DoubleArray?
    ): DoubleArray {
        val result = outLocalTransform ?: DoubleArray(16)
        val jsMatrix = jsTransformManager.getTransform(instance.unsafeCast<JSTransformManagerInstance>()) as Array<Double>
        for (i in 0 until 16) result[i] = jsMatrix[i]
        return result
    }

    actual fun getWorldTransform(
        instance: EntityInstance,
        outWorldTransform: FloatArray?
    ): FloatArray {
        val result = outWorldTransform ?: FloatArray(16)
        val jsMatrix = jsTransformManager.getWorldTransform(instance.unsafeCast<JSTransformManagerInstance>()) as Array<Double>
        for (i in 0 until 16) result[i] = jsMatrix[i].toFloat()
        return result
    }

    actual fun getWorldTransform(
        instance: EntityInstance,
        outWorldTransform: DoubleArray?
    ): DoubleArray {
        val result = outWorldTransform ?: DoubleArray(16)
        val jsMatrix = jsTransformManager.getWorldTransform(instance.unsafeCast<JSTransformManagerInstance>()) as Array<Double>
        for (i in 0 until 16) result[i] = jsMatrix[i]
        return result
    }

    actual fun openLocalTransformTransaction() {
        jsTransformManager.openLocalTransformTransaction()
    }

    actual fun commitLocalTransformTransaction() {
        jsTransformManager.commitLocalTransformTransaction()
    }

    actual var isAccurateTranslationsEnabled: Boolean = false
        set(value) {
            field = value
        }
}