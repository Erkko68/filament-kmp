package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.EntityManager
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.js.`gltfio_FilamentInstance` as JSFilamentInstance
import io.github.erkko68.filament.js.Vector
import io.github.erkko68.filament.js.Entity
import io.github.erkko68.filament.js.MaterialInstance as JSMaterialInstance

actual class FilamentInstance(internal val jsInstance: JSFilamentInstance) {
    private val skinJointCounts = mutableMapOf<Int, Int>()
    private val skinJoints = mutableMapOf<Int, IntArray>()

    actual fun getRoot(): Int {
        val jsEntity = jsInstance.getRoot()
        val id = jsEntity.getId().toInt()
        EntityManager.register(id, jsEntity)
        return id
    }

    actual fun getEntities(): IntArray {
        val vector = jsInstance.getEntities()
        val result = IntArray(vector.size().toInt())
        for (i in 0 until vector.size().toInt()) {
            val jsEntity = vector.get(i)
            val id = jsEntity.getId().toInt()
            EntityManager.register(id, jsEntity)
            result[i] = id
        }
        return result
    }

    actual fun getEntityCount(): Int {
        return jsInstance.getEntities().size().toInt()
    }

    actual fun getAnimator(): Animator {
        return Animator(jsInstance.getAnimator())
    }

    actual fun getBoundingBox(): Box {
        return Box()
    }

    actual fun getAsset(): FilamentAsset {
        return FilamentAsset(jsInstance.getAsset())
    }

    actual fun getSkinCount(): Int {
        return jsInstance.getSkinNames().size().toInt()
    }

    actual fun getSkinNames(): Array<String> {
        val vector = jsInstance.getSkinNames()
        val result = Array(vector.size().toInt()) { "" }
        for (i in 0 until vector.size().toInt()) {
            result[i] = vector.get(i)
        }
        return result
    }

    actual fun attachSkin(skinIndex: Int, target: Int) {
        // JS binding expects Entity, but KMP API uses Int. Entity ID is passed directly via unsafeCast
        jsInstance.attachSkin(skinIndex, target.unsafeCast<Entity>())
    }

    actual fun detachSkin(skinIndex: Int, target: Int) {
        // JS binding expects Entity, but KMP API uses Int. Entity ID is passed directly via unsafeCast
        jsInstance.detachSkin(skinIndex, target.unsafeCast<Entity>())
    }

    actual fun getJointCountAt(skinIndex: Int): Int {
        return skinJointCounts[skinIndex] ?: 0
    }

    actual fun getJointsAt(skinIndex: Int): IntArray {
        return skinJoints[skinIndex] ?: IntArray(0)
    }

    actual fun applyMaterialVariant(variantIndex: Int) {
        jsInstance.applyMaterialVariant(variantIndex)
    }

    actual fun getMaterialInstances(): Array<MaterialInstance> {
        val vector = jsInstance.getMaterialInstances()
        return Array(vector.size().toInt()) { i ->
            MaterialInstance(vector.get(i))
        }
    }

    actual fun getMaterialVariantNames(): Array<String> {
        return jsInstance.getMaterialVariantNames()
    }

    actual constructor() : this(js("{}").unsafeCast<JSFilamentInstance>()) {
        // Warning: Default constructor creates empty FilamentInstance with no valid JS binding backing
        // This is only safe if the instance is never actually used; normally instances should be created via AssetLoader
    }
}