// Hand-maintained external — the `Engine$Config` value_object in jsbindings.cpp.
// Used both ways: `Engine.getConfig()` returns one, `Engine.create(canvas, options, config)`
// takes one (merged over `createDefaultConfig()`, so partial objects are fine).

package io.github.erkko68.filament.web

external interface Engine_Config : JsAny {
var commandBufferSizeMB: Double?
var perRenderPassArenaSizeMB: Double?
var driverHandleArenaSizeMB: Double?
var minCommandBufferSizeMB: Double?
var perFrameCommandsSizeMB: Double?
var jobSystemThreadCount: Double?
var disableParallelShaderCompile: Boolean?
var stereoscopicType: StereoscopicType?
var stereoscopicEyeCount: Double?
var resourceAllocatorCacheSizeMB: Double?
var resourceAllocatorCacheMaxAge: Double?
var disableHandleUseAfterFreeCheck: Boolean?
var preferredShaderLanguage: ShaderLanguage?
var forceGLES2Context: Boolean?
var assertNativeWindowIsValid: Boolean?
var gpuContextPriority: GpuContextPriority?
var sharedUboInitialSizeInBytes: Double?
var enableMultipleDirectionalLights: Boolean?
}
