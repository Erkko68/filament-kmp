@file:OptIn(ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaTransformManager

actual class TransformManager internal constructor(internal var nativePtr: CPointer<FilaTransformManager>?) {

    actual fun hasComponent(@Entity entity: Int): Boolean =
        FilaTransformManager_hasComponent(nativePtr, entity.toUInt())

    @EntityInstance
    actual fun getInstance(@Entity entity: Int): Int =
        FilaTransformManager_getInstance(nativePtr, entity.toUInt()).toInt()

    actual fun setAccurateTranslationsEnabled(enable: Boolean) =
        FilaTransformManager_setAccurateTranslationsEnabled(nativePtr, enable)

    actual fun isAccurateTranslationsEnabled(): Boolean =
        FilaTransformManager_isAccurateTranslationsEnabled(nativePtr)

    @EntityInstance
    actual fun create(@Entity entity: Int): Int {
        return FilaTransformManager_create(nativePtr, entity.toUInt()).toInt()
    }

    @EntityInstance
    actual fun create(@Entity entity: Int, @EntityInstance parent: Int, localTransform: FloatArray?): Int {
        return if (localTransform != null) {
            localTransform.usePinned { pinned ->
                FilaTransformManager_createWithParent(nativePtr, entity.toUInt(), parent.toUInt(), pinned.addressOf(0))
            }
        } else {
            FilaTransformManager_createWithParent(nativePtr, entity.toUInt(), parent.toUInt(), null)
        }.toInt()
    }

    @EntityInstance
    actual fun create(@Entity entity: Int, @EntityInstance parent: Int, localTransform: DoubleArray?): Int {
        return if (localTransform != null) {
            localTransform.usePinned { pinned ->
                FilaTransformManager_createWithParentFp64(nativePtr, entity.toUInt(), parent.toUInt(), pinned.addressOf(0))
            }
        } else {
            FilaTransformManager_createWithParentFp64(nativePtr, entity.toUInt(), parent.toUInt(), null)
        }.toInt()
    }

    actual fun destroy(@Entity entity: Int) =
        FilaTransformManager_destroy(nativePtr, entity.toUInt())

    actual fun setParent(@EntityInstance i: Int, @EntityInstance newParent: Int) =
        FilaTransformManager_setParent(nativePtr, i.toUInt(), newParent.toUInt())

    @Entity
    actual fun getParent(@EntityInstance i: Int): Int =
        FilaTransformManager_getParent(nativePtr, i.toUInt()).toInt()

    actual fun getChildCount(@EntityInstance i: Int): Int =
        FilaTransformManager_getChildCount(nativePtr, i.toUInt()).toInt()

    @Entity
    actual fun getChildren(@EntityInstance i: Int, outEntities: IntArray?): IntArray {
        val count = getChildCount(i)
        val out = outEntities ?: IntArray(count)
        if (out.isEmpty()) return out
        out.usePinned { pinned ->
            FilaTransformManager_getChildren(nativePtr, i.toUInt(), pinned.addressOf(0).reinterpret(), out.size.toULong())
        }
        return out
    }

    actual fun setTransform(@EntityInstance i: Int, localTransform: FloatArray) {
        localTransform.usePinned { pinned ->
            FilaTransformManager_setTransform(nativePtr, i.toUInt(), pinned.addressOf(0))
        }
    }

    actual fun setTransform(@EntityInstance i: Int, localTransform: DoubleArray) {
        localTransform.usePinned { pinned ->
            FilaTransformManager_setTransformFp64(nativePtr, i.toUInt(), pinned.addressOf(0))
        }
    }

    actual fun getTransform(@EntityInstance i: Int, outLocalTransform: FloatArray?): FloatArray {
        val out = outLocalTransform ?: FloatArray(16)
        out.usePinned { pinned ->
            FilaTransformManager_getTransform(nativePtr, i.toUInt(), pinned.addressOf(0))
        }
        return out
    }

    actual fun getTransform(@EntityInstance i: Int, outLocalTransform: DoubleArray?): DoubleArray {
        val out = outLocalTransform ?: DoubleArray(16)
        out.usePinned { pinned ->
            FilaTransformManager_getTransformFp64(nativePtr, i.toUInt(), pinned.addressOf(0))
        }
        return out
    }

    actual fun getWorldTransform(@EntityInstance i: Int, outWorldTransform: FloatArray?): FloatArray {
        val out = outWorldTransform ?: FloatArray(16)
        out.usePinned { pinned ->
            FilaTransformManager_getWorldTransform(nativePtr, i.toUInt(), pinned.addressOf(0))
        }
        return out
    }

    actual fun getWorldTransform(@EntityInstance i: Int, outWorldTransform: DoubleArray?): DoubleArray {
        val out = outWorldTransform ?: DoubleArray(16)
        out.usePinned { pinned ->
            FilaTransformManager_getWorldTransformFp64(nativePtr, i.toUInt(), pinned.addressOf(0))
        }
        return out
    }

    actual fun openLocalTransformTransaction() =
        FilaTransformManager_openLocalTransformTransaction(nativePtr)

    actual fun commitLocalTransformTransaction() =
        FilaTransformManager_commitLocalTransformTransaction(nativePtr)

    actual val nativeObject: Long
        get() = nativePtr?.rawValue?.toLong() ?: 0L
}
