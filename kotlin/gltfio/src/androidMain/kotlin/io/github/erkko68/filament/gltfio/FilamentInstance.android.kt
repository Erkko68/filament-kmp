package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.*
import io.github.erkko68.filament.jni.*
import io.github.erkko68.filament.jni.*
import io.github.erkko68.filament.Entity

actual class FilamentInstance {
    public var nativeHandle: Long = 0

    actual constructor()

    constructor(nativeHandle: Long) : this() {
        this.nativeHandle = nativeHandle
    }

    actual val root: Entity get() = FilaFilamentInstance_getRoot(nativeHandle)

    actual val entities: IntArray get() {
        val count = FilaFilamentInstance_getEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        heapScoped {
            val entities = I32Array(alloc((count) * 4))
            FilaFilamentInstance_getEntities(nativeHandle, entities.ptr)
            return IntArray(count) { entities[it] }
        }
    }

    actual val entityCount: Int get() = FilaFilamentInstance_getEntityCount(nativeHandle).toInt()

    actual val animator: Animator get() {
        // Null until ResourceLoader has loaded the asset — gltfio creates the animator there.
        val handle = FilaFilamentInstance_getAnimator(nativeHandle)
        check(handle != 0L) { ANIMATOR_NOT_LOADED }
        return Animator(handle)
    }

    actual val boundingBox: Box get() {
        return heapScoped {
            val box = FilaBox(alloc(FilaBox.SIZE))
            FilaFilamentInstance_getBoundingBox(box.ptr, nativeHandle)
            Box(box.centerX, box.centerY, box.centerZ, box.halfExtentX, box.halfExtentY, box.halfExtentZ)
        }
    }

    actual val asset: FilamentAsset get() = FilamentAsset(FilaFilamentInstance_getAsset(nativeHandle))

    actual val skinCount: Int get() = FilaFilamentInstance_getSkinCount(nativeHandle).toInt()

    actual val skinNames: List<String> get() {
        val count = skinCount
        if (count == 0) return emptyList()
        heapScoped {
            val names = PtrArray(alloc(count * PtrArray.SIZE))
            FilaFilamentInstance_getSkinNames(nativeHandle, names.ptr)
            return List(count) { readString(names[it]) ?: "" }
        }
    }

    actual fun attachSkin(skinIndex: Int, target: Entity) {
        FilaFilamentInstance_attachSkin(nativeHandle, skinIndex.toLong(), target)
    }

    actual fun detachSkin(skinIndex: Int, target: Entity) {
        FilaFilamentInstance_detachSkin(nativeHandle, skinIndex.toLong(), target)
    }

    actual fun getJointCountAt(skinIndex: Int): Int = FilaFilamentInstance_getJointCountAt(nativeHandle, skinIndex.toLong()).toInt()

    actual fun getJointsAt(skinIndex: Int): IntArray {
        val count = getJointCountAt(skinIndex)
        if (count == 0) return IntArray(0)
        heapScoped {
            val joints = I32Array(alloc((count) * 4))
            FilaFilamentInstance_getJointsAt(nativeHandle, skinIndex.toLong(), joints.ptr)
            return IntArray(count) { joints[it] }
        }
    }

    actual fun applyMaterialVariant(variantIndex: Int) {
        FilaFilamentInstance_applyMaterialVariant(nativeHandle, variantIndex.toLong())
    }

    actual val materialInstances: List<io.github.erkko68.filament.MaterialInstance> get() {
        val count = FilaFilamentInstance_getMaterialInstanceCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        heapScoped {
            val instances = PtrArray(alloc(count * PtrArray.SIZE))
            FilaFilamentInstance_getMaterialInstances(nativeHandle, instances.ptr)
            return List(count) { io.github.erkko68.filament.MaterialInstance(instances[it]) }
        }
    }

    actual val materialVariantNames: List<String> get() {
        val count = FilaFilamentInstance_getMaterialVariantCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        heapScoped {
            val names = PtrArray(alloc(count * PtrArray.SIZE))
            FilaFilamentInstance_getMaterialVariantNames(nativeHandle, names.ptr)
            return List(count) { readString(names[it]) ?: "" }
        }
    }
}
