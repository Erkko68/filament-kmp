package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Entity

actual class FilamentAsset internal constructor(
    internal val nativeObject: com.google.android.filament.gltfio.FilamentAsset
) {
    private var knownInstances: Array<FilamentInstance>? = null

    internal fun setKnownInstances(instances: Array<FilamentInstance>) {
        knownInstances = instances
    }

    private fun resolveKnownInstances(): Array<FilamentInstance> {
        knownInstances?.let { return it }
        val primary = FilamentInstance(nativeObject.instance, this)
        return arrayOf(primary).also { knownInstances = it }
    }

    actual fun getRoot(): Entity = nativeObject.root
    
    actual fun popRenderable(): Entity = nativeObject.popRenderable()
    
    actual fun popRenderables(entities: IntArray): Int = nativeObject.popRenderables(entities)

    actual fun getEntities(): IntArray = nativeObject.entities
    
    actual fun getLightEntities(): IntArray = nativeObject.lightEntities
    
    actual fun getRenderableEntities(): IntArray = nativeObject.renderableEntities
    
    actual fun getCameraEntities(): IntArray = nativeObject.cameraEntities

    actual fun getEntitiesByName(name: String): IntArray = nativeObject.getEntitiesByName(name)

    actual fun getEntitiesByPrefix(prefix: String): IntArray = nativeObject.getEntitiesByPrefix(prefix)
    
    actual fun getFirstEntityByName(name: String): Entity = nativeObject.getFirstEntityByName(name)

    actual fun getEntityCount(): Int = nativeObject.entities.size

    actual fun getAssetInstanceCount(): Int = resolveKnownInstances().size

    actual fun getAssetInstances(): Array<FilamentInstance> = resolveKnownInstances()

    actual fun getBoundingBox(): Box {
        val nativeBox = nativeObject.boundingBox
        return Box(
            nativeBox.center[0], nativeBox.center[1], nativeBox.center[2],
            nativeBox.halfExtent[0], nativeBox.halfExtent[1], nativeBox.halfExtent[2]
        )
    }

    actual fun getName(entity: Entity): String? = nativeObject.getName(entity)

    actual fun getExtras(entity: Entity): String? = nativeObject.getExtras(entity)
    
    actual fun getMorphTargetNames(entity: Entity): Array<String> = nativeObject.getMorphTargetNames(entity)
    
    actual fun getResourceUris(): Array<String> = nativeObject.resourceUris

    actual fun releaseSourceData() {
        nativeObject.releaseSourceData()
    }

    actual fun getEngine(): Engine = Engine(nativeObject.engine)

    actual fun getInstance(): FilamentInstance = resolveKnownInstances().first()
}
