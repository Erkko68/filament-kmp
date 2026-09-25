package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Verifies [Shadows.applyTo] toggles shadowing and selects the right [View.ShadowType]. Views are
 * freed by the engine on fixture teardown.
 */
class ShadowsApplyTest : ComposeTestFixture() {

    private fun newView(): View = engine.createView()

    @Test
    fun nullDisablesShadowing() {
        val view = newView()
        val shadows: Shadows? = Shadows.Pcf
        shadows.applyTo(view)
        assertTrue(view.isShadowingEnabled)
        val none: Shadows? = null
        none.applyTo(view)
        assertFalse(view.isShadowingEnabled)
    }

    @Test
    fun eachTechniqueSelectsItsShadowType() {
        val view = newView()
        Shadows.Pcf.applyTo(view)
        assertEquals(View.ShadowType.PCF, view.shadowType)

        Shadows.Pcfd.applyTo(view)
        assertEquals(View.ShadowType.PCFd, view.shadowType)

        Shadows.Vsm(highPrecision = true).applyTo(view)
        assertEquals(View.ShadowType.VSM, view.shadowType)
        assertTrue(view.vsmShadowOptions.highPrecision)

        Shadows.Dpcf(penumbraScale = 2f).applyTo(view)
        assertEquals(View.ShadowType.DPCF, view.shadowType)
        assertEquals(2f, view.softShadowOptions.penumbraScale)

        Shadows.Pcss().applyTo(view)
        assertEquals(View.ShadowType.PCSS, view.shadowType)
    }
}
