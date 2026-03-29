# Filament KMP C Wrapper - Batch Summary & Next Steps

**Status:** ✅ **Batches A, B, C, D, E, F complete & tested successfully**

---

## Completed Work

### ✅ Batch A: IndirectLight + Scene Binding
- **API Surface:**
  - `FilaIndirectLight` & `FilaIndirectLightBuilder` opaque types
  - `FilaIndirectLightBuilder_*` (create, destroy, reflections, intensity, build)
  - `FilaIndirectLight_*` (setIntensity, getIntensity, getReflectionsTexture, getIrradianceTexture)
  - `FilaScene_setIndirectLight` / `FilaScene_getIndirectLight`
  - `FilaEngine_destroyIndirectLight`
  
- **Implementation:** `c-wrapper/src/filament/IndirectLight.cpp` (85 lines)
- **Tests:** Module + Signature compile-only tests, linked smoke program
- **Verification:** 13/13 ctest passed (including IndirectLight scene binding runtime)

### ✅ Batch B: MaterialInstance Parameter Setters
- **API Surface:**
  - `FilaMaterialInstance_setParameterFloat`
  - `FilaMaterialInstance_setParameterFloat2`
  - `FilaMaterialInstance_setParameterFloat3`
  - `FilaMaterialInstance_setParameterFloat4`
  - `FilaMaterialInstance_setParameterInt`
  - `FilaMaterialInstance_setParameterUint`

- **Implementation:** Extended `c-wrapper/src/filament/Material.cpp` with guarded setters via `Material::hasParameter()` precheck
- **Tests:** Module + Signature compile-only tests, linked parameter smoke program
- **Verification:** 14/14 ctest passed (including MaterialInstance parameter setting runtime)

### ✅ Batch C: RenderableManager Material Instance Runtime Binding
- **API Surface:**
  - `FilaRenderableManager_setMaterialInstanceAt` (set material for primitive at runtime)
  - `FilaRenderableManager_getMaterialInstanceAt` (query bound material instance)

- **Implementation:** Extended `c-wrapper/src/filament/RenderableManager.cpp` with runtime material instance get/set
- **Tests:** Extended module + signature compile-only tests to cover new APIs
- **Verification:** 14/14 ctest passed (all linked smoke programs still pass)

### ✅ Enhanced macOS Window Test
- **New Features:**
  - Cubemap texture creation (`FilaTextureBuilder` → 4×4 RGBA8)
  - Skybox attachment with environment texture
  - IndirectLight creation with reflections
  - Full scene IBL lighting pipeline
  - Proper resource cleanup order (skybox → indirectLight → texture → scene)

- **Result:** ✅ Builds successfully, integrates seamlessly with existing render loop
- **Verification:** 14/14 ctest passed (all linked smoke programs still pass)

### ✅ Batch D: View Color Grading & Render Target
- **API Surface:**
  - `FilaColorGrading` & `FilaColorGradingBuilder` opaque types
  - `FilaColorGradingBuilder_*` (create, destroy, quality, format, dimensions, exposure, temperature/tint, contrast, vibrance, saturation, etc.)
  - `FilaColorGradingBuilder_build` (builder pattern to construct ColorGrading)
  - `FilaRenderTarget` & `FilaRenderTargetBuilder` opaque types
  - `FilaRenderTargetBuilder_*` (create, destroy, texture, mipLevel, face, layer, samples)
  - `FilaRenderTarget_*` (getters for texture, mipLevel, face, layer)
  - `FilaView_setColorGrading` / `FilaView_getColorGrading`
  - `FilaView_setRenderTarget` / `FilaView_getRenderTarget`
  - Enums: `FilaColorGradingQuality` (LOW, MEDIUM, HIGH, ULTRA), `FilaColorGradingLutFormat` (INTEGER, FLOAT)
  - Enums: `FilaRenderTargetAttachmentPoint` (COLOR0-7, DEPTH), `FilaCubemapFace` (6 faces)

- **Implementation:** 
  - `c-wrapper/src/filament/ColorGrading.cpp` (~113 lines - builder pattern)
  - `c-wrapper/src/filament/RenderTarget.cpp` (already existed, verified working)
  - `c-wrapper/src/filament/View.cpp` (fixed Camera reference-to-pointer, ColorGrading const qualifier handling)

- **Tests:** 
  - Module tests: `color_grading_module_compile.c`, `render_target_module_compile.c`
  - Signature tests: `color_grading_signature_compile.c`, `render_target_signature_compile.c`
  - Integration test: `engine_view_color_grading_render_target_program.c`
  - All 4 compile-only tests pass without errors
  - Integrated with existing View module test for composition validation

- **Verification:** ✅ Compile-only tests pass and linked integration test passes

### ✅ Batch E: Stream & Texture Advanced
- **API Surface:**
  - `FilaStream` & `FilaStreamBuilder` opaque types
  - `FilaStreamBuilder_*` (create, destroy, width, height)
  - `FilaStream_*` (getStreamType, setDimensions, getTimestamp)
  - `FilaStreamBuilder_build` (builder pattern to construct Stream)
  - `FilaTextureParams` opaque type (TextureSampler wrapper)
  - `FilaTextureParams_*` (create, destroy, setters/getters for filters, wrap modes, anisotropy)
  - `FilaTexture_setExternalStream` (bind Stream to Texture for external frame sources)
  - Enums: `FilaStreamType` (ACQUIRED, NATIVE)
  - Enums: `FilaSamplerMinFilter` (NEAREST, LINEAR, mipmap variants)
  - Enums: `FilaSamplerMagFilter` (NEAREST, LINEAR)
  - Enums: `FilaSamplerWrapMode` (CLAMP_TO_EDGE, REPEAT, MIRRORED_REPEAT)
  - Enums: `FilaSamplerCompareMode` (NONE, TO_TEXTURE)
  - Enums: `FilaSamplerCompareFunc` (LE, GE, L, G, E, NE, A, NA)

- **Implementation:**
  - `c-wrapper/src/filament/Stream.cpp` (59 lines - builder pattern)
  - `c-wrapper/src/filament/TextureSampler.cpp` (160 lines - sampler parameter wrapper)
  - `c-wrapper/src/filament/Texture.cpp` (extended with setExternalStream)
  - `c-wrapper/src/filament/Engine.cpp` (added destroyStream)

- **Tests:**
  - Module tests: `stream_module_compile.c`, `texture_sampler_module_compile.c`
  - Signature tests: `stream_signature_compile.c`, `texture_sampler_signature_compile.c`
  - Integration test: `engine_stream_texture_params_program.c`
  - All 4 compile-only tests pass without errors

- **Verification:** ✅ Compile-only tests pass and linked integration test passes

### ✅ Batch F: Geometry Advanced
- **API Surface:**
  - `FilaSkinningBuffer` (vertex weights, bone indices)
  - `FilaMorphTargetBuffer` (shape keys)
  - `FilaInstanceBuffer` (GPU-driven instancing)
  - Scene queries for component counts

- **Implementation:** Added missing implementations for advanced geometry components.
- **Verification:** Completed and tested.

---

## Current C API Coverage

**Wrapped Modules (23 total):**
- Engine (create, destroy, resource lifecycle)
- Scene (entity management, skybox, indirect light)
- Renderer (begin/end frame, render)
- View (scene/camera binding, viewport, color grading, render target)
- Camera (projection, look-at)
- TransformManager (get/set transforms, hierarchy)
- LightManager (add lights, sun direction)
- RenderableManager (geometry, materials, visibility)
- EntityManager (create/destroy entities)
- VertexBuffer (builder, query)
- IndexBuffer (builder, query)
- Material (builder, instance creation)
- MaterialInstance (get parent, set parameters)
- Texture (builder, dimension queries, external stream)
- Skybox (builder, scene binding, getters)
- IndirectLight (builder, scene binding, getters)
- ColorGrading (builder, quality/format/exposure/temperature/contrast/vibrance/saturation)
- RenderTarget (builder, texture attachment, query)
- Stream (builder, external frame sources)
- TextureParams (sampler configuration, filter/wrap/anisotropy)
- SkinningBuffer (bone transformations)
- MorphTargetBuffer (shape keys)
- InstanceBuffer (GPU instancing)

**Test Suite:**
- 23 module compile tests (header usability)
- 23 signature tests (ABI lock)
- 16+ linked integration programs

---

## Roadmap / Next Batches (Prioritized)

To achieve true 1:1 API parity and a robust KMP ecosystem, the following batches are prioritized:

### **Batch G: Core Utilities, Math, & Missing Enums** *(High Priority - Next)*
**Why:** Provide the foundational types required by other modules and advanced functionality.
**Scope:**
- `filament/BufferObject.h` (Custom uniform/storage buffer objects)
- `filament/Sync.h` (Advanced hardware synchronization)
- `filament/DebugRegistry.h` (Internal metrics and debugging)
- **Helper / Math Types:** `Box`, `Frustum`, `Viewport`, `Color`
- **Enums & Config:** `ToneMapper`, `ColorSpace`, `Exposure`, `MaterialEnums`, `MaterialChunkType`
- **Config Structs:** Full mapping of `Options.h` (`Engine::Config`, `View::DynamicResolutionOptions`, `View::RenderQuality`, etc.)

**Action:** Start by mapping these types matching the `filament-prebuilts/include/filament/` structure.

### **Batch H: Resource Uploads & Advanced API Parity** *(High Priority)*
**Why:** Ensure developers can actively feed data to the engine, configure post-processing, and manage lifecycle events dynamically.
**Scope:**
- **Pixel / Buffer Descriptors:** Implement `PixelBufferDescriptor` and `BufferDescriptor` mappings (crucial for updating textures and buffers).
- **Textures & Buffers:** `Texture::setImage`, `Texture::generateMipmaps`, `VertexBuffer::setBufferAt`, `IndexBuffer::setBuffer`.
- **Advanced Engine Features:** `Engine::create(backend, platform)` to specify Vulkan/Metal/OpenGL, and shared context initialization.
- **Advanced View Settings:** Expose Ambient Occlusion (SSAO), Screen Space Reflections (SSR), Bloom, FXAA/TAA/MSAA, and Depth of Field.
- **Material Enhancements:** Support `setParameter` for textures and samplers (not just floats/ints), and arrays of parameters.
- **Callbacks:** Bridge C++ callbacks (e.g., buffer release, frame completion) to C function pointers so KMP can handle memory correctly.

### **Batch I: `gltfio` and Ecosystem Libraries** *(Crucial for usability)*
**Why:** Loading 3D models easily is the most common use case. Developers need high-level model loading without parsing GLTF manually.
**Scope:**
- Wrap `gltfio` library (`AssetLoader`, `FilamentAsset`, `FilamentInstance`, `ResourceLoader`, `Animator`).
- Wrap `camutils` (`Manipulator`, `Bookmark`) for standard camera controls (orbit, map, free flight).
- Wrap `ktxreader` / `image` utilities for KTX2 compressed texture loading.
- Wrap `utils` (e.g., `NameComponentManager`, `HierarchyComponentManager`).

---

## Recommended Action

**Next:** Start **Batch G** to implement the missing core Filament modules (`BufferObject`, `Sync`, `DebugRegistry`), followed by the mathematical helpers (`Box`, `Frustum`, etc.) and configuration enums/structs (`Options`, `MaterialEnums`). Ensure all file structures (`src/` and `include/`) match the existing `filament-prebuilts` layout.