package eric.bitria.samples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.TextureSampler
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
            it.setParameter("baseColor", color.r, color.g, color.b)
        }
    }
    DisposableEffect(instance) {
        onDispose { engine.destroyMaterialInstance(instance) }
    }
    return instance
}

/**
 * Loads `textured.filamat` — a LIT material with a single `albedo` sampler2d parameter. Bind a
 * texture per-instance via [rememberTexturedInstance].
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun rememberTexturedTemplate(): Material? =
    rememberMaterial { Res.readBytes("files/materials/textured.filamat") }

/**
 * Creates a [MaterialInstance] from the textured template with [texture] bound to its `albedo`
 * sampler (trilinear filtering, repeat wrap). Keyed on both so a new texture rebinds.
 */
@Composable
fun rememberTexturedInstance(template: Material, texture: Texture): MaterialInstance {
    val engine = LocalFilamentEngine.current
    val instance = remember(template, texture) {
        template.createInstance().also {
            val sampler = TextureSampler(
                TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR,
                TextureSampler.MagFilter.LINEAR,
                TextureSampler.WrapMode.REPEAT,
            )
            it.setParameter("albedo", texture, sampler)
        }
    }
    DisposableEffect(instance) {
        onDispose { engine.destroyMaterialInstance(instance) }
    }
    return instance
}

/**
 * Loads `emissive.filamat` — UNLIT material with `color` (float3) and `intensity` (float)
 * parameters. Use with [rememberEmissiveInstance].
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun rememberEmissiveTemplate(): Material? =
    rememberMaterial { Res.readBytes("files/materials/emissive.filamat") }

/**
 * Creates an emissive [MaterialInstance] tuned to [color] and [intensity]. Intensity values
 * above ~1 push the surface past the bloom threshold and produce a halo.
 */
@Composable
fun rememberEmissiveInstance(template: Material, color: Color, intensity: Float): MaterialInstance {
    val engine = LocalFilamentEngine.current
    val instance = remember(template, color, intensity) {
        template.createInstance().also {
            it.setParameter("color", color.r, color.g, color.b)
            it.setParameter("intensity", intensity)
        }
    }
    DisposableEffect(instance) {
        onDispose { engine.destroyMaterialInstance(instance) }
    }
    return instance
}
