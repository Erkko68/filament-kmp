package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine

/**
 * Keeps an engine that filament-compose owns alive until every composable using it has let go.
 *
 * Within one composition, resources leave before the engine they were remembered after. Not across
 * compositions: a scene inside a SubcomposeLayout (a BoxWithConstraints, a LazyColumn item) can outlive the
 * engine hoisted above it, or be deactivated after it when the item is recycled, and destroying anything on a
 * destroyed engine crashes the process. So resource composables [RetainEngine] and the owner hands its engine
 * to [destroyWhenUnused], which destroys it with the last user.
 */
// ponytail: composition-thread only, like every Filament call here; a lock if engines ever leave that thread.
internal object EngineLifetimes {
    private val users = HashMap<Engine, Int>()
    private val pendingDestroy = HashMap<Engine, () -> Unit>()

    fun retain(engine: Engine) {
        users[engine] = (users[engine] ?: 0) + 1
    }

    fun release(engine: Engine) {
        val left = (users[engine] ?: 1) - 1
        if (left > 0) {
            users[engine] = left
            return
        }
        users.remove(engine)
        pendingDestroy.remove(engine)?.invoke()
    }

    /** Runs [destroy] (the owner's teardown of [engine]) now, or when its last user releases it. */
    fun destroyWhenUnused(engine: Engine, destroy: () -> Unit = engine::destroy) {
        if ((users[engine] ?: 0) > 0) pendingDestroy[engine] = destroy else destroy()
    }
}

/**
 * Keeps [engine] alive while this call site is composed; call it before creating anything on [engine].
 *
 * Held from the moment it is remembered, not when its composition applies: objects created in a pass that is then
 * deactivated or discarded are abandoned only *after* the applied ones are forgotten, so a retain taken at apply
 * time would let the engine go first.
 */
@Composable
internal fun RetainEngine(engine: Engine) {
    remember(engine) { EngineRetention(engine) }
}

/** One [EngineLifetimes] reference to [engine], taken on creation and dropped when forgotten or abandoned. */
internal class EngineRetention(private val engine: Engine) : RememberObserver {
    init {
        EngineLifetimes.retain(engine)
    }

    override fun onRemembered() {}

    override fun onForgotten() = EngineLifetimes.release(engine)

    override fun onAbandoned() = onForgotten()
}
