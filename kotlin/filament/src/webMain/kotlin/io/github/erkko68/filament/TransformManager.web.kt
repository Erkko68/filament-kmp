package io.github.erkko68.filament

import io.github.erkko68.filament.web.interop.readNumbersInto

import io.github.erkko68.filament.web.TransformManager_Instance as JSTransformManagerInstance

import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.toJsNumbers

import io.github.erkko68.filament.web.TransformManager as JSTransformManager

actual class TransformManager @InternalFilamentApi constructor(internal val jsTransformManager: JSTransformManager) {
    // `EntityInstance` is a typealias for `Int` in common code, but on JS the
    // The upstream binding returns an opaque `TransformManager$Instance` JS object with no
    // numeric ID, and a fresh wrapper each call. Cache the registered EntityInstance id per
    // entity so repeated getInstance calls return the same id (referential equality stands).
    private val instances = mutableMapOf<Entity, EntityInstance>()

    actual fun hasComponent(entity: Entity): Boolean {
        return jsTransformManager.hasComponent(EntityManager.jsEntityOf(entity))
    }

    actual fun getInstance(entity: Entity): EntityInstance {
        return instances.getOrPut(entity) {
            InstanceRegistry.register(jsTransformManager.getInstance(EntityManager.jsEntityOf(entity)))
        }
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
        jsTransformManager.setParent(InstanceRegistry.get(instance).unsafeCast<JSTransformManagerInstance>(), InstanceRegistry.get(newParent).unsafeCast<JSTransformManagerInstance>())
    }

    actual fun getParent(instance: EntityInstance): Entity {
        // Register the returned JS Entity wrapper so subsequent JS calls
        // (setParent, getInstance, …) can resolve it back through
        // EntityManager.jsEntityOf().
        val jsEntity = jsTransformManager.getParent(InstanceRegistry.get(instance).unsafeCast<JSTransformManagerInstance>())
        val id = jsEntity.getId().toInt()
        if (id != 0) EntityManager.register(id, jsEntity)
        return id
    }

    actual fun getChildCount(instance: EntityInstance): Int =
        jsTransformManager.getChildCount(
            InstanceRegistry.get(instance).unsafeCast<JSTransformManagerInstance>()
        ).toInt()

    actual fun getChildren(
        instance: EntityInstance,
        out: IntArray?
    ): IntArray {
        val vec = jsTransformManager.getChildren(InstanceRegistry.get(instance).unsafeCast<JSTransformManagerInstance>())
        val count = vec.size().toInt()
        val result = out ?: IntArray(count)
        for (i in 0 until minOf(count, result.size)) {
            val jsEntity = vec.get(i.toDouble())
            val id = jsEntity.getId().toInt()
            if (id != 0) EntityManager.register(id, jsEntity)
            result[i] = id
        }
        return result
    }

    actual fun setTransform(instance: EntityInstance, localTransform: FloatArray) {
        jsTransformManager.setTransform(InstanceRegistry.get(instance).unsafeCast<JSTransformManagerInstance>(), localTransform.toJsNumbers())
    }

    actual fun setTransform(instance: EntityInstance, localTransform: DoubleArray) {
        jsTransformManager.setTransform(InstanceRegistry.get(instance).unsafeCast<JSTransformManagerInstance>(), localTransform.toJsNumbers())
    }

    actual fun getTransform(
        instance: EntityInstance,
        out: FloatArray?
    ): FloatArray {
        val result = out ?: FloatArray(16)
        (jsTransformManager.getTransform(InstanceRegistry.get(instance).unsafeCast<JSTransformManagerInstance>()))?.readNumbersInto(result)
        return result
    }

    actual fun getTransform(
        instance: EntityInstance,
        out: DoubleArray?
    ): DoubleArray {
        val result = out ?: DoubleArray(16)
        (jsTransformManager.getTransform(InstanceRegistry.get(instance).unsafeCast<JSTransformManagerInstance>()))?.readNumbersInto(result)
        return result
    }

    actual fun getWorldTransform(
        instance: EntityInstance,
        out: FloatArray?
    ): FloatArray {
        val result = out ?: FloatArray(16)
        (jsTransformManager.getWorldTransform(InstanceRegistry.get(instance).unsafeCast<JSTransformManagerInstance>()))?.readNumbersInto(result)
        return result
    }

    actual fun getWorldTransform(
        instance: EntityInstance,
        out: DoubleArray?
    ): DoubleArray {
        val result = out ?: DoubleArray(16)
        (jsTransformManager.getWorldTransform(InstanceRegistry.get(instance).unsafeCast<JSTransformManagerInstance>()))?.readNumbersInto(result)
        return result
    }

    actual fun openLocalTransformTransaction() {
        jsTransformManager.openLocalTransformTransaction()
    }

    actual fun commitLocalTransformTransaction() {
        jsTransformManager.commitLocalTransformTransaction()
    }

    actual var isAccurateTranslationsEnabled: Boolean
        get() = jsTransformManager.isAccurateTranslationsEnabled()
        set(value) { jsTransformManager.setAccurateTranslationsEnabled(value) }
}