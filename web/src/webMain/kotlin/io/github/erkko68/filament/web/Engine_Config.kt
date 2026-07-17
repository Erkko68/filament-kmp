package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

// Returned by Engine.getConfig(); the upstream d.ts only types the *input*
// config object (Engine.create options), not the getter's result.
external interface Engine_Config : JsAny {
var commandBufferSizeMB: Double?
var perRenderPassArenaSizeMB: Double?
var driverHandleArenaSizeMB: Double?
var minCommandBufferSizeMB: Double?
var perFrameCommandsSizeMB: Double?
var jobSystemThreadCount: Double?
var stereoscopicEyeCount: Double?
var resourceAllocatorCacheSizeMB: Double?
var resourceAllocatorCacheMaxAge: Double?
var sharedUboInitialSizeInBytes: Double?
var forceGLES2Context: Boolean?
}
