@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.gltfio

import kotlinx.cinterop.*
import io.github.erkko68.filament.*
import io.github.erkko68.filament.cinterop.*
import io.github.erkko68.filament.gltfio.cinterop.*
import cnames.structs.FilaFilamentInstance

actual class FilamentInstance {
    public var nativeHandle: CPointer<FilaFilamentInstance>? = null

    actual constructor()

    constructor(nativeHandle: CPointer<FilaFilamentInstance>?) : this() {
        this.nativeHandle = nativeHandle
    }

    actual val root: Int get() = FilaFilamentInstance_getRoot(nativeHandle).toInt()

    actual val entities: IntArray get() {
        val count = FilaFilamentInstance_getEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        memScoped {
            val entities = allocArray<FilaEntityVar>(count)
            FilaFilamentInstance_getEntities(nativeHandle, entities)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual val entityCount: Int get() = FilaFilamentInstance_getEntityCount(nativeHandle).toInt()

    actual val animator: Animator get() {
        // Null until ResourceLoader has loaded the asset — gltfio creates the animator there.
        val handle = FilaFilamentInstance_getAnimator(nativeHandle)
        checkNotNull(handle) { ANIMATOR_NOT_LOADED }
        return Animator(handle)
    }

    actual val boundingBox: Box get() {
        return FilaFilamentInstance_getBoundingBox(nativeHandle).useContents {
            Box(
                centerX, centerY, centerZ,
                halfExtentX, halfExtentY, halfExtentZ
            )
        }
    }

    actual val asset: FilamentAsset get() = FilamentAsset(FilaFilamentInstance_getAsset(nativeHandle))

    actual val skinCount: Int get() = FilaFilamentInstance_getSkinCount(nativeHandle).toInt()

    actual val skinNames: List<String> get() {
        val count = skinCount
        if (count == 0) return emptyList()
        memScoped {
            val names = allocArray<CPointerVar<ByteVar>>(count)
            FilaFilamentInstance_getSkinNames(nativeHandle, names)
            return List(count) { names[it]?.toKString() ?: "" }
        }
    }

    actual fun attachSkin(skinIndex: Int, target: Int) {
        FilaFilamentInstance_attachSkin(nativeHandle, skinIndex.toULong(), target.toUInt())
    }

    actual fun detachSkin(skinIndex: Int, target: Int) {
        FilaFilamentInstance_detachSkin(nativeHandle, skinIndex.toULong(), target.toUInt())
    }

    actual fun getJointCountAt(skinIndex: Int): Int = FilaFilamentInstance_getJointCountAt(nativeHandle, skinIndex.toULong()).toInt()

    actual fun getJointsAt(skinIndex: Int): IntArray {
        val count = getJointCountAt(skinIndex)
        if (count == 0) return IntArray(0)
        memScoped {
            val joints = allocArray<FilaEntityVar>(count)
            FilaFilamentInstance_getJointsAt(nativeHandle, skinIndex.toULong(), joints)
            return IntArray(count) { joints[it].toInt() }
        }
    }

    actual fun applyMaterialVariant(variantIndex: Int) {
        FilaFilamentInstance_applyMaterialVariant(nativeHandle, variantIndex.toULong())
    }

    actual val materialInstances: List<io.github.erkko68.filament.MaterialInstance> get() {
        val count = FilaFilamentInstance_getMaterialInstanceCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        memScoped {
            val instances = allocArray<CPointerVar<cnames.structs.FilaMaterialInstance>>(count)
            FilaFilamentInstance_getMaterialInstances(nativeHandle, instances)
            return List(count) { io.github.erkko68.filament.MaterialInstance(instances[it]) }
        }
    }

    actual val materialVariantNames: List<String> get() {
        val count = FilaFilamentInstance_getMaterialVariantCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        memScoped {
            val names = allocArray<CPointerVar<ByteVar>>(count)
            FilaFilamentInstance_getMaterialVariantNames(nativeHandle, names)
            return List(count) { names[it]?.toKString() ?: "" }
        }
    }
}
