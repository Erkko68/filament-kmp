package io.github.erkko68.filament

import kotlin.test.Test
import kotlin.test.assertEquals

/** On desktop a window or shared context is a raw address; anything else counts as none. */
class DesktopHandlesTest {
    @Test
    fun nativeSurfaceTakesAnAddress() {
        assertEquals(0x1234L, NativeSurface(0x1234L).handle)
        assertEquals(0x1234L, NativeSurface(0x1234).handle)
        assertEquals(0L, NativeSurface("not an address").handle)
        assertEquals(0x1234L, acquireWindow(NativeSurface(0x1234L)))
    }

    @Test
    fun onlyAnAddressIsASharedContext() {
        assertEquals(42L, sharedContextPointer(42L))
        assertEquals(0L, sharedContextPointer("not an address"))
    }
}
