package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.scene.IndirectLight
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.SphericalHarmonics
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.rememberColorMaterialInstance
import io.github.erkko68.filament.compose.testutils.renderFrames
import io.github.erkko68.filament.compose.testutils.withEngine
import kotlin.test.Test
import kotlin.test.assertTrue

/** The built-in ambients light a lit material on their own (no direct light, no texture), with the tint they claim. Skips without a GPU. */
class AmbientPresetsRealBackendTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun eachPresetAloneLightsALitCube() = withEngine(Engine.Backend.DEFAULT) { engine, setContent ->
        var ambient by mutableStateOf<SphericalHarmonics?>(null)
        setContent {
            FilamentSceneView(Modifier.testTag("view").size(64.dp), engine = engine) {
                ambient?.let { IndirectLight(irradiance = it) }
                Cube(rememberColorMaterialInstance(LinearColor(1f, 1f, 1f)), size = 50f, position = Position(0f, 0f, -50f))
            }
        }
        fun centre(): Color {
            renderFrames(engine, 10)
            val pixels = onNodeWithTag("view").captureToImage().toPixelMap()
            return pixels[pixels.width / 2, pixels.height / 2]
        }

        val dark = centre()
        assertTrue(dark.red < 0.02f && dark.green < 0.02f && dark.blue < 0.02f, "without any light the lit cube should be black, got $dark")
        val presets = with(SphericalHarmonics) {
            mapOf("NeutralStudio" to NeutralStudio, "CoolStudio" to CoolStudio, "Daylight" to Daylight, "Sunset" to Sunset, "Warm" to Warm)
        }
        val lit = presets.mapValues { (_, sh) -> ambient = sh; centre() }
        lit.forEach { (name, c) -> assertTrue(maxOf(c.red, c.green, c.blue) > 0.1f, "$name alone should light the cube, got $c") }
        // Against the grey studio, so the view's own tone mapping cancels out.
        val neutral = lit.getValue("NeutralStudio").let { it.blue / it.red }
        with(lit.getValue("Daylight")) { assertTrue(blue / red > neutral, "Daylight should be bluer than the neutral studio, got $this") }
        with(lit.getValue("CoolStudio")) { assertTrue(blue / red > neutral, "CoolStudio should be bluer than the neutral studio, got $this") }
        with(lit.getValue("Warm")) { assertTrue(blue / red < neutral, "Warm should be warmer than the neutral studio, got $this") }
    }
}
