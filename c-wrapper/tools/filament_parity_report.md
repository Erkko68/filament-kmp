# Filament C Wrapper Parity Audit

- Prebuilt headers scanned: **183**
- Wrapper headers scanned: **62**
- Missing wrapper headers: **124**

## Missing Headers

- `backend/platforms/AndroidNdk.h`
- `backend/platforms/OpenGLPlatform.h`
- `backend/platforms/PlatformCocoaGL.h`
- `backend/platforms/PlatformCocoaTouchGL.h`
- `backend/platforms/PlatformEGL.h`
- `backend/platforms/PlatformEGLAndroid.h`
- `backend/platforms/PlatformEGLHeadless.h`
- `backend/platforms/PlatformGLX.h`
- `backend/platforms/PlatformMetal-ObjC.h`
- `backend/platforms/PlatformMetal.h`
- `backend/platforms/PlatformOSMesa.h`
- `backend/platforms/PlatformWGL.h`
- `backend/platforms/PlatformWebGL.h`
- `backend/platforms/VulkanPlatform.h`
- `backend/platforms/VulkanPlatformAndroid.h`
- `backend/platforms/VulkanPlatformApple.h`
- `backend/platforms/VulkanPlatformLinux.h`
- `backend/platforms/VulkanPlatformWindows.h`
- `backend/platforms/WebGPUPlatform.h`
- `backend/platforms/WebGPUPlatformAndroid.h`
- `backend/platforms/WebGPUPlatformApple.h`
- `backend/platforms/WebGPUPlatformLinux.h`
- `backend/platforms/WebGPUPlatformWindows.h`
- `camutils/Bookmark.h`
- `camutils/Manipulator.h`
- `camutils/compiler.h`
- `filamat/Enums.h`
- `filamat/MaterialBuilder.h`
- `filamat/Package.h`
- `filament-generatePrefilterMipmap/generatePrefilterMipmap.h`
- `filament-iblprefilter/IBLPrefilterContext.h`
- `filameshio/MeshReader.h`
- `geometry/SurfaceOrientation.h`
- `geometry/TangentSpaceMesh.h`
- `geometry/Transcoder.h`
- `gltfio/Animator.h`
- `gltfio/AssetLoader.h`
- `gltfio/FilamentAsset.h`
- `gltfio/FilamentInstance.h`
- `gltfio/MaterialProvider.h`
- `gltfio/NodeManager.h`
- `gltfio/ResourceLoader.h`
- `gltfio/TextureProvider.h`
- `gltfio/TrsTransformManager.h`
- `gltfio/materials/uberarchive.h`
- `gltfio/math.h`
- `ibl/Cubemap.h`
- `ibl/CubemapIBL.h`
- `ibl/CubemapSH.h`
- `ibl/CubemapUtils.h`
- `ibl/Image.h`
- `ibl/utilities.h`
- `image/ColorTransform.h`
- `image/ImageOps.h`
- `image/ImageSampler.h`
- `image/Ktx1Bundle.h`
- `image/LinearImage.h`
- `imageio-lite/ImageDecoder.h`
- `imageio-lite/ImageEncoder.h`
- `ktxreader/Ktx1Reader.h`
- `ktxreader/Ktx2Reader.h`
- `math/TMatHelpers.h`
- `math/TQuatHelpers.h`
- `math/TVecHelpers.h`
- `math/compiler.h`
- `math/fast.h`
- `math/half.h`
- `math/mat2.h`
- `math/mat3.h`
- `math/mat4.h`
- `math/mathfwd.h`
- `math/norm.h`
- `math/quat.h`
- `math/scalar.h`
- `math/vec2.h`
- `math/vec3.h`
- `math/vec4.h`
- `mathio/ostream.h`
- `mikktspace/mikktspace.h`
- `tsl/robin_growth_policy.h`
- `tsl/robin_hash.h`
- `tsl/robin_map.h`
- `tsl/robin_set.h`
- `uberz/ArchiveEnums.h`
- `uberz/ReadableArchive.h`
- `uberz/WritableArchive.h`
- `utils/Allocator.h`
- `utils/BitmaskEnum.h`
- `utils/CallStack.h`
- `utils/FixedCapacityVector.h`
- `utils/Hash.h`
- `utils/InternPool.h`
- `utils/Invocable.h`
- `utils/Log.h`
- `utils/Logger.h`
- `utils/LruCache.h`
- `utils/MonotonicRingMap.h`
- `utils/Mutex.h`
- `utils/NameComponentManager.h`
- `utils/Panic.h`
- `utils/PrivateImplementation-impl.h`
- `utils/PrivateImplementation.h`
- `utils/RefCountedMap.h`
- `utils/SingleInstanceComponentManager.h`
- `utils/Slice.h`
- `utils/StaticString.h`
- `utils/StructureOfArrays.h`
- `utils/Systrace.h`
- `utils/algorithm.h`
- `utils/bitset.h`
- `utils/compiler.h`
- `utils/compressed_pair.h`
- `utils/debug.h`
- `utils/generic/Mutex.h`
- `utils/linux/Mutex.h`
- `utils/memalign.h`
- `utils/ostream.h`
- `utils/sstream.h`
- `utils/unwindows.h`
- `viewer/AutomationEngine.h`
- `viewer/AutomationSpec.h`
- `viewer/RemoteServer.h`
- `viewer/Settings.h`
- `viewer/ViewerGui.h`

## Shared Header Method Gaps (Heuristic)

### `backend/AcquiredImage.h` (prebuilt methods: 0, wrapper funcs: 6, covered: 0)

- No unmatched methods detected.

### `backend/BufferDescriptor.h` (prebuilt methods: 3, wrapper funcs: 11, covered: 2)

Missing/Unmatched methods:
- `mCallback`

### `backend/CallbackHandler.h` (prebuilt methods: 2, wrapper funcs: 2, covered: 1)

Missing/Unmatched methods:
- `post`

### `backend/DescriptorSetOffsetArray.h` (prebuilt methods: 4, wrapper funcs: 11, covered: 1)

Missing/Unmatched methods:
- `allocateFromCommandStream`
- `end`
- `uninitialized_fill_n`

### `backend/DriverApiForward.h` (prebuilt methods: 0, wrapper funcs: 4, covered: 0)

- No unmatched methods detected.

### `backend/DriverEnums.h` (prebuilt methods: 9, wrapper funcs: 69, covered: 4)

Missing/Unmatched methods:
- `assert_invariant`
- `bool`
- `int32_t`
- `uint64_t`
- `uint8_t`

### `backend/Handle.h` (prebuilt methods: 3, wrapper funcs: 2, covered: 0)

Missing/Unmatched methods:
- `assert_invariant`
- `getId`
- `move`

### `backend/PipelineState.h` (prebuilt methods: 0, wrapper funcs: 70, covered: 0)

- No unmatched methods detected.

### `backend/PixelBufferDescriptor.h` (prebuilt methods: 3, wrapper funcs: 25, covered: 2)

Missing/Unmatched methods:
- `assert_invariant`

### `backend/Platform.h` (prebuilt methods: 22, wrapper funcs: 13, covered: 2)

Missing/Unmatched methods:
- `clear`
- `createDriver`
- `debugUpdateStat`
- `decref`
- `getDeviceInfo`
- `getOSVersion`
- `hasDebugUpdateStatFunc`
- `hasInsertBlobFunc`
- `hasRetrieveBlobFunc`
- `incref`
- `insertBlob`
- `isCompositorTimingSupported`
- `pumpEvents`
- `queryCompositorTiming`
- `queryFrameTimestamps`
- `reset`
- `retrieveBlob`
- `setBlobFunc`
- `setDebugUpdateStatFunc`
- `setPresentFrameId`

### `backend/PresentCallable.h` (prebuilt methods: 2, wrapper funcs: 6, covered: 2)

- No unmatched methods detected.

### `backend/Program.h` (prebuilt methods: 12, wrapper funcs: 32, covered: 7)

Missing/Unmatched methods:
- `attributes`
- `diagnostics`
- `specializationConstants`
- `uniforms`
- `with_capacity`

### `backend/SamplerDescriptor.h` (prebuilt methods: 0, wrapper funcs: 11, covered: 0)

- No unmatched methods detected.

### `backend/TargetBufferInfo.h` (prebuilt methods: 0, wrapper funcs: 13, covered: 0)

- No unmatched methods detected.

### `filament/Box.h` (prebuilt methods: 6, wrapper funcs: 14, covered: 1)

Missing/Unmatched methods:
- `float3`
- `greaterThanEqual`
- `length2`
- `maximum`
- `upperLeft`

### `filament/BufferObject.h` (prebuilt methods: 7, wrapper funcs: 7, covered: 7)

- No unmatched methods detected.

### `filament/Camera.h` (prebuilt methods: 35, wrapper funcs: 39, covered: 35)

- No unmatched methods detected.

### `filament/Color.h` (prebuilt methods: 8, wrapper funcs: 7, covered: 5)

Missing/Unmatched methods:
- `linearToSRGB`
- `pow`
- `sRGBToLinear`

### `filament/ColorGrading.h` (prebuilt methods: 20, wrapper funcs: 20, covered: 19)

Missing/Unmatched methods:
- `toneMapping`

### `filament/ColorSpace.h` (prebuilt methods: 0, wrapper funcs: 9, covered: 0)

- No unmatched methods detected.

### `filament/DebugRegistry.h` (prebuilt methods: 6, wrapper funcs: 16, covered: 6)

- No unmatched methods detected.

### `filament/Engine.h` (prebuilt methods: 76, wrapper funcs: 108, covered: 68)

Missing/Unmatched methods:
- `getDriver`
- `getEngine`
- `getFeatureFlags`
- `getJobSystem`
- `getPlatform`
- `getSkyboxeCount`
- `has_value`
- `sharedContext`

### `filament/Exposure.h` (prebuilt methods: 6, wrapper funcs: 13, covered: 6)

- No unmatched methods detected.

### `filament/Fence.h` (prebuilt methods: 3, wrapper funcs: 2, covered: 3)

- No unmatched methods detected.

### `filament/FilamentAPI.h` (prebuilt methods: 4, wrapper funcs: 0, covered: 1)

Missing/Unmatched methods:
- `FilamentAPI`
- `builderMakeName`
- `getName`

### `filament/Frustum.h` (prebuilt methods: 6, wrapper funcs: 7, covered: 6)

- No unmatched methods detected.

### `filament/IndexBuffer.h` (prebuilt methods: 10, wrapper funcs: 8, covered: 8)

Missing/Unmatched methods:
- `async`
- `setBufferAsync`

### `filament/IndirectLight.h` (prebuilt methods: 15, wrapper funcs: 15, covered: 15)

- No unmatched methods detected.

### `filament/InstanceBuffer.h` (prebuilt methods: 7, wrapper funcs: 6, covered: 7)

- No unmatched methods detected.

### `filament/LightManager.h` (prebuilt methods: 53, wrapper funcs: 59, covered: 53)

- No unmatched methods detected.

### `filament/Material.h` (prebuilt methods: 43, wrapper funcs: 43, covered: 39)

Missing/Unmatched methods:
- `UserVariantFilterMask`
- `compile`
- `setParameter`
- `strlen`

### `filament/MaterialChunkType.h` (prebuilt methods: 0, wrapper funcs: 0, covered: 0)

- No unmatched methods detected.

### `filament/MaterialEnums.h` (prebuilt methods: 0, wrapper funcs: 0, covered: 0)

- No unmatched methods detected.

### `filament/MaterialInstance.h` (prebuilt methods: 41, wrapper funcs: 77, covered: 40)

Missing/Unmatched methods:
- `strlen`

### `filament/MorphTargetBuffer.h` (prebuilt methods: 15, wrapper funcs: 15, covered: 15)

- No unmatched methods detected.

### `filament/Options.h` (prebuilt methods: 0, wrapper funcs: 17, covered: 0)

- No unmatched methods detected.

### `filament/RenderTarget.h` (prebuilt methods: 13, wrapper funcs: 14, covered: 13)

- No unmatched methods detected.

### `filament/RenderableManager.h` (prebuilt methods: 72, wrapper funcs: 79, covered: 68)

Missing/Unmatched methods:
- `bmax`
- `bmin`
- `max`
- `min`

### `filament/Renderer.h` (prebuilt methods: 22, wrapper funcs: 23, covered: 22)

- No unmatched methods detected.

### `filament/Scene.h` (prebuilt methods: 15, wrapper funcs: 16, covered: 15)

- No unmatched methods detected.

### `filament/SkinningBuffer.h` (prebuilt methods: 7, wrapper funcs: 7, covered: 7)

- No unmatched methods detected.

### `filament/Skybox.h` (prebuilt methods: 12, wrapper funcs: 13, covered: 12)

- No unmatched methods detected.

### `filament/Stream.h` (prebuilt methods: 10, wrapper funcs: 10, covered: 10)

- No unmatched methods detected.

### `filament/SwapChain.h` (prebuilt methods: 6, wrapper funcs: 4, covered: 5)

Missing/Unmatched methods:
- `isFrameScheduledCallbackSet`

### `filament/Sync.h` (prebuilt methods: 2, wrapper funcs: 1, covered: 2)

- No unmatched methods detected.

### `filament/Texture.h` (prebuilt methods: 37, wrapper funcs: 36, covered: 34)

Missing/Unmatched methods:
- `async`
- `move`
- `setImageAsync`

### `filament/TextureSampler.h` (prebuilt methods: 5, wrapper funcs: 17, covered: 1)

Missing/Unmatched methods:
- `TextureSampler`
- `float`
- `ilogbf`
- `uint8_t`

### `filament/ToneMapper.h` (prebuilt methods: 18, wrapper funcs: 27, covered: 18)

- No unmatched methods detected.

### `filament/TransformManager.h` (prebuilt methods: 25, wrapper funcs: 30, covered: 24)

Missing/Unmatched methods:
- `ret`

### `filament/VertexBuffer.h` (prebuilt methods: 16, wrapper funcs: 13, covered: 13)

Missing/Unmatched methods:
- `async`
- `setBufferAtAsync`
- `setBufferObjectAtAsync`

### `filament/View.h` (prebuilt methods: 85, wrapper funcs: 78, covered: 81)

Missing/Unmatched methods:
- `getSampleCount`
- `move`
- `pick`
- `setSampleCount`

### `filament/Viewport.h` (prebuilt methods: 0, wrapper funcs: 0, covered: 0)

- No unmatched methods detected.

### `utils/CString.h` (prebuilt methods: 22, wrapper funcs: 11, covered: 4)

Missing/Unmatched methods:
- `FixedSizeString`
- `assert`
- `begin`
- `c_str`
- `compare`
- `do_tracking`
- `end`
- `insert`
- `length`
- `move`
- `replace`
- `size_t`
- `string_view`
- `strlen`
- `strncpy`
- `swap`
- `to_string`
- `track`

### `utils/Entity.h` (prebuilt methods: 3, wrapper funcs: 4, covered: 3)

- No unmatched methods detected.

### `utils/EntityInstance.h` (prebuilt methods: 2, wrapper funcs: 3, covered: 2)

- No unmatched methods detected.

### `utils/EntityManager.h` (prebuilt methods: 11, wrapper funcs: 16, covered: 11)

- No unmatched methods detected.

### `utils/ImmutableCString.h` (prebuilt methods: 10, wrapper funcs: 9, covered: 0)

Missing/Unmatched methods:
- `assert`
- `begin`
- `compare`
- `do_tracking`
- `end`
- `free`
- `initializeFrom`
- `strlen`
- `swap`
- `track`

### `utils/Path.h` (prebuilt methods: 25, wrapper funcs: 26, covered: 18)

Missing/Unmatched methods:
- `c_str`
- `getCanonicalPath`
- `getExtension`
- `getName`
- `getNameWithoutExtension`
- `listContents`
- `split`

### `utils/Status.h` (prebuilt methods: 2, wrapper funcs: 12, covered: 2)

- No unmatched methods detected.

