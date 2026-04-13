@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.COpaquePointer

/**
 * Native implementation of NativeSurface, wrapping a raw pointer.
 */
actual class NativeSurface(val handler: COpaquePointer?)
