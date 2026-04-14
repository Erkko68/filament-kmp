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

    actual fun getRoot(): Int = FilaFilamentInstance_getRoot(nativeHandle).toInt()

    actual fun getEntities(): IntArray {
        val count = FilaFilamentInstance_getEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        memScoped {
            val entities = allocArray<FilaEntityVar>(count)
            FilaFilamentInstance_getEntities(nativeHandle, entities)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual fun getEntityCount(): Int = FilaFilamentInstance_getEntityCount(nativeHandle).toInt()

    actual fun getAnimator(): Animator = Animator(FilaFilamentInstance_getAnimator(nativeHandle))

    actual fun getBoundingBox(): Box {
        return FilaFilamentInstance_getBoundingBox(nativeHandle).useContents {
            Box(
                centerX, centerY, centerZ,
                halfExtentX, halfExtentY, halfExtentZ
            )
        }
    }

    actual fun getAsset(): FilamentAsset = FilamentAsset(FilaFilamentInstance_getAsset(nativeHandle))

    actual fun getSkinCount(): Int = FilaFilamentInstance_getSkinCount(nativeHandle).toInt()

    actual fun getSkinNames(): Array<String> {
        val count = getSkinCount()
        if (count == 0) return emptyArray()
        memScoped {
            val names = allocArray<CPointerVar<ByteVar>>(count)
            FilaFilamentInstance_getSkinNames(nativeHandle, names)
            return Array(count) { names[it]?.toKString() ?: "" }
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

    actual fun getMaterialInstances(): Array<io.github.erkko68.filament.MaterialInstance> {
        val count = FilaFilamentInstance_getMaterialInstanceCount(nativeHandle).toInt()
        if (count == 0) return emptyArray()
        memScoped {
            val instances = allocArray<CPointerVar<cnames.structs.FilaMaterialInstance>>(count)
            FilaFilamentInstance_getMaterialInstances(nativeHandle, instances)
            return Array(count) { io.github.erkko68.filament.MaterialInstance(instances[it]) }
        }
    }

    actual fun getMaterialVariantNames(): Array<String> {
        val count = FilaFilamentInstance_getMaterialVariantCount(nativeHandle).toInt()
        if (count == 0) return emptyArray()
        memScoped {
            val names = allocArray<CPointerVar<ByteVar>>(count)
            FilaFilamentInstance_getMaterialVariantNames(nativeHandle, names)
            return Array(count) { names[it]?.toKString() ?: "" }
        }
    }
}
