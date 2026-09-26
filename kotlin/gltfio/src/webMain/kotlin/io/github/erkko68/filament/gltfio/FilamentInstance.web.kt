package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.*
import io.github.erkko68.filament.wasm.*
import io.github.erkko68.filament.wasm.*
import io.github.erkko68.filament.Entity

actual class FilamentInstance {
    public var nativeHandle: Int = 0

    actual constructor()

    constructor(nativeHandle: Int) : this() {
        this.nativeHandle = nativeHandle
    }

    actual val root: Entity get() = FilaFilamentInstance_getRoot(nativeHandle).toInt()

    actual val entities: IntArray get() {
        val count = FilaFilamentInstance_getEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        fila.heapScoped {
            val entities = I32Array(alloc((count) * 4))
            FilaFilamentInstance_getEntities(nativeHandle, entities.ptr)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual val entityCount: Int get() = FilaFilamentInstance_getEntityCount(nativeHandle).toInt()

    actual val animator: Animator get() {
        // Null until ResourceLoader has loaded the asset — gltfio creates the animator there.
        val handle = FilaFilamentInstance_getAnimator(nativeHandle)
        check(handle != 0) { ANIMATOR_NOT_LOADED }
        return Animator(handle)
    }

    actual val boundingBox: Box get() {
        return fila.heapScoped {
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
        fila.heapScoped {
            val names = I32Array(alloc((count) * 4))
            FilaFilamentInstance_getSkinNames(nativeHandle, names.ptr)
            return List(count) { fila.readString(names[it]) ?: "" }
        }
    }

    actual fun attachSkin(skinIndex: Int, target: Entity) {
        FilaFilamentInstance_attachSkin(nativeHandle, skinIndex, target)
    }

    actual fun detachSkin(skinIndex: Int, target: Entity) {
        FilaFilamentInstance_detachSkin(nativeHandle, skinIndex, target)
    }

    actual fun getJointCountAt(skinIndex: Int): Int = FilaFilamentInstance_getJointCountAt(nativeHandle, skinIndex).toInt()

    actual fun getJointsAt(skinIndex: Int): IntArray {
        val count = getJointCountAt(skinIndex)
        if (count == 0) return IntArray(0)
        fila.heapScoped {
            val joints = I32Array(alloc((count) * 4))
            FilaFilamentInstance_getJointsAt(nativeHandle, skinIndex, joints.ptr)
            return IntArray(count) { joints[it].toInt() }
        }
    }

    actual fun applyMaterialVariant(variantIndex: Int) {
        FilaFilamentInstance_applyMaterialVariant(nativeHandle, variantIndex)
    }

    actual val materialInstances: List<io.github.erkko68.filament.MaterialInstance> get() {
        val count = FilaFilamentInstance_getMaterialInstanceCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        fila.heapScoped {
            val instances = I32Array(alloc((count) * 4))
            FilaFilamentInstance_getMaterialInstances(nativeHandle, instances.ptr)
            return List(count) { io.github.erkko68.filament.MaterialInstance(instances[it]) }
        }
    }

    actual val materialVariantNames: List<String> get() {
        val count = FilaFilamentInstance_getMaterialVariantCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        fila.heapScoped {
            val names = I32Array(alloc((count) * 4))
            FilaFilamentInstance_getMaterialVariantNames(nativeHandle, names.ptr)
            return List(count) { fila.readString(names[it]) ?: "" }
        }
    }
}
