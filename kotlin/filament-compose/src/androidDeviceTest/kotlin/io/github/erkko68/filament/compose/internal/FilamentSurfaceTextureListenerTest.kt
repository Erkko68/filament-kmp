package io.github.erkko68.filament.compose.internal

import android.graphics.SurfaceTexture
import android.view.Surface
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Guards the [Surface] ownership in the TextureView path: created once, released on destroy. */
class FilamentSurfaceTextureListenerTest {
    private val surfaceTexture = SurfaceTexture(0)

    @AfterTest
    fun tearDown() = surfaceTexture.release()

    @Test
    fun releasesSurfaceAfterDestroyCallback() {
        var surface: Surface? = null
        var destroyedWhileValid = false
        val listener = filamentSurfaceTextureListener(
            onAvailable = { s, _, _ -> surface = s },
            onResized = { _, _ -> },
            // The swapchain teardown must still see a live surface.
            onDestroyed = { destroyedWhileValid = surface?.isValid == true },
        )

        listener.onSurfaceTextureAvailable(surfaceTexture, 64, 32)
        val created = assertNotNull(surface)
        assertTrue(created.isValid)

        assertTrue(listener.onSurfaceTextureDestroyed(surfaceTexture))
        assertTrue(destroyedWhileValid, "surface was released before the destroy callback ran")
        assertFalse(created.isValid, "surface leaked — not released on destroy")
    }

    @Test
    fun forwardsSizeChanges() {
        var size: Pair<Int, Int>? = null
        val listener = filamentSurfaceTextureListener(
            onAvailable = { _, _, _ -> },
            onResized = { w, h -> size = w to h },
            onDestroyed = {},
        )
        listener.onSurfaceTextureSizeChanged(surfaceTexture, 128, 64)
        assertEquals(128 to 64, size)
    }
}
