package io.github.erkko68.filament

actual class Scene {
    actual fun setSkybox(skybox: Skybox?) {
    }

    actual fun getSkybox(): Skybox? {
        TODO("Not yet implemented")
    }

    actual fun setIndirectLight(ibl: IndirectLight?) {
    }

    actual fun getIndirectLight(): IndirectLight? {
        TODO("Not yet implemented")
    }

    actual fun addEntity(entity: Int) {
    }

    actual fun addEntities(entities: IntArray) {
    }

    actual fun removeEntity(entity: Int) {
    }

    actual fun removeEntities(entities: IntArray) {
    }

    actual fun getEntityCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun getRenderableCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun getLightCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun hasEntity(entity: Int): Boolean {
        TODO("Not yet implemented")
    }

    actual fun getEntities(): IntArray {
        TODO("Not yet implemented")
    }

    actual fun getEntities(outArray: IntArray?): IntArray {
        TODO("Not yet implemented")
    }

    actual fun forEach(block: (Int) -> Unit) {
    }
}