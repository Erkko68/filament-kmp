# Linux GPU Frame Sharing: GLX Today, Vulkan Next

Status: **parked** until development moves to a Linux machine with a GPU. This note records why the
Linux GPU-to-GPU target ([Integration Strategies](../compose/integration-strategies.md#experimental-gpu-to-gpu-frame-sharing))
has to change, the options checked, and the plan.

## Today: Filament shares skiko's GLX context

`GlxOffscreenTarget` (`kotlin/filament-compose/src/jvmMain/.../internal/target/glx/`) creates
Filament's OpenGL engine in the share group of skiko's GLX context, renders into a texture, and
wraps that texture's framebuffer on skiko's `DirectContext`. Finding the context is reflection into
`LinuxOpenGLRedrawer.context`; `jni/src/main/cpp/GlxHelper.cpp` dereferences it as a `GLXContext*`.

## What breaks it: skiko moving to EGL

[JetBrains/skiko#1274](https://github.com/JetBrains/skiko/pull/1274)
([SKIKO-1176](https://youtrack.jetbrains.com/issue/SKIKO-1176/Use-EGL-on-linux-x64)), open since
2026-08-19, replaces GLX with EGL in skiko's Linux redrawer and binds OpenGL ES instead of desktop GL.

- The Kotlin field names stay, so the reflection keeps "working", but `context` becomes an
  `EGLContextWrapper*`. Reading it as a `GLXContext*` and calling `glXMakeCurrent` crashes natively,
  which the Kotlin fallback can't catch.
- **Guard (landed):** `SkikoGlx.find` checks that skiko's native `makeCurrent` still takes
  `(display, window, context)`. Under #1274 it takes `(context)`, so the target reports
  `unavailable` and the view falls back to readback instead of crashing.

## Option rejected: follow skiko onto EGL

Checked against Filament 1.78.0:

- EGL on Linux exists only as a build switch, `FILAMENT_SUPPORTS_EGL_ON_LINUX` (`build.sh -e`), off by
  default. It *replaces* GLX (`PlatformEGLHeadless` instead of `PlatformGLX`) and turns X11 off, which
  also removes Vulkan's X11 window surfaces. Release tarballs ship GLX only, so we'd build Linux from
  source like `wasm` / `windows-arm64`.
- EGL contexts only share within one client API. On desktop Linux Filament binds `EGL_OPENGL_API`
  (`BACKEND_OPENGL_VERSION_GL`), while skiko under #1274 binds `EGL_OPENGL_ES_API`, so we'd also need
  a GLES Filament build.
- Same `EGLDisplay` is solvable: `PlatformEGL::setEglDisplay` is public.

Too much build surface for a target that stays tied to how skiko creates its context.

## Plan: Vulkan engine, memory shared into skiko's GL

Mirror the Windows target, which already runs Filament's Vulkan engine into shared D3D12 textures
(`jni/src/main/cpp/D3DHelper.cpp`, `D3DSharedPlatform`) on the stock prebuilts:

1. A `VulkanPlatform` subclass exposes a fake swap chain of `VkImage`s allocated with
   `VkExportMemoryAllocateInfo` (`OPAQUE_FD`), exported via `vkGetMemoryFdKHR`
   (`VK_KHR_external_memory_fd`). Same overrides as `D3DSharedPlatform`: `createSwapChain`,
   `getSwapChainBundle`, `acquire`, `present`, `createVkDevice`, `selectVkPhysicalDevice`.
2. skiko's GL imports each fd with `GL_EXT_memory_object_fd` (`glCreateMemoryObjectsEXT`,
   `glImportMemoryFdEXT`, `glTexStorageMem2DEXT`) and Skia wraps the texture as today.
3. Sync with an exported semaphore (`VK_KHR_external_semaphore_fd` → `GL_EXT_semaphore_fd`,
   `glWaitSemaphoreEXT`) signalled in `present`.
4. GPU match by UUID instead of LUID: GL's `GL_DEVICE_UUID_EXT` against Vulkan's
   `VkPhysicalDeviceIDProperties::deviceUUID`.

Why this is the better target:

- The GL extensions exist for both desktop GL and GLES, so it works on skiko's GLX contexts now
  and EGL ones after #1274.
- No custom Filament build: the Linux prebuilt already has Vulkan (`libbluevk.a`,
  `VulkanPlatformLinux`). `importTexture` is a stub on Vulkan and external images are AHB-shaped,
  so the fake swap chain is the right hook here too.
- Linux users get GPU sharing with `Engine.Backend.VULKAN`, not only OpenGL.

Open questions to settle on the Linux box:

- Can the GL import run inside Compose's draw, where skiko's context is already current? Then no
  `makeCurrent` of our own is needed and the GLX/EGL difference disappears entirely.
- Driver coverage: Mesa (radeonsi, iris) and NVIDIA's driver expose the memory-object extensions;
  llvmpipe / headless CI should keep falling back to readback.
- Keep GLX as a second target for machines without Vulkan, or drop it once Vulkan works.
