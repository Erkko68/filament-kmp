package io.github.erkko68.filament

import io.github.erkko68.filament.js.TransformManager as JSTransformManager
import io.github.erkko68.filament.js.TransformManager_Instance as JSTransformManagerInstance

actual class TransformManager(internal val jsTransformManager: JSTransformManager) {
    // `EntityInstance` is a typealias for `Int` in common code, but on JS the
    // upstream binding returns an opaque `TransformManager$Instance` JS object
    // with no accessor for a numeric ID. We `unsafeCast` that object to "Int",
    // which works for downstream calls (the JS object is what setParent/setTransform
    // actually expect) but breaks equality across two getInstance calls, since
    // upstream returns a fresh wrapper each time. Cache the wrapper per entity
    // so referential equality stands.
    private val instances = mutableMapOf<Entity, JSTransformManagerInstance>()

    actual fun hasComponent(entity: Entity): Boolean {
        return jsTransformManager.hasComponent(EntityManager.jsEntityOf(entity))
    }

    actual fun getInstance(entity: Entity): EntityInstance {
        val cached = instances.getOrPut(entity) {
            jsTransformManager.getInstance(EntityManager.jsEntityOf(entity))
        }
        return cached.unsafeCast<EntityInstance>()
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
        // Register the returned JS Entity wrapper so subsequent JS calls
        // (setParent, getInstance, …) can resolve it back through
        // EntityManager.jsEntityOf().
        val jsEntity = jsTransformManager.getParent(instance.unsafeCast<JSTransformManagerInstance>())
        val id = jsEntity.getId().toInt()
        if (id != 0) EntityManager.register(id, jsEntity)
        return id
    }

    // Upstream binds getChildren as a single embind LAMBDA returning an
    // EntityVector — there's no separately exposed getChildCount, so derive it.
    actual fun getChildCount(instance: EntityInstance): Int {
        val vec = jsTransformManager.getChildren(instance.unsafeCast<JSTransformManagerInstance>())
        return vec.size().unsafeCast<Int>()
    }

    actual fun getChildren(
        instance: EntityInstance,
        outEntities: IntArray?
    ): IntArray {
        val vec = jsTransformManager.getChildren(instance.unsafeCast<JSTransformManagerInstance>())
        val count = vec.size().unsafeCast<Int>()
        val result = outEntities ?: IntArray(count)
        for (i in 0 until minOf(count, result.size)) {
            val jsEntity = vec.get(i)
            val id = jsEntity.getId().unsafeCast<Int>()
            if (id != 0) EntityManager.register(id, jsEntity)
            result[i] = id
        }
        return result
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