cmake_minimum_required(VERSION 3.19)

# --- Platform specific setup ---
set(FILAMENT_PLATFORM "ios-simulator" CACHE STRING "Target platform (ios, ios-simulator)")
set(FILAMENT_ARCH "arm64" CACHE STRING "Target architecture (arm64, x64)")

# --- Platform specific setup ---
if (FILAMENT_PLATFORM STREQUAL "ios")
    set(CMAKE_SYSTEM_NAME iOS)
    set(CMAKE_OSX_SYSROOT iphoneos)
    set(CMAKE_OSX_ARCHITECTURES arm64)
    set(CMAKE_OSX_DEPLOYMENT_TARGET "15.0")
    set(FILAMENT_LIB_DIR_DEFAULT "${CMAKE_CURRENT_SOURCE_DIR}/../prebuilts/iosArm64/lib")
elseif (FILAMENT_PLATFORM STREQUAL "ios-simulator")
    set(CMAKE_SYSTEM_NAME iOS)
    set(CMAKE_OSX_SYSROOT iphonesimulator)
    set(CMAKE_OSX_DEPLOYMENT_TARGET "15.0")
    if (FILAMENT_ARCH STREQUAL "x64")
        set(CMAKE_OSX_ARCHITECTURES "x86_64")
        set(FILAMENT_LIB_DIR_DEFAULT "${CMAKE_CURRENT_SOURCE_DIR}/../prebuilts/iosX64/lib")
    else()
        set(CMAKE_OSX_ARCHITECTURES "arm64")
        set(FILAMENT_LIB_DIR_DEFAULT "${CMAKE_CURRENT_SOURCE_DIR}/../prebuilts/iosSimulatorArm64/lib")
    endif()
endif()

project(filament-c)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_C_STANDARD 11)

# Filament directory setup
if (NOT FILAMENT_DIST_DIR)
    set(FILAMENT_DIST_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../filament-main")
endif()

if (NOT FILAMENT_INCLUDE_DIR)
    set(FILAMENT_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../include")
endif()

if (NOT FILAMENT_LIB_DIR)
    set(FILAMENT_LIB_DIR "${FILAMENT_LIB_DIR_DEFAULT}")
endif()

# Platform detection
if (WIN32)
    set(LIB_PREFIX "")
    set(LIB_SUFFIX ".lib")
else()
    set(LIB_PREFIX "lib")
    set(LIB_SUFFIX ".a")
endif()

message(STATUS "Using Filament distribution at: ${FILAMENT_DIST_DIR}")
message(STATUS "Using Filament headers at: ${FILAMENT_INCLUDE_DIR}")
message(STATUS "Using Filament libraries at: ${FILAMENT_LIB_DIR}")

# Imported libraries Helper
macro(add_filament_library NAME)
    add_library(${NAME} STATIC IMPORTED)
    set_target_properties(${NAME} PROPERTIES IMPORTED_LOCATION
            ${FILAMENT_LIB_DIR}/${LIB_PREFIX}${NAME}${LIB_SUFFIX})
endmacro()

add_filament_library(filament)
add_filament_library(backend)
add_filament_library(utils)

add_library(geometry STATIC IMPORTED)
set_target_properties(geometry PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_LIB_DIR}/${LIB_PREFIX}geometry${LIB_SUFFIX})

add_library(ibl-lite STATIC IMPORTED)
set_target_properties(ibl-lite PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_LIB_DIR}/${LIB_PREFIX}ibl-lite${LIB_SUFFIX})

add_library(filamat STATIC IMPORTED)
set_target_properties(filamat PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_LIB_DIR}/${LIB_PREFIX}filamat${LIB_SUFFIX})

add_library(shaders STATIC IMPORTED)
set_target_properties(shaders PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_LIB_DIR}/${LIB_PREFIX}shaders${LIB_SUFFIX})

add_library(iblprefilter STATIC IMPORTED)
set_target_properties(iblprefilter PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_LIB_DIR}/${LIB_PREFIX}filament-iblprefilter${LIB_SUFFIX})

add_filament_library(camutils)
add_filament_library(image)
add_filament_library(imageio-lite)
add_filament_library(filaflat)
add_filament_library(filabridge)
add_filament_library(zstd)
add_filament_library(smol-v)
add_filament_library(ktxreader)
add_filament_library(png)
add_filament_library(tinyexr)
add_filament_library(z)
add_filament_library(gltfio_core)
add_filament_library(dracodec)
add_filament_library(stb)

# The C-wrapper library
add_library(filament-c STATIC
    # Engine
    filament/cpp/Engine.cpp
    # Camera
    filament/cpp/Camera.cpp
    # Material
    filament/cpp/Material.cpp
    # RenderableManager
    filament/cpp/RenderableManager.cpp
    # TransformManager
    filament/cpp/TransformManager.cpp
    # LightManager
    filament/cpp/LightManager.cpp
    # Renderer
    filament/cpp/Renderer.cpp
    # View
    filament/cpp/View.cpp
    # Scene
    filament/cpp/Scene.cpp
    # SwapChain
    filament/cpp/SwapChain.cpp
    # VertexBuffer
    filament/cpp/VertexBuffer.cpp
    # IndexBuffer
    filament/cpp/IndexBuffer.cpp
    # BufferObject
    filament/cpp/BufferObject.cpp
    # Texture
    filament/cpp/Texture.cpp
    # RenderTarget
    filament/cpp/RenderTarget.cpp
    # MorphTargetBuffer
    filament/cpp/MorphTargetBuffer.cpp
    # SkinningBuffer
    filament/cpp/SkinningBuffer.cpp
    # IndirectLight
    filament/cpp/IndirectLight.cpp
    # Skybox
    filament/cpp/Skybox.cpp
    # Stream
    filament/cpp/Stream.cpp
    # Fence
    filament/cpp/Fence.cpp
    # ToneMapper
    filament/cpp/ToneMapper.cpp
    # ColorGrading
    filament/cpp/ColorGrading.cpp
    # MaterialInstance
    filament/cpp/MaterialInstance.cpp
    # EntityManager
    filament/cpp/MathUtils.cpp
    filament/cpp/EntityManager.cpp
    # TextureSampler
    filament/cpp/TextureSampler.cpp
    # Colors
    filament/cpp/Colors.cpp
    # SurfaceOrientation
    filament/cpp/SurfaceOrientation.cpp
)

target_include_directories(filament-c PUBLIC
    filament/c
)

target_include_directories(filament-c PRIVATE
    filament/cpp
    ${FILAMENT_INCLUDE_DIR}
)

target_link_libraries(filament-c
    PRIVATE filament
    PRIVATE backend
    PRIVATE utils
    PRIVATE geometry
    PRIVATE ibl-lite
    PRIVATE filaflat
    PRIVATE filabridge
    PRIVATE zstd
    PRIVATE smol-v
)

if (APPLE)
    if (FILAMENT_PLATFORM STREQUAL "ios" OR FILAMENT_PLATFORM STREQUAL "ios-simulator")
        target_link_libraries(filament-c PRIVATE
            "-framework Metal"
            "-framework Foundation"
            "-framework UIKit"
            "-framework QuartzCore"
            "-framework CoreVideo"
        )
    else()
        target_link_libraries(filament-c PRIVATE
            "-framework Metal"
            "-framework Foundation"
            "-framework AppKit"
            "-framework QuartzCore"
            "-framework CoreVideo"
        )
    endif()
endif()

# filamat-c library
add_library(filamat-c STATIC
    filamat/cpp/MaterialBuilder.cpp
)

target_link_libraries(filamat-c
    PRIVATE filamat
    PRIVATE filament
    PRIVATE backend
    PRIVATE filabridge
    PRIVATE shaders
    PRIVATE smol-v
    PRIVATE utils
)

target_include_directories(filamat-c PUBLIC
    filamat/c
    filament/c
)

target_include_directories(filamat-c PRIVATE
    filamat/cpp
    filament/cpp
    ${FILAMENT_INCLUDE_DIR}
)

# filament-utils-c library
add_library(filament-utils-c STATIC
    filament-utils/cpp/IBLPrefilter.cpp
    filament-utils/cpp/Manipulator.cpp
    filament-utils/cpp/HDRLoader.cpp
    filament-utils/cpp/KTX1Loader.cpp
    filament-utils/cpp/TextureLoader.cpp
)

target_link_libraries(filament-utils-c
    PRIVATE filament-c
    PRIVATE iblprefilter
    PRIVATE ktxreader
    PRIVATE camutils
    PRIVATE image
    PRIVATE imageio-lite
    PRIVATE zstd
    PRIVATE utils
    PRIVATE filaflat
    PRIVATE filabridge
)

target_include_directories(filament-utils-c PUBLIC
    filament-utils/c
    filament/c
)

target_include_directories(filament-utils-c PRIVATE
    filament-utils/cpp
    filament/cpp
    ${FILAMENT_INCLUDE_DIR}
)

# gltfio-c library
add_library(gltfio-c STATIC
    gltfio/cpp/AssetLoader.cpp
    gltfio/cpp/FilamentAsset.cpp
    gltfio/cpp/FilamentInstance.cpp
    gltfio/cpp/Animator.cpp
    gltfio/cpp/MaterialProvider.cpp
    gltfio/cpp/ResourceLoader.cpp
)

target_link_libraries(gltfio-c
    PRIVATE gltfio_core
    PRIVATE dracodec
    PRIVATE stb
    PRIVATE filament
    PRIVATE backend
    PRIVATE utils
    PRIVATE filaflat
    PRIVATE filabridge
    PRIVATE image
    PRIVATE imageio-lite
    PRIVATE ktxreader
    PRIVATE zstd
)

target_include_directories(gltfio-c PUBLIC
    gltfio/c
    filament/c
)

target_include_directories(gltfio-c PRIVATE
    gltfio/cpp
    filament/cpp
    ${FILAMENT_INCLUDE_DIR}
)

if (FILAMENT_SUPPORTS_VULKAN)
    add_definitions(-DFILAMENT_SUPPORTS_VULKAN=1)
    # Add bluevk/smol-v if vulkan is enabled
else()
    add_definitions(-DFILAMENT_SUPPORTS_VULKAN=0)
endif()
