package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.gltfio.jni.FilamentAsset as JniFilamentAsset
import io.github.erkko68.filament.jni.Box as JniBox

actual class FilamentAsset(internal val jni: JniFilamentAsset) {
    actual fun getRoot(): Int = jni.getRoot()
    actual fun popRenderable(): Int = jni.popRenderable()
    actual fun popRenderables(entities: IntArray): Int = jni.popRenderables(entities)
    
    actual fun getEntities(): IntArray = jni.getEntities()
    actual fun getLightEntities(): IntArray = jni.getLightEntities()
    actual fun getRenderableEntities(): IntArray = jni.getRenderableEntities()
    actual fun getCameraEntities(): IntArray = jni.getCameraEntities()
    
    actual fun getEntitiesByName(name: String): IntArray = jni.getEntitiesByName(name)
    actual fun getEntitiesByPrefix(prefix: String): IntArray = jni.getEntitiesByPrefix(prefix)
    actual fun getFirstEntityByName(name: String): Int = jni.getFirstEntityByName(name)
    
    actual fun getEntityCount(): Int = jni.getEntities().size
    actual fun getAssetInstanceCount(): Int = 1 
    actual fun getAssetInstances(): Array<FilamentInstance> {
        return arrayOf(FilamentInstance(jni.getInstance()))
    }
    
    actual fun getBoundingBox(): Box {
        val jbox = jni.getBoundingBox()
        return Box(jbox.center[0], jbox.center[1], jbox.center[2], jbox.halfExtent[0], jbox.halfExtent[1], jbox.halfExtent[2])
    }
    
    actual fun getName(entity: Int): String? = jni.getName(entity)
    actual fun getExtras(entity: Int): String? = jni.getExtras(entity)
    actual fun getMorphTargetNames(entity: Int): Array<String> = jni.getMorphTargetNames(entity)
    
    actual fun getResourceUris(): Array<String> = jni.getResourceUris()
    
    actual fun releaseSourceData() {
        jni.releaseSourceData()
    }
}
