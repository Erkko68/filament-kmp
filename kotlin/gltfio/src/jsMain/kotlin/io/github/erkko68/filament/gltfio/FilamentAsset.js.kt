package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box

actual class FilamentAsset {
    actual fun getRoot(): Int {
        TODO("Not yet implemented")
    }

    actual fun popRenderable(): Int {
        TODO("Not yet implemented")
    }

    actual fun popRenderables(entities: IntArray): Int {
        TODO("Not yet implemented")
    }

    actual fun getEntities(): IntArray {
        TODO("Not yet implemented")
    }

    actual fun getLightEntities(): IntArray {
        TODO("Not yet implemented")
    }

    actual fun getRenderableEntities(): IntArray {
        TODO("Not yet implemented")
    }

    actual fun getCameraEntities(): IntArray {
        TODO("Not yet implemented")
    }

    actual fun getEntitiesByName(name: String): IntArray {
        TODO("Not yet implemented")
    }

    actual fun getEntitiesByPrefix(prefix: String): IntArray {
        TODO("Not yet implemented")
    }

    actual fun getFirstEntityByName(name: String): Int {
        TODO("Not yet implemented")
    }

    actual fun getEntityCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun getAssetInstanceCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun getAssetInstances(): Array<FilamentInstance> {
        TODO("Not yet implemented")
    }

    actual fun getBoundingBox(): Box {
        TODO("Not yet implemented")
    }

    actual fun getName(entity: Int): String? {
        TODO("Not yet implemented")
    }

    actual fun getExtras(entity: Int): String? {
        TODO("Not yet implemented")
    }

    actual fun getMorphTargetNames(entity: Int): Array<String> {
        TODO("Not yet implemented")
    }

    actual fun getResourceUris(): Array<String> {
        TODO("Not yet implemented")
    }

    actual fun releaseSourceData() {
    }
}