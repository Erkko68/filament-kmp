package io.github.erkko68.filament.compose.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine

/**
 * A Filament object filament-compose created: destroyed once its owner lets go *and* every owned object
 * created from it ([dependsOn]) is gone. Compose orders teardown only within one composition and abandons
 * discarded objects in no order, so an engine or material could otherwise die before its children.
 * Parents we don't own (a caller's own Engine or Material) aren't tracked.
 */
// Composition-thread only, like every Filament call here.
internal class Owned<T>(val value: T, dependsOn: List<Any?>, private val destroy: (T & Any) -> Unit) : RememberObserver {
    private var refs = 1
    private val parents = if (value == null) emptyList() else dependsOn.mapNotNull { registry[it]?.last() }.onEach { it.refs++ }

    init {
        if (value != null) registry.getOrPut(value) { ArrayList() } += this
    }

    fun release() {
        if (--refs > 0 || value == null) return
        val owners = registry.getValue(value)
        owners.remove(this)
        if (owners.isEmpty()) registry.remove(value)
        destroy(value)
        parents.forEach { it.release() }
    }

    override fun onRemembered() {}
    override fun onForgotten() = release()
    override fun onAbandoned() = release()

    private companion object {
        // Several call sites can own one shared value (a per-engine gltfio context); any live one will do.
        val registry = HashMap<Any, ArrayList<Owned<*>>>()
    }
}

/**
 * Creates a Filament object on [engine] (null allowed) and destroys it when this call site leaves the
 * composition, or its create pass is discarded, once nothing created from it is still alive.
 */
@Composable
internal fun <T> rememberOwned(
    engine: Engine,
    vararg keys: Any?,
    dependsOn: List<Any?> = emptyList(),
    create: () -> T,
    destroy: (T & Any) -> Unit,
): T = remember(engine, *keys) { Owned(create(), dependsOn + engine, destroy) }.value
