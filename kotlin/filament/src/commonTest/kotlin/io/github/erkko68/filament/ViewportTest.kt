package io.github.erkko68.filament

import kotlin.test.Test
import kotlin.test.assertEquals

class ViewportTest {
    @Test
    fun constructorSetsFields() {
        val vp = Viewport(1, 2, 3, 4)
        assertEquals(1, vp.left)
        assertEquals(2, vp.bottom)
        assertEquals(3, vp.width)
        assertEquals(4, vp.height)
    }

    @Test
    fun propertyRoundTrip() {
        val vp = Viewport()
        vp.left = 10
        vp.bottom = 20
        vp.width = 30
        vp.height = 40
        assertEquals(10, vp.left)
        assertEquals(20, vp.bottom)
        assertEquals(30, vp.width)
        assertEquals(40, vp.height)
    }

    @Test
    fun defaultConstructorIsZero() {
        val vp = Viewport()
        assertEquals(0, vp.left)
        assertEquals(0, vp.bottom)
        assertEquals(0, vp.width)
        assertEquals(0, vp.height)
    }
}
