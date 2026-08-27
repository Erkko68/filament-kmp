package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.confined
import io.github.erkko68.filament.cString
import io.github.erkko68.filament.ffm.FilaBox
import io.github.erkko68.filament.ffm.FilamentC
import io.github.erkko68.filament.isNullPtr
import io.github.erkko68.filament.toInts
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import io.github.erkko68.filament.InternalFilamentApi

actual class FilamentInstance @InternalFilamentApi constructor(internal var nativeHandle: MemorySegment?) {
    actual constructor() : this(null)

    actual val root: Int get() = FilamentC.FilaFilamentInstance_getRoot(nativeHandle)

    actual val entities: IntArray get() {
        val count = FilamentC.FilaFilamentInstance_getEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        return confined { a ->
            val seg = a.allocate(ValueLayout.JAVA_INT, count.toLong())
            FilamentC.FilaFilamentInstance_getEntities(nativeHandle, seg)
            seg.toInts()
        }
    }

    actual val entityCount: Int get() = FilamentC.FilaFilamentInstance_getEntityCount(nativeHandle).toInt()

    actual val animator: Animator get() {
        // Null until ResourceLoader has loaded the asset — gltfio creates the animator there.
        val handle = FilamentC.FilaFilamentInstance_getAnimator(nativeHandle)
        check(!handle.isNullPtr()) { ANIMATOR_NOT_LOADED }
        return Animator(handle)
    }

    actual val boundingBox: Box get() = confined { a ->
        val b = FilamentC.FilaFilamentInstance_getBoundingBox(a, nativeHandle)
        Box(FilaBox.centerX(b), FilaBox.centerY(b), FilaBox.centerZ(b),
            FilaBox.halfExtentX(b), FilaBox.halfExtentY(b), FilaBox.halfExtentZ(b))
    }

    actual val asset: FilamentAsset get() = FilamentAsset(FilamentC.FilaFilamentInstance_getAsset(nativeHandle))

    actual val skinCount: Int get() = FilamentC.FilaFilamentInstance_getSkinCount(nativeHandle).toInt()

    actual val skinNames: List<String> get() {
        val count = skinCount
        if (count == 0) return emptyList()
        return confined { a ->
            val names = a.allocate(ValueLayout.ADDRESS, count.toLong())
            FilamentC.FilaFilamentInstance_getSkinNames(nativeHandle, names)
            List(count) { names.getAtIndex(ValueLayout.ADDRESS, it.toLong()).let { p -> if (p.isNullPtr()) "" else p.cString() } }
        }
    }

    actual fun attachSkin(skinIndex: Int, target: Int) = FilamentC.FilaFilamentInstance_attachSkin(nativeHandle, skinIndex.toLong(), target)
    actual fun detachSkin(skinIndex: Int, target: Int) = FilamentC.FilaFilamentInstance_detachSkin(nativeHandle, skinIndex.toLong(), target)

    actual fun getJointCountAt(skinIndex: Int): Int = FilamentC.FilaFilamentInstance_getJointCountAt(nativeHandle, skinIndex.toLong()).toInt()

    actual fun getJointsAt(skinIndex: Int): IntArray {
        val count = getJointCountAt(skinIndex)
        if (count == 0) return IntArray(0)
        return confined { a ->
            val seg = a.allocate(ValueLayout.JAVA_INT, count.toLong())
            FilamentC.FilaFilamentInstance_getJointsAt(nativeHandle, skinIndex.toLong(), seg)
            seg.toInts()
        }
    }

    actual fun applyMaterialVariant(variantIndex: Int) = FilamentC.FilaFilamentInstance_applyMaterialVariant(nativeHandle, variantIndex.toLong())

    actual val materialInstances: List<MaterialInstance> get() {
        val count = FilamentC.FilaFilamentInstance_getMaterialInstanceCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        return confined { a ->
            val out = a.allocate(ValueLayout.ADDRESS, count.toLong())
            FilamentC.FilaFilamentInstance_getMaterialInstances(nativeHandle, out)
            List(count) { MaterialInstance(out.getAtIndex(ValueLayout.ADDRESS, it.toLong())) }
        }
    }

    actual val materialVariantNames: List<String> get() {
        val count = FilamentC.FilaFilamentInstance_getMaterialVariantCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        return confined { a ->
            val names = a.allocate(ValueLayout.ADDRESS, count.toLong())
            FilamentC.FilaFilamentInstance_getMaterialVariantNames(nativeHandle, names)
            List(count) { names.getAtIndex(ValueLayout.ADDRESS, it.toLong()).let { p -> if (p.isNullPtr()) "" else p.cString() } }
        }
    }
}
