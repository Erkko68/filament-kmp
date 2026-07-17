package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * May be either a string exactly containing a URL loaded with Filament.init() or Filament.fetch(),
 * OR any TypedArray such as Uint8Array, Float32Array, etc., all of which match the ArrayBufferView
 * interface.
 */
typealias BufferReference = JsAny /* string | ArrayBufferView */
