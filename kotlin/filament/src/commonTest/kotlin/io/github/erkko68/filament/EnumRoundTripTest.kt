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
        roundTrip<BlendMode>("View.blendMode", { view.blendMode = it }, { view.blendMode })
        roundTrip<Dithering>("View.dithering", { view.dithering = it }, { view.dithering })
        roundTrip<AntiAliasing>("View.antiAliasing", { view.antiAliasing = it }, { view.antiAliasing })
        roundTrip<ShadowType>("View.shadowType", { view.shadowType = it }, { view.shadowType })
        engine.destroy(view)
    }

    @Test
    fun viewOptionEnumsRoundTripEveryEntry() {
        val view = engine.createView()
        roundTrip<BloomOptions.BlendMode>(
            "BloomOptions.blendMode",
            { view.bloomOptions = BloomOptions().apply { blendMode = it } },
            { view.bloomOptions.blendMode },
        )
        roundTrip<QualityLevel>(
            "DynamicResolutionOptions.quality",
            { view.dynamicResolutionOptions = DynamicResolutionOptions().apply { quality = it } },
            { view.dynamicResolutionOptions.quality },
        )
        roundTrip<QualityLevel>(
            "RenderQuality.hdrColorBuffer",
            { view.renderQuality = RenderQuality().apply { hdrColorBuffer = it } },
            { view.renderQuality.hdrColorBuffer },
        )
        roundTrip<DepthOfFieldOptions.Filter>(
            "DepthOfFieldOptions.filter",
            { view.depthOfFieldOptions = DepthOfFieldOptions().apply { filter = it } },
            { view.depthOfFieldOptions.filter },
        )
        roundTrip<QualityLevel>(
            "AmbientOcclusionOptions.quality",
            { view.ambientOcclusionOptions = AmbientOcclusionOptions().apply { quality = it } },
            { view.ambientOcclusionOptions.quality },
        )
        roundTrip<QualityLevel>(
            "AmbientOcclusionOptions.lowPassFilter",
            { view.ambientOcclusionOptions = AmbientOcclusionOptions().apply { lowPassFilter = it } },
            { view.ambientOcclusionOptions.lowPassFilter },
        )
        roundTrip<QualityLevel>(
            "AmbientOcclusionOptions.upsampling",
            { view.ambientOcclusionOptions = AmbientOcclusionOptions().apply { upsampling = it } },
            { view.ambientOcclusionOptions.upsampling },
        )
        roundTrip<AmbientOcclusionOptions.AmbientOcclusionType>(
            "AmbientOcclusionOptions.aoType",
            { view.ambientOcclusionOptions = AmbientOcclusionOptions().apply { aoType = it } },
            { view.ambientOcclusionOptions.aoType },
        )
        roundTrip<TemporalAntiAliasingOptions.BoxType>(
            "TemporalAntiAliasingOptions.boxType",
            { view.temporalAntiAliasingOptions = TemporalAntiAliasingOptions().apply { boxType = it } },
            { view.temporalAntiAliasingOptions.boxType },
        )
        roundTrip<TemporalAntiAliasingOptions.BoxClipping>(
            "TemporalAntiAliasingOptions.boxClipping",
            { view.temporalAntiAliasingOptions = TemporalAntiAliasingOptions().apply { boxClipping = it } },
            { view.temporalAntiAliasingOptions.boxClipping },
        )
        roundTrip<TemporalAntiAliasingOptions.JitterPattern>(
            "TemporalAntiAliasingOptions.jitterPattern",
            { view.temporalAntiAliasingOptions = TemporalAntiAliasingOptions().apply { jitterPattern = it } },
            { view.temporalAntiAliasingOptions.jitterPattern },
        )
        engine.destroy(view)
    }

    @Test
    fun textureSamplerEnumsRoundTripEveryEntry() {
        val s = TextureSampler()
        roundTrip<TextureSampler.MinFilter>("TextureSampler.minFilter", { s.minFilter = it }, { s.minFilter })
        roundTrip<TextureSampler.MagFilter>("TextureSampler.magFilter", { s.magFilter = it }, { s.magFilter })
        roundTrip<TextureSampler.WrapMode>("TextureSampler.wrapModeS", { s.wrapModeS = it }, { s.wrapModeS })
        roundTrip<TextureSampler.WrapMode>("TextureSampler.wrapModeT", { s.wrapModeT = it }, { s.wrapModeT })
        roundTrip<TextureSampler.WrapMode>("TextureSampler.wrapModeR", { s.wrapModeR = it }, { s.wrapModeR })
        roundTrip<TextureSampler.CompareMode>("TextureSampler.compareMode", { s.setCompareMode(it, s.compareFunc) }, { s.compareMode })
        roundTrip<TextureSampler.CompareFunc>("TextureSampler.compareFunc", { s.setCompareMode(s.compareMode, it) }, { s.compareFunc })
    }
}

/** GPU-fixture variant for enums that need a real material instance behind them. */
class MaterialInstanceEnumRoundTripTest : RenderingTestFixture() {

    @Test
    fun materialInstanceEnumsRoundTripEveryEntry() {
        val engine = engine ?: return
        val mat = Material.Builder().payload(TestMaterials.getEmissiveMaterialBytes()).build(engine)!!
        val inst = mat.createInstance()

        roundTrip<Material.CullingMode>("MaterialInstance.cullingMode", { inst.cullingMode = it }, { inst.cullingMode })
        roundTrip<TextureSampler.CompareFunc>(
            "MaterialInstance.depthFunc", { inst.depthFunc = it }, { inst.depthFunc },
        )

        engine.destroy(inst)
        engine.destroy(mat)
    }
}
