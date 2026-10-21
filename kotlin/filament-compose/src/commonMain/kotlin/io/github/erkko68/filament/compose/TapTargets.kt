package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.erkko68.filament.Entity
import io.github.erkko68.filament.compose.scene.Position

/** A scene's tap handlers by renderable entity; each [FilamentView] over the scene dispatches its picks here. */
internal class TapTargets {
    private val handlers = HashMap<Entity, (Position) -> Unit>()

    /** Snapshot state: a view installs its tap detector only while some handler is registered. */
    var isEmpty by mutableStateOf(true)
        private set

    fun add(entities: IntArray, handler: (Position) -> Unit) {
        for (entity in entities) handlers[entity] = handler
        isEmpty = handlers.isEmpty()
    }

    fun remove(entities: IntArray, handler: (Position) -> Unit) {
        for (entity in entities) if (handlers[entity] === handler) handlers.remove(entity)
        isEmpty = handlers.isEmpty()
    }

    fun handlerFor(entity: Entity): ((Position) -> Unit)? = handlers[entity]
}

internal val LocalTapTargets = staticCompositionLocalOf<TapTargets?> { null }

/** The nearest enclosing `Group`'s tap handler, for children that declare none of their own. */
internal val LocalTapHandler = compositionLocalOf<((Position) -> Unit)?> { null }

/**
 * The handler a scene object answers taps with: its own [onTap], else the enclosing Group's.
 * Stays the same instance while [onTap] is non-null, so a new lambda each recomposition doesn't re-register.
 */
@Composable
internal fun rememberTapHandler(onTap: ((Position) -> Unit)?): ((Position) -> Unit)? {
    val latest by rememberUpdatedState(onTap)
    val own = remember { { at: Position -> latest?.invoke(at); Unit } }
    return if (onTap != null) own else LocalTapHandler.current
}

/** Registers [entities] (remembered, so the array identity is stable) under [handler] while composed. */
@Composable
internal fun TapTarget(entities: IntArray, handler: ((Position) -> Unit)?) {
    val targets = LocalTapTargets.current ?: return
    DisposableEffect(targets, entities, handler) {
        if (handler != null) targets.add(entities, handler)
        onDispose { if (handler != null) targets.remove(entities, handler) }
    }
}
