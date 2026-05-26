@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.testutils

import io.github.erkko68.filament.NativeSurface
import kotlinx.cinterop.COpaquePointer

actual fun createTestSurface(): NativeSurface = NativeSurface(null)
