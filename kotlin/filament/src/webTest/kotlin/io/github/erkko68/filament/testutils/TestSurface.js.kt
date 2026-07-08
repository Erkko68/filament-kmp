package io.github.erkko68.filament.testutils

import io.github.erkko68.filament.NativeSurface
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

actual fun createTestSurface(): NativeSurface {
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    return NativeSurface(canvas)
}
