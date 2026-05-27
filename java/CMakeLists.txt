cmake_minimum_required(VERSION 3.19...3.28)

# Filament prebuilts are built with clang/libc++ — use the same toolchain on Linux
if (UNIX AND NOT APPLE)
    find_program(CLANGPP clang++)
    if (CLANGPP)
        set(CMAKE_CXX_COMPILER ${CLANGPP})
    else()
        message(FATAL_ERROR "clang++ not found. Install it with: sudo apt install clang libc++-dev libc++abi-dev")
    endif()
endif()

project(filament-jni LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 20)

if (UNIX AND NOT APPLE)
    add_compile_options(-stdlib=libc++)
    add_link_options(-stdlib=libc++)
endif()

# --- Platform / arch ---
set(FILAMENT_PLATFORM "macos" CACHE STRING "Target platform (ios, ios-simulator, macos, linux, windows)")
set(FILAMENT_ARCH     "arm64" CACHE STRING "Target architecture (arm64, x64)")

set(FILAMENT_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../include")
set(FILAMENT_PREBUILT_PREFIX ${FILAMENT_PLATFORM})
if (WIN32)
    set(FILAMENT_PREBUILT_PREFIX "mingw")
endif()
set(FILAMENT_LIB_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../prebuilts/${FILAMENT_PREBUILT_PREFIX}${FILAMENT_ARCH}/lib")

find_package(JNI REQUIRED)

message(STATUS "Using Filament headers at: ${FILAMENT_INCLUDE_DIR}")
message(STATUS "Using Filament libraries at: ${FILAMENT_LIB_DIR}")

# Imported static-library helper
macro(add_filament_library NAME)
    add_library(${NAME} STATIC IMPORTED)
    if (WIN32)
        set_target_properties(${NAME} PROPERTIES IMPORTED_LOCATION ${FILAMENT_LIB_DIR}/${NAME}.lib)
    else()
        set_target_properties(${NAME} PROPERTIES IMPORTED_LOCATION ${FILAMENT_LIB_DIR}/lib${NAME}.a)
    endif()
endmacro()

# Union of static archives needed by filament + filamat + gltfio + filament-utils.
add_filament_library(filament)
add_filament_library(backend)
add_filament_library(utils)
add_filament_library(math)
add_filament_library(filaflat)
add_filament_library(filabridge)
add_filament_library(zstd)
add_filament_library(smol-v)
add_filament_library(bluegl)
add_filament_library(bluevk)
add_filament_library(ibl)
add_filament_library(image)
add_filament_library(geometry)
add_filament_library(meshoptimizer)
add_filament_library(camutils)
add_filament_library(ktxreader)
add_filament_library(filament-generatePrefilterMipmap)
add_filament_library(filamat)
add_filament_library(shaders)
add_filament_library(gltfio)
add_filament_library(gltfio_core)
add_filament_library(uberzlib)
add_filament_library(uberarchive)
add_filament_library(dracodec)
add_filament_library(stb)
add_filament_library(basis_transcoder)
add_filament_library(imageio-lite)
add_filament_library(filament-iblprefilter)

# --- One combined shared library ---
#
# Historically each binding (filament-jni, filamat-jni, gltfio-jni, filament-utils-jni)
# was built as its own SHARED lib statically linking the entire Filament stack.
# That gave each dylib its own copy of singletons like EntityManager, leading to crashes
# when an entity created in one binding was passed to another.
# This combined target embeds the static archives exactly once.
add_library(filament-jni SHARED
    # filament/
    filament/src/main/cpp/Engine.cpp
    filament/src/main/cpp/Renderer.cpp
    filament/src/main/cpp/View.cpp
    filament/src/main/cpp/Scene.cpp
    filament/src/main/cpp/Camera.cpp
    filament/src/main/cpp/EntityManager.cpp
    filament/src/main/cpp/VertexBuffer.cpp
    filament/src/main/cpp/IndexBuffer.cpp
    filament/src/main/cpp/Texture.cpp
    filament/src/main/cpp/Material.cpp
    filament/src/main/cpp/RenderableManager.cpp
    filament/src/main/cpp/LightManager.cpp
    filament/src/main/cpp/TransformManager.cpp
    filament/src/main/cpp/IndirectLight.cpp
    filament/src/main/cpp/Skybox.cpp
    filament/src/main/cpp/ColorGrading.cpp
    filament/src/main/cpp/ToneMapper.cpp
    filament/src/main/cpp/TextureSampler.cpp
    filament/src/main/cpp/Fence.cpp
    filament/src/main/cpp/BufferObject.cpp
    filament/src/main/cpp/RenderTarget.cpp
    filament/src/main/cpp/Stream.cpp
    filament/src/main/cpp/MorphTargetBuffer.cpp
    filament/src/main/cpp/SkinningBuffer.cpp
    filament/src/main/cpp/SurfaceOrientation.cpp
    filament/src/main/cpp/Colors.cpp
    filament/src/main/cpp/MathUtils.cpp
    filament/src/main/cpp/MaterialInstance.cpp
    filament/src/main/cpp/SwapChain.cpp
    filament/src/main/cpp/common/NioUtils.cpp
    filament/src/main/cpp/common/CallbackUtils.cpp
    filament/src/main/cpp/common/VirtualMachineEnv.cpp

    # filamat/
    filamat/src/main/cpp/MaterialBuilder.cpp

    # gltfio/
    gltfio/src/main/cpp/AssetLoader.cpp
    gltfio/src/main/cpp/FilamentAsset.cpp
    gltfio/src/main/cpp/ResourceLoader.cpp
    gltfio/src/main/cpp/FilamentInstance.cpp
    gltfio/src/main/cpp/Animator.cpp
    gltfio/src/main/cpp/MaterialProvider.cpp
    gltfio/src/main/cpp/MaterialKey.cpp
    gltfio/src/main/cpp/Gltfio.cpp

    # filament-utils/  (NB: its common/ duplicates filament/common — we use filament's copy only)
    filament-utils/src/main/cpp/Bookmark.cpp
    filament-utils/src/main/cpp/HDRLoader.cpp
    filament-utils/src/main/cpp/IBLPrefilterContext.cpp
    filament-utils/src/main/cpp/Manipulator.cpp
    filament-utils/src/main/cpp/KTX1Loader.cpp
    filament-utils/src/main/cpp/TextureLoader.cpp
)

# Platform-specific Filament <-> Skia texture-sharing helpers.
if (APPLE AND FILAMENT_PLATFORM STREQUAL "macos")
    target_sources(filament-jni PRIVATE filament/src/main/cpp/interop/MetalInterop.mm)
    set_source_files_properties(filament/src/main/cpp/interop/MetalInterop.mm
        PROPERTIES COMPILE_FLAGS "-fobjc-arc")
elseif (WIN32 OR (UNIX AND NOT APPLE))
    target_sources(filament-jni PRIVATE filament/src/main/cpp/interop/GLInterop.cpp)
endif()

target_include_directories(filament-jni PRIVATE
    filament/src/main/cpp
    filamat/src/main/cpp
    gltfio/src/main/cpp
    filament-utils/src/main/cpp
    ${JNI_INCLUDE_DIRS}
    ${FILAMENT_INCLUDE_DIR}
)

target_link_libraries(filament-jni
    # Renderer + core
    PRIVATE filament
    PRIVATE backend
    PRIVATE utils
    PRIVATE filaflat
    PRIVATE filabridge
    PRIVATE zstd
    PRIVATE smol-v
    PRIVATE bluegl
    PRIVATE bluevk
    PRIVATE ibl
    PRIVATE image
    PRIVATE geometry
    PRIVATE meshoptimizer
    PRIVATE camutils
    PRIVATE ktxreader
    PRIVATE filament-generatePrefilterMipmap
    # filamat
    PRIVATE filamat
    PRIVATE shaders
    # gltfio
    PRIVATE gltfio
    PRIVATE gltfio_core
    PRIVATE uberzlib
    PRIVATE uberarchive
    PRIVATE dracodec
    PRIVATE stb
    PRIVATE basis_transcoder
    # filament-utils
    PRIVATE imageio-lite
    PRIVATE filament-iblprefilter
    ${JNI_LIBRARIES}
)

if (APPLE)
    if (FILAMENT_PLATFORM STREQUAL "macos")
        target_link_libraries(filament-jni PRIVATE
            "-framework Cocoa"
            "-framework Metal"
            "-framework Foundation"
            "-framework AppKit"
            "-framework QuartzCore"
            "-framework CoreVideo"
            "-framework OpenGL"
        )
    else()
        target_link_libraries(filament-jni PRIVATE
            "-framework Metal"
            "-framework Foundation"
            "-framework UIKit"
            "-framework QuartzCore"
            "-framework CoreVideo"
        )
    endif()
elseif (WIN32)
    # PlatformWGL / BlueGLWindows need the Win32 OpenGL + GDI libs;
    # shlwapi is used by utils::Path.
    target_link_libraries(filament-jni PRIVATE opengl32 gdi32 user32 shlwapi)
elseif (UNIX)
    target_link_libraries(filament-jni PRIVATE GL)
    # GNU ld scans static archives left-to-right in a single pass, so cross-refs
    # between Filament archives (e.g. gltfio → ktxreader → filament → backend)
    # can drop members if ordering is wrong. Wrap them in a group so ld rescans
    # until all references resolve. Also force a non-executable stack — one of
    # the prebuilt .S objects is missing the .note.GNU-stack marker.
    target_link_options(filament-jni PRIVATE
        "LINKER:-z,noexecstack"
        "LINKER:--start-group"
    )
    target_link_libraries(filament-jni PRIVATE "-Wl,--end-group")
endif()
