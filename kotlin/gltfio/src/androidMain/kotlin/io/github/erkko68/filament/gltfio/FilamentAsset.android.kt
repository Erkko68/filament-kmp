package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Entity
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap
import io.github.erkko68.filament.InternalFilamentApi

actual class FilamentAsset @InternalFilamentApi constructor(
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

    actual val root: Entity get() = nativeObject.root
    
    actual fun popRenderable(): Entity = nativeObject.popRenderable()
    
    actual fun popRenderables(entities: IntArray): Int = nativeObject.popRenderables(entities)

    actual val entities: IntArray get() = nativeObject.entities
    
    actual val lightEntities: IntArray get() = nativeObject.lightEntities
    
    actual val renderableEntities: IntArray get() = nativeObject.renderableEntities
    
    actual val cameraEntities: IntArray get() = nativeObject.cameraEntities

    actual fun getEntitiesByName(name: String): IntArray = nativeObject.getEntitiesByName(name)

    actual fun getEntitiesByPrefix(prefix: String): IntArray = nativeObject.getEntitiesByPrefix(prefix)
    
    actual fun getFirstEntityByName(name: String): Entity = nativeObject.getFirstEntityByName(name)

    actual val entityCount: Int get() = nativeObject.entities.size

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "throws at runtime with embind 'unbound types' — the vector return type is unregistered in the web prebuilt.")
    actual val assetInstanceCount: Int get() = resolveKnownInstances().size

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "throws at runtime with embind 'unbound types' — the vector return type is unregistered in the web prebuilt.")
    actual val assetInstances: List<FilamentInstance> get() = resolveKnownInstances().toList()

    actual val boundingBox: Box get() {
        val nativeBox = nativeObject.boundingBox
        return Box(
            nativeBox.center[0], nativeBox.center[1], nativeBox.center[2],
            nativeBox.halfExtent[0], nativeBox.halfExtent[1], nativeBox.halfExtent[2]
        )
    }

    actual fun getName(entity: Entity): String? = nativeObject.getName(entity)

    actual fun getExtras(entity: Entity): String? = nativeObject.getExtras(entity)
    
    actual fun getMorphTargetNames(entity: Entity): List<String> = nativeObject.getMorphTargetNames(entity).toList()
    
    actual val resourceUris: List<String> get() = nativeObject.resourceUris.toList()

    actual fun releaseSourceData() {
        nativeObject.releaseSourceData()
    }

    actual val engine: Engine get() = Engine(nativeObject.engine)

    actual val instance: FilamentInstance get() = resolveKnownInstances().first()
}
