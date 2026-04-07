cmake_minimum_required(VERSION 3.19)
project(filament-c)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_C_STANDARD 11)

# Options (can be passed from gradle or command line)
option(FILAMENT_SUPPORTS_VULKAN "Enables Vulkan" OFF)

# Filament directory setup
if (NOT FILAMENT_DIST_DIR)
    set(FILAMENT_DIST_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../filament-main")
endif()

set(FILAMENT_BUILD_DIR "${FILAMENT_DIST_DIR}/out/cmake-make-release")

message(STATUS "Using Filament distribution at: ${FILAMENT_DIST_DIR}")
message(STATUS "Using Filament build at: ${FILAMENT_BUILD_DIR}")

# Imported libraries
add_library(filament STATIC IMPORTED)
set_target_properties(filament PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_BUILD_DIR}/filament/libfilament.a)

add_library(backend STATIC IMPORTED)
set_target_properties(backend PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_BUILD_DIR}/filament/backend/libbackend.a)

add_library(utils STATIC IMPORTED)
set_target_properties(utils PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_BUILD_DIR}/libs/utils/libutils.a)

add_library(geometry STATIC IMPORTED)
set_target_properties(geometry PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_BUILD_DIR}/libs/geometry/libgeometry_combined.a)

add_library(ibl-lite STATIC IMPORTED)
set_target_properties(ibl-lite PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_BUILD_DIR}/libs/ibl-lite/libibl-lite.a)

add_library(filaflat STATIC IMPORTED)
set_target_properties(filaflat PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_BUILD_DIR}/libs/filaflat/libfilaflat.a)

add_library(filabridge STATIC IMPORTED)
set_target_properties(filabridge PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_BUILD_DIR}/libs/filabridge/libfilabridge.a)

add_library(zstd STATIC IMPORTED)
set_target_properties(zstd PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_BUILD_DIR}/third_party/zstd/libzstd.a)

# The C-wrapper library
add_library(filament-c SHARED
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
)

target_include_directories(filament-c PUBLIC
    filament/c
)

target_include_directories(filament-c PRIVATE
    filament/cpp
    ${FILAMENT_DIST_DIR}/filament/include
    ${FILAMENT_DIST_DIR}/libs/utils/include
    ${FILAMENT_DIST_DIR}/libs/math/include
    ${FILAMENT_DIST_DIR}/libs/geometry/include
    ${FILAMENT_BUILD_DIR}/generated/include
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
)

# filamat-c library
add_library(filamat-c SHARED
    filamat/cpp/MaterialBuilder.cpp
)

target_include_directories(filamat-c PUBLIC
    filamat/c
    filament/c
)

target_include_directories(filamat-c PRIVATE
    filamat/cpp
    filament/cpp
    ${FILAMENT_DIST_DIR}/include
)

# filament-utils-c library
add_library(filament-utils-c SHARED
    filament-utils/cpp/IBLPrefilter.cpp
    filament-utils/cpp/Manipulator.cpp
    filament-utils/cpp/HDRLoader.cpp
)

target_include_directories(filament-utils-c PUBLIC
    filament-utils/c
    filament/c
)

target_include_directories(filament-utils-c PRIVATE
    filament-utils/cpp
    filament/cpp
    ${FILAMENT_DIST_DIR}/include
)

if (FILAMENT_SUPPORTS_VULKAN)
    add_definitions(-DFILAMENT_SUPPORTS_VULKAN=1)
    # Add bluevk/smol-v if vulkan is enabled
else()
    add_definitions(-DFILAMENT_SUPPORTS_VULKAN=0)
endif()
