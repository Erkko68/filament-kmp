package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testutils.RenderingTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Round-trips *every entry* of every gettable enum-typed property through the
 * binding layer. Enum marshalling is a `when` per entry per platform, and a
 * misaligned arm fails silently at runtime (the historical `Backend`/
 * `StereoscopicType` misalignment shipped exactly that way) — iterating the
 * full entry set turns that bug class into a test failure. Generalizes what
 * [ViewOptionsRoundTripTest] does for option-struct fields.
 */
private inline fun <reified E : Enum<E>> roundTrip(property: String, set: (E) -> Unit, get: () -> E) {
    for (entry in enumValues<E>()) {
        set(entry)
        assertEquals(entry, get(), "$property did not round-trip $entry")
    }
}

class EnumRoundTripTest : FilamentTestFixture() {

    @Test
    fun viewEnumsRoundTripEveryEntry() {
        val view = engine.createView()
        roundTrip<View.BlendMode>("View.blendMode", { view.blendMode = it }, { view.blendMode })
        roundTrip<View.Dithering>("View.dithering", { view.dithering = it }, { view.dithering })
        roundTrip<View.AntiAliasing>("View.antiAliasing", { view.antiAliasing = it }, { view.antiAliasing })
        roundTrip<View.ShadowType>("View.shadowType", { view.shadowType = it }, { view.shadowType })
        engine.destroyView(view)
    }

    @Test
    fun viewOptionEnumsRoundTripEveryEntry() {
        val view = engine.createView()
        roundTrip<View.BloomOptions.BlendMode>(
            "BloomOptions.blendMode",
            { view.bloomOptions = View.BloomOptions().apply { blendMode = it } },
            { view.bloomOptions.blendMode },
        )
        roundTrip<View.Quality>(
            "DynamicResolutionOptions.quality",
            { view.dynamicResolutionOptions = View.DynamicResolutionOptions().apply { quality = it } },
            { view.dynamicResolutionOptions.quality },
        )
        roundTrip<View.Quality>(
            "RenderQuality.hdrColorBuffer",
            { view.renderQuality = View.RenderQuality().apply { hdrColorBuffer = it } },
            { view.renderQuality.hdrColorBuffer },
        )
        roundTrip<View.DepthOfFieldOptions.Filter>(
            "DepthOfFieldOptions.filter",
            { view.depthOfFieldOptions = View.DepthOfFieldOptions().apply { filter = it } },
            { view.depthOfFieldOptions.filter },
        )
        roundTrip<View.Quality>(
            "AmbientOcclusionOptions.quality",
            { view.ambientOcclusionOptions = View.AmbientOcclusionOptions().apply { quality = it } },
            { view.ambientOcclusionOptions.quality },
        )
        roundTrip<View.Quality>(
            "AmbientOcclusionOptions.lowPassFilter",
            { view.ambientOcclusionOptions = View.AmbientOcclusionOptions().apply { lowPassFilter = it } },
            { view.ambientOcclusionOptions.lowPassFilter },
        )
        roundTrip<View.Quality>(
            "AmbientOcclusionOptions.upsampling",
            { view.ambientOcclusionOptions = View.AmbientOcclusionOptions().apply { upsampling = it } },
            { view.ambientOcclusionOptions.upsampling },
        )
        engine.destroyView(view)
    }

    @Test
    fun textureSamplerEnumsRoundTripEveryEntry() {
        val s = TextureSampler()
        roundTrip<TextureSampler.MinFilter>("TextureSampler.minFilter", { s.minFilter = it }, { s.minFilter })
        roundTrip<TextureSampler.MagFilter>("TextureSampler.magFilter", { s.magFilter = it }, { s.magFilter })
        roundTrip<TextureSampler.WrapMode>("TextureSampler.wrapModeS", { s.wrapModeS = it }, { s.wrapModeS })
        roundTrip<TextureSampler.WrapMode>("TextureSampler.wrapModeT", { s.wrapModeT = it }, { s.wrapModeT })
        roundTrip<TextureSampler.WrapMode>("TextureSampler.wrapModeR", { s.wrapModeR = it }, { s.wrapModeR })
        roundTrip<TextureSampler.CompareMode>("TextureSampler.compareMode", { s.compareMode = it }, { s.compareMode })
        roundTrip<TextureSampler.CompareFunction>(
            "TextureSampler.compareFunction", { s.compareFunction = it }, { s.compareFunction },
        )
    }
}

/** GPU-fixture variant for enums that need a real material instance behind them. */
class MaterialInstanceEnumRoundTripTest : RenderingTestFixture() {

    @Test
    fun materialInstanceEnumsRoundTripEveryEntry() {
        val engine = engine ?: return
        val mat = Material.Builder().payload(TestMaterials.getEmissiveMaterialBytes()).build(engine)
        val inst = mat.createInstance()

        roundTrip<Material.CullingMode>("MaterialInstance.cullingMode", { inst.cullingMode = it }, { inst.cullingMode })
        roundTrip<TextureSampler.CompareFunction>(
            "MaterialInstance.depthFunc", { inst.depthFunc = it }, { inst.depthFunc },
        )

        engine.destroyMaterialInstance(inst)
        engine.destroyMaterial(mat)
    }
}
