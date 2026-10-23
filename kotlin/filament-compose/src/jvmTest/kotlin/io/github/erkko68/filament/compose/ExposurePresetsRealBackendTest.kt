package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.Exposure
import io.github.erkko68.filament.compose.scene.LightIntensity
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.PointLight
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberColorMaterialInstance
import io.github.erkko68.filament.compose.testutils.renderFrames
import io.github.erkko68.filament.compose.testutils.withEngine
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Each light preset, paired with the exposure its KDoc names, shows a white surface as a mid-tone; swapped, it
 * clips or goes black. Skips without a GPU.
 *
 * Upstream (Exposure.cpp, LightManager.cpp): exposure = 1 / (1.2 · N²/t · 100/S), a point light's candela is
 * lumens / 4π, and a white diffuse surface lit head-on shows E/π. So Sunny + Sun ≈ 0.83, Indoor + a bulb 1 m
 * away ≈ 0.26, Indoor + Sun ≈ 414 and Sunny + the bulb ≈ 0.0005, before tone mapping.
 */
class ExposurePresetsRealBackendTest {

    private enum class Light { Sun, Bulb }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun matchedPairsExposeAWhiteSurfaceAsAMidTone() = withEngine(Engine.Backend.DEFAULT) { engine, setContent ->
        var exposure by mutableStateOf(Exposure.Sunny)
        var light by mutableStateOf(Light.Sun)
        setContent {
            // Looking straight at the cube's +Z face, at z = -25.
            val cam = rememberCameraState(initialEye = Position(0f, 0f, 10f), initialTarget = Position(0f, 0f, -25f))
            cam.exposure = exposure
            FilamentSceneView(Modifier.testTag("view").size(64.dp), engine = engine, cameraState = cam) {
                when (light) {
                    Light.Sun -> DirectionalLight(direction = Direction(0f, 0f, -1f), intensity = LightIntensity.Sun)
                    Light.Bulb -> PointLight(position = Position(0f, 0f, -24f), intensity = LightIntensity.HouseholdBulb)
                }
                // Fully rough: no highlight on the axis the light and camera share.
                Cube(rememberColorMaterialInstance(LinearColor(1f), roughness = 1f), size = 50f, position = Position(0f, 0f, -50f))
            }
        }
        fun centre(e: Exposure, l: Light): Float {
            exposure = e
            light = l
            renderFrames(engine, 10)
            val pixels = onNodeWithTag("view").captureToImage().toPixelMap()
            return pixels[pixels.width / 2, pixels.height / 2].let { maxOf(it.red, it.green, it.blue) }
        }

        val sunny = centre(Exposure.Sunny, Light.Sun)
        val indoor = centre(Exposure.Indoor, Light.Bulb)
        val clipped = centre(Exposure.Indoor, Light.Sun)
        val crushed = centre(Exposure.Sunny, Light.Bulb)
        assertTrue(sunny in 0.15f..0.95f, "Sunny + Sun should be a mid-tone, got $sunny")
        assertTrue(indoor in 0.15f..0.95f, "Indoor + HouseholdBulb should be a mid-tone, got $indoor")
        assertTrue(clipped > 0.97f, "Indoor + Sun should clip, got $clipped")
        assertTrue(crushed < 0.03f, "Sunny + HouseholdBulb should be black, got $crushed")
    }
}
