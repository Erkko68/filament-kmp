package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.EngineRetention
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.RetainEngine
import io.github.erkko68.filament.compose.noFilamentEngine
import io.github.erkko68.filament.compose.scene.GltfioContext.Companion.acquire
import io.github.erkko68.filament.gltfio.AssetLoader
import io.github.erkko68.filament.gltfio.Gltfio
import io.github.erkko68.filament.gltfio.UbershaderProvider

/**
 * Refcounted, per-engine gltfio resources.
 *
 * gltfio is loaded lazily on first call to [acquire]. The shared [AssetLoader] and
 * [UbershaderProvider] are kept alive while at least one consumer (e.g. a
 * [rememberGltfAsset]) is in the composition, and destroyed when the last consumer
 * disposes. This decouples gltfio from [io.github.erkko68.filament.compose.FilamentView]
 * — apps that don't load glTF pay neither the native-library load nor the ubershader
 * compile cost.
 */
internal class GltfioContext private constructor(
    val assetLoader: AssetLoader,
    private val materialProvider: UbershaderProvider,
) {
    private fun destroy() {
        AssetLoader.destroy(assetLoader)
        materialProvider.destroy()
    }

    companion object {
        private class Entry(val context: GltfioContext, var refCount: Int)
        private val entries = mutableMapOf<Engine, Entry>()

        fun acquire(engine: Engine): GltfioContext {
            val entry = entries.getOrPut(engine) {
                Gltfio.init()
                val materials = UbershaderProvider(engine)
                val loader = AssetLoader.create(engine, materials, engine.entityManager)
                Entry(GltfioContext(loader, materials), refCount = 0)
            }
            entry.refCount++
            return entry.context
        }

        fun release(engine: Engine) {
            val entry = entries[engine] ?: return
            entry.refCount--
            if (entry.refCount <= 0) {
                entry.context.destroy()
                entries.remove(engine)
            }
        }
    }
}

/**
 * Acquires the shared per-engine [GltfioContext] for the lifetime of the calling
 * composable. Multiple sibling consumers in the same composition share a single
 * [AssetLoader] and [UbershaderProvider]; they are destroyed when the last consumer
 * leaves the composition.
 */
@Composable
internal fun rememberGltfioContext(engine: Engine = LocalFilamentEngine.current ?: noFilamentEngine()): GltfioContext {
    RetainEngine(engine)
    return remember(engine) { GltfioContextLease(engine) }.context
}

/**
 * One reference to [engine]'s [GltfioContext], released when forgotten *or abandoned*: a composition discarded
 * before it applies (SubcomposeLayout / LazyColumn precomposition) runs no DisposableEffect, so the context and
 * its materials would never be destroyed, and Filament panics on destroying an engine whose materials still have
 * instances.
 */
internal class GltfioContextLease(private val engine: Engine) : RememberObserver {
    private val retention = EngineRetention(engine)
    val context: GltfioContext = GltfioContext.acquire(engine)

    override fun onRemembered() {}

    override fun onForgotten() {
        GltfioContext.release(engine)
        retention.onForgotten()
    }

    override fun onAbandoned() = onForgotten()
}
