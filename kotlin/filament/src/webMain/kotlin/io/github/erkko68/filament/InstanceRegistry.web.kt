package io.github.erkko68.filament

// Component-manager instances (LightManager/TransformManager/RenderableManager) are opaque
// embind objects in filament.js, but the common API types them as `EntityInstance` (= Int).
// On Kotlin/JS the old code smuggled the object through the Int via `unsafeCast`; that's
// impossible on wasmJs (an Int can't hold a JsAny). So map Int <-> JS object here, the same
// way EntityManager maps entity ids to their JS Entity wrappers.
internal object InstanceRegistry {
    private val byId = HashMap<Int, JsAny>()
    private var next = 1

    fun register(js: JsAny): Int {
        val id = next++
        byId[id] = js
        return id
    }

    fun get(id: Int): JsAny =
        byId[id] ?: error("No JS instance registered for EntityInstance $id")
}
