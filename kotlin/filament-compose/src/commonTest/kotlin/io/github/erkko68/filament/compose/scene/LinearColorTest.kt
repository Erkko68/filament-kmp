package io.github.erkko68.filament.compose.scene

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import androidx.compose.ui.graphics.Color as ComposeColor

/**
 * Pins the sRGB transfer function on [LinearColor]'s Compose-UI interop. Filament works in linear
 * space while `androidx.compose.ui.graphics.Color` is gamma-encoded, so copying components across
 * raw — which the pre-rename `Color(composeColor)` constructor did — leaves every mid-tone far too
 * bright. These are pure value conversions, so no engine fixture is needed.
 */
class LinearColorTest {

    /**
     * sRGB 0.5 decodes to linear ≈0.2140 — a 0.29× change, which is the whole point: the raw
     * component copy this replaces passed 0.5 straight through. Tolerance is loose because Compose
     * converts through a 1024-entry LUT, so results land on ~0.001 steps rather than the exact
     * analytic value.
     */
    @Test
    fun fromComposeColorAppliesSrgbDecode() {
        val linear = LinearColor.fromComposeColor(ComposeColor(0.5f, 0.5f, 0.5f))

        assertEquals(0.2140f, linear.r, 0.005f)
        assertEquals(0.2140f, linear.g, 0.005f)
        assertEquals(0.2140f, linear.b, 0.005f)
    }

    /** The endpoints are fixed points of the transfer function in both directions. */
    @Test
    fun endpointsAreExact() {
        val black = LinearColor.fromComposeColor(ComposeColor(0f, 0f, 0f))
        assertEquals(0f, black.r, 1e-5f)

        val white = LinearColor.fromComposeColor(ComposeColor(1f, 1f, 1f))
        assertEquals(1f, white.r, 1e-5f)

        assertEquals(0f, LinearColor(0f).toComposeColor().red, 1e-5f)
        assertEquals(1f, LinearColor(1f).toComposeColor().red, 1e-5f)
    }

    @Test
    fun composeRoundTripPreservesValue() {
        for (channel in listOf(0.05f, 0.25f, 0.5f, 0.75f, 0.95f)) {
            val original = ComposeColor(channel, channel * 0.5f, 1f - channel)
            val back = LinearColor.fromComposeColor(original).toComposeColor()

            assertEquals(original.red, back.red, 0.002f)
            assertEquals(original.green, back.green, 0.002f)
            assertEquals(original.blue, back.blue, 0.002f)
        }
    }

    /** Linear values are always ≤ their gamma-encoded source below white — the direction matters. */
    @Test
    fun decodingDarkensMidTones() {
        val linear = LinearColor.fromComposeColor(ComposeColor(0.6f, 0.6f, 0.6f))
        assertTrue(linear.r < 0.6f, "expected sRGB decode to darken 0.6, got ${linear.r}")
    }

    /** Lights accept over-bright channels; converting back to Compose has to clamp them. */
    @Test
    fun toComposeColorClampsOverbright() {
        assertEquals(1f, LinearColor(4f, 4f, 4f).toComposeColor().red, 1e-5f)
    }
}
