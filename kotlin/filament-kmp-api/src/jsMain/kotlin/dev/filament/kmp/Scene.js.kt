package dev.filament.kmp

actual class Scene {
    actual val isValid: Boolean
        get() = TODO("Not yet implemented")

    actual fun getSkybox(): Skybox? = TODO("Not yet implemented")

    actual fun setSkybox(skybox: Skybox?) {
        TODO("Not yet implemented")
    }

    actual fun getIndirectLight(): IndirectLight? = TODO("Not yet implemented")

    actual fun setIndirectLight(ibl: IndirectLight?) {
        TODO("Not yet implemented")
    }

    actual fun addEntity(@Entity entity: Int) {
        TODO("Not yet implemented")
    }

    actual fun addEntities(@Entity entities: IntArray) {
        TODO("Not yet implemented")
    }

    actual fun removeEntity(@Entity entity: Int) {
        TODO("Not yet implemented")
    }

    actual fun remove(@Entity entity: Int) {
        TODO("Not yet implemented")
    }

    actual fun removeEntities(@Entity entities: IntArray) {
        TODO("Not yet implemented")
    }

    actual fun getEntityCount(): Int = TODO("Not yet implemented")

    actual fun getRenderableCount(): Int = TODO("Not yet implemented")

    actual fun getLightCount(): Int = TODO("Not yet implemented")

    actual fun hasEntity(@Entity entity: Int): Boolean = TODO("Not yet implemented")

    actual fun getEntities(outArray: IntArray?): IntArray = TODO("Not yet implemented")

    actual fun getEntities(): IntArray = TODO("Not yet implemented")

    actual fun forEach(entityProcessor: Scene.EntityProcessor) {
        TODO("Not yet implemented")
    }

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() {
    }

    actual interface EntityProcessor {
        actual fun process(@Entity entity: Int)
    }
}

