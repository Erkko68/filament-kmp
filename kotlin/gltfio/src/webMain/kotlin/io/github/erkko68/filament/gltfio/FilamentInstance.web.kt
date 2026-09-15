package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.web.interop.emptyJsObject

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.EntityManager
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.web.`gltfio_FilamentInstance` as JSFilamentInstance
import io.github.erkko68.filament.web.Vector
import io.github.erkko68.filament.Entity
import io.github.erkko68.filament.web.MaterialInstance as JSMaterialInstance
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap
import io.github.erkko68.filament.InternalFilamentApi

actual class FilamentInstance @InternalFilamentApi constructor(internal val jsInstance: JSFilamentInstance) {
    actual val root: Entity get() {
        val jsEntity = jsInstance.getRoot()
        val id = jsEntity.getId().toInt()
        EntityManager.register(id, jsEntity)
        return id
    }

    actual val entities: IntArray get() {
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

    actual val entityCount: Int get() = jsInstance.getEntities().size().toInt()

    actual val animator: Animator get() {
        // Null until ResourceLoader has loaded the asset — gltfio creates the animator there.
        return Animator(checkNotNull(jsInstance.getAnimator()) { ANIMATOR_NOT_LOADED })
    }

    actual val boundingBox: Box get() {
        return Box()
    }

    actual val asset: FilamentAsset get() {
        return FilamentAsset(jsInstance.getAsset())
    }

    actual val skinCount: Int get() = jsInstance.getSkinCount().toInt()

    actual val skinNames: List<String> get() {
        val vector = jsInstance.getSkinNames()
        val result = Array(vector.size().toInt()) { "" }
        for (i in 0 until vector.size().toInt()) {
            result[i] = vector.get(i.toDouble()).toString()
        }
        return result.toList()
    }

    actual fun attachSkin(skinIndex: Int, target: Entity) {
        jsInstance.attachSkin(skinIndex.toDouble(), EntityManager.jsEntityOf(target))
    }

    actual fun detachSkin(skinIndex: Int, target: Entity) {
        jsInstance.detachSkin(skinIndex.toDouble(), EntityManager.jsEntityOf(target))
    }

    actual fun getJointCountAt(skinIndex: Int): Int =
        jsInstance.getJointCountAt(skinIndex.toDouble()).toInt()

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

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "throws at runtime with embind 'unbound types' — the vector return type is unregistered in the web prebuilt.")
    actual val materialInstances: List<MaterialInstance> get() {
        val vector = jsInstance.getMaterialInstances()
        return List(vector.size().toInt()) { i ->
            MaterialInstance(vector.get(i.toDouble()))
        }
    }

    actual val materialVariantNames: List<String> get() {
        val names = jsInstance.getMaterialVariantNames()
        return List(names.size) { names[it].toString() }
    }

    actual constructor() : this(emptyJsObject().unsafeCast<JSFilamentInstance>()) {
        // Warning: Default constructor creates empty FilamentInstance with no valid JS binding backing
        // This is only safe if the instance is never actually used; normally instances should be created via AssetLoader
    }
}