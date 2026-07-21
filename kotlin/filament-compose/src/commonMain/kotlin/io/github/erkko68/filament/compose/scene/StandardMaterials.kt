package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.TextureSampler
import io.github.erkko68.filament.compose.LocalFilamentEngine

/**
 * The built-in materials shipped with `filament-compose` — precompiled `.filamat` blobs embedded
 * in the library, so the common "colour a primitive" case needs **no `.mat` authoring, no `matc`,
 * and no asset shipping** on any target (including Web, where runtime material compilation isn't
 * available).
 *
 * You rarely name these directly — use the convenience instance helpers
 * ([rememberColorMaterialInstance], [rememberUnlitColorMaterialInstance],
 * [rememberTexturedMaterialInstance], [rememberEmissiveMaterialInstance],
 * [rememberTransparentColorMaterialInstance]) instead. Reach for
 * [rememberStandardMaterial] only when you want the shared base [Material] to instantiate yourself.
 *
 * To go beyond these (cloth, subsurface, refraction, custom shading), author your own `.mat`,
 * compile it with `matc`, and load it via [rememberMaterial] — see docs/compose/materials.md.
 *
 * The LIT materials are compiled without the skinning / VSM / SSR / stereo variants, so they don't
 * skin and use PCF shadows; that keeps the embedded blobs small.
 */
enum class StandardMaterial {
    /** LIT PBR solid colour: `baseColor`, `metallic`, `roughness`, `reflectance`. */
    Lit,

    /** UNLIT solid colour (ignores scene lighting): `baseColor`. */
    Unlit,

    /** LIT textured base colour: `albedo` sampler, `metallic`, `roughness`. Geometry needs uv0. */
    Textured,

    /** UNLIT emissive (glows through bloom): `color`, `intensity`. */
    Emissive,

    /** LIT PBR with alpha transparency: `baseColor`, `alpha`, `metallic`, `roughness`, `reflectance`. */
    Transparent,
    ;

    internal fun payload(): ByteArray = when (this) {
        Lit -> EmbeddedMaterials.standardLit
        Unlit -> EmbeddedMaterials.standardUnlit
        Textured -> EmbeddedMaterials.standardTextured
        Emissive -> EmbeddedMaterials.standardEmissive
        Transparent -> EmbeddedMaterials.standardTransparent
    }
}

/**
 * Per-scene cache of built [StandardMaterial] [Material]s, so repeated convenience-helper calls
 * (e.g. four primitives each asking for a [StandardMaterial.Lit] instance) share one base material
 * instead of compiling four identical shader packages. Provided by `rememberFilamentScene` via
 * [LocalStandardMaterials] and disposed with the scene. Each type builds lazily on first use.
 */
internal class StandardMaterialCache(val engine: Engine) {
    private val cache = HashMap<StandardMaterial, Material>()

    fun get(type: StandardMaterial): Material =
        cache.getOrPut(type) { Material.Builder().payload(type.payload()).build(engine) }

    fun dispose() {
        for (material in cache.values) engine.destroyMaterial(material)
        cache.clear()
    }
}

/** The enclosing scene's [StandardMaterialCache], or null when used outside `rememberFilamentScene`. */
internal val LocalStandardMaterials = staticCompositionLocalOf<StandardMaterialCache?> { null }

/**
 * The shared base [Material] for a [StandardMaterial], built on [engine] and kept alive for the
 * lifetime of the enclosing `rememberFilamentScene` (one per engine + type, reused across call
 * sites). Outside a scene — when [engine] is hoisted via `rememberFilamentEngine` — it builds a
 * material owned by this call site and destroyed when it leaves the composition.
 *
 * Use this when you want to manage [MaterialInstance]s yourself; otherwise prefer the convenience
 * helpers like [rememberColorMaterialInstance].
 *
 * @param type Which built-in material to build.
 * @param engine Engine to build on. Defaults to the engine in the current composition scope.
 */
@Composable
fun rememberStandardMaterial(
    type: StandardMaterial,
    engine: Engine = LocalFilamentEngine.current,
): Material {
    val cache = LocalStandardMaterials.current
    return if (cache != null && cache.engine == engine) {
        // Scene-owned and shared; the scene disposes it.
        cache.get(type)
    } else {
        // Hoisted engine outside a scene: this call site owns the material.
        val material = remember(engine, type) {
            Material.Builder().payload(type.payload()).build(engine)
        }
        DisposableEffect(material) {
            onDispose { engine.destroyMaterial(material) }
        }
        material
    }
}

/**
 * A LIT PBR [MaterialInstance] tinted [color], updated in place as the inputs change. Drop it
 * straight into a primitive:
 *
 * ```kotlin
 * Cube(material = rememberColorMaterialInstance(Color(0.9f, 0.25f, 0.3f)))
 * ```
 *
 * @param color Linear-sRGB base colour.
 * @param metallic 0 = dielectric, 1 = metal.
 * @param roughness 0 = mirror, 1 = fully rough.
 * @param reflectance Dielectric Fresnel reflectance (0.5 ≈ the common 4% default).
 * @param engine Engine that owns the material. Defaults to the current composition scope's engine.
 */
@Composable
fun rememberColorMaterialInstance(
    color: Color,
    metallic: Float = 0f,
    roughness: Float = 0.5f,
    reflectance: Float = 0.5f,
    engine: Engine = LocalFilamentEngine.current,
): MaterialInstance {
    val material = rememberStandardMaterial(StandardMaterial.Lit, engine)
    // The base material is always non-null here, so the instance is too.
    return rememberMaterialInstance(material, color, metallic, roughness, reflectance, engine = engine) {
        setParameter("baseColor", color)
        setParameter("metallic", metallic)
        setParameter("roughness", roughness)
        setParameter("reflectance", reflectance)
    }!!
}

/**
 * An UNLIT solid-colour [MaterialInstance] — a flat [color] unaffected by scene lighting (HUD
 * markers, wireframe-style fills, unlit gizmos).
 *
 * @param color Linear-sRGB colour.
 * @param engine Engine that owns the material. Defaults to the current composition scope's engine.
 */
@Composable
fun rememberUnlitColorMaterialInstance(
    color: Color,
    engine: Engine = LocalFilamentEngine.current,
): MaterialInstance {
    val material = rememberStandardMaterial(StandardMaterial.Unlit, engine)
    return rememberMaterialInstance(material, color, engine = engine) {
        setParameter("baseColor", color)
    }!!
}

/**
 * A LIT [MaterialInstance] whose base colour is sampled from [texture]. Pair with a primitive (all
 * built-ins supply uv0) or a [io.github.erkko68.filament.compose.scene.primitives.Mesh] with UVs.
 *
 * ```kotlin
 * val tex = rememberTexture { Res.readBytes("files/textures/wood.png") }
 * tex?.let { Sphere(material = rememberTexturedMaterialInstance(it)) }
 * ```
 *
 * @param texture Base-colour texture bound to the `albedo` sampler.
 * @param metallic 0 = dielectric, 1 = metal.
 * @param roughness 0 = mirror, 1 = fully rough.
 * @param sampler How the texture is filtered and wrapped. Defaults to trilinear + repeat.
 * @param engine Engine that owns the material. Defaults to the current composition scope's engine.
 */
@Composable
fun rememberTexturedMaterialInstance(
    texture: Texture,
    metallic: Float = 0f,
    roughness: Float = 0.5f,
    sampler: TextureSampler = TextureSampler(
        TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR,
        TextureSampler.MagFilter.LINEAR,
        TextureSampler.WrapMode.REPEAT,
    ),
    engine: Engine = LocalFilamentEngine.current,
): MaterialInstance {
    val material = rememberStandardMaterial(StandardMaterial.Textured, engine)
    // `sampler` is captured (not keyed): it's almost always the constant default, and a new value
    // typically arrives with a new texture, which is keyed.
    return rememberMaterialInstance(material, texture, metallic, roughness, engine = engine) {
        setParameter("albedo", texture, sampler)
        setParameter("metallic", metallic)
        setParameter("roughness", roughness)
    }!!
}

/**
 * An UNLIT emissive [MaterialInstance] that glows: [color] tint scaled by [intensity]. Intensity
 * above ~1 pushes past the bloom threshold and produces a halo (enable `Bloom` on the view).
 *
 * @param color Linear-sRGB base/emissive colour.
 * @param intensity Emissive multiplier; > 1 to bloom. Defaults to 1 (full colour, no bloom).
 * @param engine Engine that owns the material. Defaults to the current composition scope's engine.
 */
@Composable
fun rememberEmissiveMaterialInstance(
    color: Color,
    intensity: Float = 1f,
    engine: Engine = LocalFilamentEngine.current,
): MaterialInstance {
    val material = rememberStandardMaterial(StandardMaterial.Emissive, engine)
    return rememberMaterialInstance(material, color, intensity, engine = engine) {
        setParameter("color", color)
        setParameter("intensity", intensity)
    }!!
}

/**
 * A LIT PBR [MaterialInstance] tinted [color] with [alpha] transparency — the "make this object
 * semi-transparent" case with no `.mat` authoring. Alpha is pre-multiplied by the shader and the
 * material renders in two depth passes so convex primitives composite correctly with themselves.
 *
 * Transparent surfaces usually shouldn't cast opaque shadows — pass `castShadows = false` on the
 * primitive for glass-like looks.
 *
 * ```kotlin
 * Sphere(material = rememberTransparentColorMaterialInstance(Color(0.3f, 0.6f, 0.9f), alpha = 0.35f))
 * ```
 *
 * @param color Linear-sRGB base colour.
 * @param alpha Opacity: 0 = fully transparent, 1 = opaque.
 * @param metallic 0 = dielectric, 1 = metal.
 * @param roughness 0 = mirror, 1 = fully rough.
 * @param reflectance Dielectric Fresnel reflectance (0.5 ≈ the common 4% default).
 * @param engine Engine that owns the material. Defaults to the current composition scope's engine.
 */
@Composable
fun rememberTransparentColorMaterialInstance(
    color: Color,
    alpha: Float = 0.5f,
    metallic: Float = 0f,
    roughness: Float = 0.5f,
    reflectance: Float = 0.5f,
    engine: Engine = LocalFilamentEngine.current,
): MaterialInstance {
    val material = rememberStandardMaterial(StandardMaterial.Transparent, engine)
    return rememberMaterialInstance(material, color, alpha, metallic, roughness, reflectance, engine = engine) {
        setParameter("baseColor", color)
        setParameter("alpha", alpha)
        setParameter("metallic", metallic)
        setParameter("roughness", roughness)
        setParameter("reflectance", reflectance)
    }!!
}
