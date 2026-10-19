package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.filamat.testutils.FilamatTestFixture
import io.github.erkko68.filament.testsupport.FrameProbe
import io.github.erkko68.filament.testsupport.IgnoreJs
import io.github.erkko68.filament.testsupport.assumeRealBackend
import io.github.erkko68.filament.testsupport.regionStats
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** A runtime-compiled material's parameter reaches the screen: the frame follows the tint it is given. */
@IgnoreJs // a web readback only lands once the browser gets a frame, which a synchronous test never yields
class CompiledMaterialPixelTest : FilamatTestFixture() {
    override fun createEngine(): Engine {
        assumeRealBackend()
        return Engine.create(Engine.Backend.DEFAULT)!!
    }

    @Test
    fun theTintParameterIsTheColourOnScreen() {
        val material = MaterialBuilder()
            .shading(MaterialBuilder.Shading.UNLIT)
            .targetApi(MaterialBuilder.TargetApi.ALL) // the default is OpenGL only, which Metal rejects
            .parameter("tint", MaterialBuilder.UniformType.FLOAT3)
            .material("void material(inout MaterialInputs m) { prepareMaterial(m); m.baseColor = vec4(materialParams.tint, 1.0); }")
            .load()
        val probe = FrameProbe(engine)
        val instance = probe.instance(material)
        probe.addHorizontalQuad(instance, 0f, 0f, 0f, 8f)
        probe.camera.lookAt(0.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)

        instance.setParameter("tint", 1f, 0f, 0f)
        val red = probe.renderAndRead()
        instance.setParameter("tint", 0f, 0f, 1f)
        val blue = probe.renderAndRead()
        probe.destroy()

        val r = assertNotNull(red, "no readback").regionStats(probe.width, probe.height, 0.25, 0.75, 0.25, 0.75)
        val b = assertNotNull(blue, "no readback").regionStats(probe.width, probe.height, 0.25, 0.75, 0.25, 0.75)
        assertTrue(r.meanR > 150 && r.meanG < 50 && r.meanB < 50, "red tint drew $r")
        assertTrue(b.meanB > 150 && b.meanR < 50 && b.meanG < 50, "blue tint drew $b")
    }
}
