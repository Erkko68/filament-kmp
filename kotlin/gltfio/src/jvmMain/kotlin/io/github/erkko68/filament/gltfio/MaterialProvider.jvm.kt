package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.gltfio.jni.MaterialProvider as JniMaterialProvider
import io.github.erkko68.filament.gltfio.jni.UbershaderProvider as JniUbershaderProvider

actual interface MaterialProvider {
    actual fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): MaterialInstance?
    actual fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): Material?
    actual fun getMaterials(): Array<Material>
    actual fun needsDummyData(attrib: Int): Boolean
    actual fun destroyMaterials()
    actual fun destroy()
}

actual class UbershaderProvider actual constructor(engine: Engine) : MaterialProvider {
    internal val jni = JniUbershaderProvider(engine.nativeEngine)

    actual override fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): MaterialInstance? {
        val jniInstance = jni.createMaterialInstance(config.jni, uvmap, label, extras)
        return jniInstance?.let { MaterialInstance(it) }
    }

    actual override fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): Material? {
        val jniMaterial = jni.getMaterial(config.jni, uvmap, label)
        return jniMaterial?.let { Material(it) }
    }

    actual override fun getMaterials(): Array<Material> {
        return jni.getMaterials().map { Material(it) }.toTypedArray()
    }

    actual override fun needsDummyData(attrib: Int): Boolean = jni.needsDummyData(attrib)
    actual override fun destroyMaterials() = jni.destroyMaterials()
    actual override fun destroy() = jni.destroy()
}
