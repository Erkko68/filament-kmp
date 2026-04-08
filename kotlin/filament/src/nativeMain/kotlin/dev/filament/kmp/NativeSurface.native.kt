@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.COpaquePointer

/**
 * Native implementation of NativeSurface, wrapping a raw pointer.
 */
actual class NativeSurface(val handler: COpaquePointer?)
