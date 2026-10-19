package io.github.erkko68.filament.utils

import io.github.erkko68.filament.TextureSampler
import io.github.erkko68.filament.filamat.MaterialBuilder
import io.github.erkko68.filament.testsupport.FrameProbe
import io.github.erkko68.filament.testsupport.IgnoreJs
import io.github.erkko68.filament.testsupport.RealBackendFixture
import io.github.erkko68.filament.testsupport.regionStats
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** A texture decoded by [TextureLoader] is what a material samples on screen. */
@IgnoreJs // a web readback only lands once the browser gets a frame, which a synchronous test never yields
class LoadedTexturePixelTest : RealBackendFixture() {
    @Test
    fun aDecodedPngIsSampledOnScreen() {
        MaterialBuilder.init()
        val pkg = MaterialBuilder()
            .shading(MaterialBuilder.Shading.UNLIT)
            .targetApi(MaterialBuilder.TargetApi.ALL)
            .parameter("tex", MaterialBuilder.SamplerType.SAMPLER_2D)
            .material("void material(inout MaterialInputs m) { prepareMaterial(m); m.baseColor = texture(materialParams_tex, vec2(0.5)); }")
            .build()
        MaterialBuilder.shutdown()
        assertTrue(pkg.isValid, "material did not compile")

        val texture = assertNotNull(TextureLoader.loadTexture(engine, TextureReadersTest.ONE_PIXEL_PNG, TextureLoader.TextureType.COLOR))
        val probe = FrameProbe(engine)
        val instance = probe.instance(probe.material(pkg.data))
        instance.setParameter("tex", texture, TextureSampler())
        probe.addHorizontalQuad(instance, 0f, 0f, 0f, 8f)
        probe.camera.lookAt(0.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)
        val pixels = probe.renderAndRead()
        probe.destroy()
        engine.destroy(texture)

        val centre = assertNotNull(pixels, "no readback").regionStats(probe.width, probe.height, 0.25, 0.75, 0.25, 0.75)
        assertTrue(centre.meanR > 150 && centre.meanG < 50 && centre.meanB < 50, "the red PNG drew $centre")
    }
}
