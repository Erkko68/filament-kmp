package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.web.interop.emptyJsObject

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.EntityManager
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.web.`gltfio_FilamentInstance` as JSFilamentInstance
import io.github.erkko68.filament.web.Vector
import io.github.erkko68.filament.web.Entity
import io.github.erkko68.filament.web.MaterialInstance as JSMaterialInstance

actual class FilamentInstance(internal val jsInstance: JSFilamentInstance) {
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
            val jsEntity = vector.get(i.toDouble())
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
            result[i] = vector.get(i.toDouble()).toString()
        }
        return result
    }

    actual fun attachSkin(skinIndex: Int, target: Int) {
        // JS binding expects Entity, but KMP API uses Int. Entity ID is passed directly via unsafeCast
        jsInstance.attachSkin(skinIndex.toDouble(), EntityManager.jsEntityOf(target))
    }

    actual fun detachSkin(skinIndex: Int, target: Int) {
        // JS binding expects Entity, but KMP API uses Int. Entity ID is passed directly via unsafeCast
        jsInstance.detachSkin(skinIndex.toDouble(), EntityManager.jsEntityOf(target))
    }

    actual fun getJointCountAt(skinIndex: Int): Int {
        return jsInstance.getJointCountAt(skinIndex.toDouble()).toInt()
    }

    actual fun getJointsAt(skinIndex: Int): IntArray {
        val joints = jsInstance.getJointsAt(skinIndex.toDouble())
        return IntArray(joints.size) { i ->
            val jsEntity = joints[i]
            val id = jsEntity.getId().toInt()
            EntityManager.register(id, jsEntity)
            id
        }
    }

    actual fun applyMaterialVariant(variantIndex: Int) {
        jsInstance.applyMaterialVariant(variantIndex.toDouble())
    }

    actual fun getMaterialInstances(): Array<MaterialInstance> {
        val vector = jsInstance.getMaterialInstances()
        return Array(vector.size().toInt()) { i ->
            MaterialInstance(vector.get(i.toDouble()))
        }
    }

    actual fun getMaterialVariantNames(): Array<String> {
        val names = jsInstance.getMaterialVariantNames()
        return Array(names.size) { names[it].toString() }
    }

    actual constructor() : this(emptyJsObject().unsafeCast<JSFilamentInstance>()) {
        // Warning: Default constructor creates empty FilamentInstance with no valid JS binding backing
        // This is only safe if the instance is never actually used; normally instances should be created via AssetLoader
    }
}