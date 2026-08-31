package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * Asynchronously initializes the WebGPU adapter and device.
 * This must be awaited before initializing the Filament Engine with the WebGPU backend.
 */
external fun initWebGPU(): js.promise.Promise<JsAny?>
