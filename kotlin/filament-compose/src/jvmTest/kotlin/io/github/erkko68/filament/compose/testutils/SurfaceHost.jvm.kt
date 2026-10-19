package io.github.erkko68.filament.compose.testutils

import androidx.compose.runtime.Composable

actual fun surfaceTest(frames: Int, content: @Composable (frame: Int) -> Unit) = composeUiSurfaceTest(frames, content = content)

actual annotation class IgnoreWithoutViewInterop
