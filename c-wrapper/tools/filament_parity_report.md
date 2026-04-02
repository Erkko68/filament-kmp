# Filament C Wrapper Parity Audit

- Prebuilt headers scanned: **183**
- Wrapper headers scanned: **74**
- Missing wrapper headers: **36**

## Missing Headers

- `filameshio/MeshReader.h`
- `gltfio/NodeManager.h`
- `gltfio/TrsTransformManager.h`
- `gltfio/materials/uberarchive.h`
- `gltfio/math.h`
- `utils/Allocator.h`
- `utils/BitmaskEnum.h`
- `utils/CString.h`
- `utils/FixedCapacityVector.h`
- `utils/Hash.h`
- `utils/ImmutableCString.h`
- `utils/InternPool.h`
- `utils/Invocable.h`
- `utils/Logger.h`
- `utils/LruCache.h`
- `utils/MonotonicRingMap.h`
- `utils/Mutex.h`
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

## Shared Header Method Gaps (Heuristic)

### `backend/AcquiredImage.h` (prebuilt methods: 0, wrapper funcs: 6, covered: 0)

- No unmatched methods detected.

### `backend/BufferDescriptor.h` (prebuilt methods: 3, wrapper funcs: 16, covered: 2)

Missing/Unmatched methods:
- `mCallback`

### `backend/CallbackHandler.h` (prebuilt methods: 2, wrapper funcs: 3, covered: 1)

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

### `backend/PixelBufferDescriptor.h` (prebuilt methods: 3, wrapper funcs: 34, covered: 2)

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

### `filament/BufferObject.h` (prebuilt methods: 5, wrapper funcs: 7, covered: 5)

- No unmatched methods detected.

### `filament/Camera.h` (prebuilt methods: 35, wrapper funcs: 39, covered: 35)

- No unmatched methods detected.

### `filament/Color.h` (prebuilt methods: 8, wrapper funcs: 7, covered: 5)

Missing/Unmatched methods:
- `linearToSRGB`
- `pow`
- `sRGBToLinear`

### `filament/ColorGrading.h` (prebuilt methods: 17, wrapper funcs: 20, covered: 17)

- No unmatched methods detected.

### `filament/ColorSpace.h` (prebuilt methods: 0, wrapper funcs: 9, covered: 0)

- No unmatched methods detected.

### `filament/DebugRegistry.h` (prebuilt methods: 6, wrapper funcs: 16, covered: 6)

- No unmatched methods detected.

### `filament/Engine.h` (prebuilt methods: 76, wrapper funcs: 108, covered: 71)

Missing/Unmatched methods:
- `getDriver`
- `getEngine`
- `getFeatureFlags`
- `getJobSystem`
- `getPlatform`

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

### `filament/IndexBuffer.h` (prebuilt methods: 8, wrapper funcs: 8, covered: 7)

Missing/Unmatched methods:
- `setBufferAsync`

### `filament/IndirectLight.h` (prebuilt methods: 15, wrapper funcs: 15, covered: 15)

- No unmatched methods detected.

### `filament/InstanceBuffer.h` (prebuilt methods: 5, wrapper funcs: 6, covered: 5)

- No unmatched methods detected.

### `filament/LightManager.h` (prebuilt methods: 53, wrapper funcs: 59, covered: 53)

- No unmatched methods detected.

### `filament/Material.h` (prebuilt methods: 43, wrapper funcs: 43, covered: 40)

Missing/Unmatched methods:
- `UserVariantFilterMask`
- `compile`
- `setParameter`

### `filament/MaterialChunkType.h` (prebuilt methods: 0, wrapper funcs: 0, covered: 0)

- No unmatched methods detected.

### `filament/MaterialEnums.h` (prebuilt methods: 0, wrapper funcs: 0, covered: 0)

- No unmatched methods detected.

### `filament/MaterialInstance.h` (prebuilt methods: 41, wrapper funcs: 77, covered: 41)

- No unmatched methods detected.

### `filament/MorphTargetBuffer.h` (prebuilt methods: 13, wrapper funcs: 15, covered: 13)

- No unmatched methods detected.

### `filament/Options.h` (prebuilt methods: 0, wrapper funcs: 17, covered: 0)

- No unmatched methods detected.

### `filament/RenderTarget.h` (prebuilt methods: 13, wrapper funcs: 14, covered: 13)

- No unmatched methods detected.

### `filament/RenderableManager.h` (prebuilt methods: 72, wrapper funcs: 79, covered: 72)

- No unmatched methods detected.

### `filament/Renderer.h` (prebuilt methods: 22, wrapper funcs: 23, covered: 22)

- No unmatched methods detected.

### `filament/Scene.h` (prebuilt methods: 15, wrapper funcs: 16, covered: 15)

- No unmatched methods detected.

### `filament/SkinningBuffer.h` (prebuilt methods: 5, wrapper funcs: 7, covered: 5)

- No unmatched methods detected.

### `filament/Skybox.h` (prebuilt methods: 12, wrapper funcs: 13, covered: 12)

- No unmatched methods detected.

### `filament/Stream.h` (prebuilt methods: 5, wrapper funcs: 10, covered: 5)

- No unmatched methods detected.

### `filament/SwapChain.h` (prebuilt methods: 6, wrapper funcs: 5, covered: 6)

- No unmatched methods detected.

### `filament/Sync.h` (prebuilt methods: 2, wrapper funcs: 2, covered: 2)

- No unmatched methods detected.

### `filament/Texture.h` (prebuilt methods: 35, wrapper funcs: 36, covered: 33)

Missing/Unmatched methods:
- `async`
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

### `filament/VertexBuffer.h` (prebuilt methods: 14, wrapper funcs: 13, covered: 12)

Missing/Unmatched methods:
- `setBufferAtAsync`
- `setBufferObjectAtAsync`

### `filament/View.h` (prebuilt methods: 77, wrapper funcs: 84, covered: 77)

- No unmatched methods detected.

### `filament/Viewport.h` (prebuilt methods: 0, wrapper funcs: 0, covered: 0)

- No unmatched methods detected.

### `geometry/SurfaceOrientation.h` (prebuilt methods: 11, wrapper funcs: 14, covered: 11)

- No unmatched methods detected.

### `geometry/TangentSpaceMesh.h` (prebuilt methods: 19, wrapper funcs: 21, covered: 17)

Missing/Unmatched methods:
- `aux`
- `getAux`

### `geometry/Transcoder.h` (prebuilt methods: 0, wrapper funcs: 3, covered: 0)

- No unmatched methods detected.

### `gltfio/Animator.h` (prebuilt methods: 9, wrapper funcs: 8, covered: 8)

Missing/Unmatched methods:
- `addInstance`

### `gltfio/AssetLoader.h` (prebuilt methods: 14, wrapper funcs: 10, covered: 10)

Missing/Unmatched methods:
- `getMaterialProvider`
- `getNames`
- `getNodeManager`
- `isSupported`

### `gltfio/FilamentAsset.h` (prebuilt methods: 32, wrapper funcs: 35, covered: 28)

Missing/Unmatched methods:
- `addEntitiesToScene`
- `getAssetInstanceCount`
- `getAssetInstances`
- `getResourceUris`

### `gltfio/FilamentInstance.h` (prebuilt methods: 20, wrapper funcs: 22, covered: 20)

- No unmatched methods detected.

### `gltfio/MaterialProvider.h` (prebuilt methods: 8, wrapper funcs: 4, covered: 4)

Missing/Unmatched methods:
- `constrainMaterial`
- `createMaterialInstance`
- `needsDummyData`
- `processShaderString`

### `gltfio/ResourceLoader.h` (prebuilt methods: 11, wrapper funcs: 12, covered: 11)

- No unmatched methods detected.

### `gltfio/TextureProvider.h` (prebuilt methods: 15, wrapper funcs: 5, covered: 2)

Missing/Unmatched methods:
- `cancelDecoding`
- `createKtx2Provider`
- `createStbProvider`
- `createWebpProvider`
- `getDecodedCount`
- `getPopMessage`
- `getPoppedCount`
- `getPushMessage`
- `getPushedCount`
- `popTexture`
- `pushTexture`
- `updateQueue`
- `waitForCompletion`

### `utils/CallStack.h` (prebuilt methods: 8, wrapper funcs: 7, covered: 6)

Missing/Unmatched methods:
- `CString`
- `update_gcc`

### `utils/Entity.h` (prebuilt methods: 3, wrapper funcs: 4, covered: 3)

- No unmatched methods detected.

### `utils/EntityInstance.h` (prebuilt methods: 2, wrapper funcs: 3, covered: 2)

- No unmatched methods detected.

### `utils/EntityManager.h` (prebuilt methods: 11, wrapper funcs: 16, covered: 11)

- No unmatched methods detected.

### `utils/Log.h` (prebuilt methods: 0, wrapper funcs: 1, covered: 0)

- No unmatched methods detected.

### `utils/NameComponentManager.h` (prebuilt methods: 5, wrapper funcs: 10, covered: 5)

- No unmatched methods detected.

### `utils/Panic.h` (prebuilt methods: 16, wrapper funcs: 8, covered: 9)

Missing/Unmatched methods:
- `PanicStream`
- `c_str`
- `log`
- `logWarning`
- `panicLog`
- `setPanicHandler`
- `what`

### `utils/Path.h` (prebuilt methods: 25, wrapper funcs: 31, covered: 19)

Missing/Unmatched methods:
- `c_str`
- `getCanonicalPath`
- `getExtension`
- `getName`
- `getNameWithoutExtension`
- `split`

### `utils/Status.h` (prebuilt methods: 2, wrapper funcs: 12, covered: 2)

- No unmatched methods detected.

