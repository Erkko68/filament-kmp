package io.github.erkko68.filament.compose.testutils

import androidx.compose.runtime.Composable

// The window creates and destroys a SurfaceView's surface on its own real-time frames, not the test clock's.
actual fun surfaceTest(frames: Int, content: @Composable (frame: Int) -> Unit) =
    composeUiSurfaceTest(frames, afterFrame = { Thread.sleep(20) }, content = content)

actual annotation class IgnoreWithoutViewInterop
