package eric.bitria.samples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.scene.Color
import io.github.erkko68.filament.compose.scene.rememberMaterial
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Loads `lit_color.filamat` once and exposes it as a shared [Material]. The `.mat` source
 * declares a single `float3 baseColor` parameter — drive it per-instance via
 * [rememberColorInstance].
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun rememberLitColorTemplate(): Material? =
    rememberMaterial { Res.readBytes("files/materials/lit_color.filamat") }

/**
 * Creates a [MaterialInstance] from the LIT-color template and sets its `baseColor` to
 * [color]. Keyed on the colour so a different colour allocates a new instance (cheap) and
 * disposes the previous one. Use this only when the renderable referencing the instance is
 * (re)built each time the colour changes — for in-place updates while a renderable is still
 * bound, keep a single instance and call `setParameter("baseColor", …)` from a SideEffect.
 */
@Composable
fun rememberColorInstance(template: Material, color: Color): MaterialInstance {
    val engine = LocalFilamentEngine.current
    val instance = remember(template, color) {
        template.createInstance().also {
            it.setParameter("baseColor", color.x, color.y, color.z)
        }
    }
    DisposableEffect(instance) {
        onDispose { engine.destroyMaterialInstance(instance) }
    }
    return instance
}
