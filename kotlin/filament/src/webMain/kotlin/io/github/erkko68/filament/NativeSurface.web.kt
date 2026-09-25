package io.github.erkko68.filament

import org.w3c.dom.HTMLCanvasElement

/** On web a swap chain renders to its engine's WebGL canvas; [canvas] names which one. */
actual class NativeSurface(internal val canvas: HTMLCanvasElement)
