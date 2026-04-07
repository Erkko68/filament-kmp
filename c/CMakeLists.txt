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

# Platform detection
if (WIN32)
    set(LIB_PREFIX "")
    set(LIB_SUFFIX ".lib")
else()
    set(LIB_PREFIX "lib")
    set(LIB_SUFFIX ".a")
endif()

message(STATUS "Using Filament distribution at: ${FILAMENT_DIST_DIR}")
message(STATUS "Using Filament build at: ${FILAMENT_BUILD_DIR}")

# Imported libraries Helper
macro(add_filament_library NAME PATH)
    add_library(${NAME} STATIC IMPORTED)
    set_target_properties(${NAME} PROPERTIES IMPORTED_LOCATION
            ${FILAMENT_BUILD_DIR}/${PATH}/${LIB_PREFIX}${NAME}${LIB_SUFFIX})
endmacro()

add_filament_library(filament filament)
add_filament_library(backend filament/backend)
add_filament_library(utils libs/utils)
add_filament_library(math libs/math)

add_library(geometry STATIC IMPORTED)
set_target_properties(geometry PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_BUILD_DIR}/libs/geometry/${LIB_PREFIX}geometry_combined${LIB_SUFFIX})

add_library(ibl-lite STATIC IMPORTED)
set_target_properties(ibl-lite PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_BUILD_DIR}/libs/ibl/${LIB_PREFIX}ibl-lite${LIB_SUFFIX})

add_library(filamat STATIC IMPORTED)
set_target_properties(filamat PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_BUILD_DIR}/libs/filamat/${LIB_PREFIX}filamat_combined${LIB_SUFFIX})

add_library(shaders STATIC IMPORTED)
set_target_properties(shaders PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_BUILD_DIR}/shaders/${LIB_PREFIX}shaders${LIB_SUFFIX})

add_library(iblprefilter STATIC IMPORTED)
set_target_properties(iblprefilter PROPERTIES IMPORTED_LOCATION
        ${FILAMENT_BUILD_DIR}/libs/iblprefilter/${LIB_PREFIX}filament-iblprefilter${LIB_SUFFIX})

add_filament_library(camutils libs/camutils)
add_filament_library(image libs/image)
add_filament_library(imageio libs/imageio)
add_filament_library(filaflat libs/filaflat)
add_filament_library(filabridge libs/filabridge)
add_filament_library(zstd third_party/zstd/tnt)
add_filament_library(smol-v third_party/smol-v/tnt)
add_filament_library(bluegl libs/bluegl)
add_filament_library(bluevk libs/bluevk)
add_filament_library(png third_party/libpng/tnt)
add_filament_library(tinyexr third_party/tinyexr/tnt)
add_filament_library(z third_party/libz/tnt)

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
    ${FILAMENT_DIST_DIR}/filament/backend/include
    ${FILAMENT_DIST_DIR}/libs/utils/include
    ${FILAMENT_DIST_DIR}/libs/math/include
    ${FILAMENT_DIST_DIR}/libs/geometry/include
    ${FILAMENT_DIST_DIR}/libs/filabridge/include
    ${FILAMENT_DIST_DIR}/libs/filaflat/include
    ${FILAMENT_DIST_DIR}/third_party/robin-map/include
    ${FILAMENT_DIST_DIR}/third_party/stb
    ${FILAMENT_DIST_DIR}/third_party/libz/tnt/include
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
    PRIVATE smol-v
    PRIVATE bluegl
    PRIVATE bluevk
)

if (APPLE)
    target_link_libraries(filament-c PRIVATE
        "-framework Metal"
        "-framework Foundation"
        "-framework AppKit"
        "-framework QuartzCore"
        "-framework CoreVideo"
        "-framework OpenGL"
    )
elseif (WIN32)
    target_link_libraries(filament-c PRIVATE
        opengl32.lib
        user32.lib
        gdi32.lib
        shlwapi.lib
    )
else() # Linux
    target_link_libraries(filament-c PRIVATE
        pthread
        dl
        GL
        X11
    )
    set_target_properties(filament-c PROPERTIES POSITION_INDEPENDENT_CODE ON)
endif()

# filamat-c library
add_library(filamat-c SHARED
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
    PRIVATE math
)

target_include_directories(filamat-c PUBLIC
    filamat/c
    filament/c
)

target_include_directories(filamat-c PRIVATE
    filamat/cpp
    filament/cpp
    ${FILAMENT_DIST_DIR}/filament/include
    ${FILAMENT_DIST_DIR}/filament/backend/include
    ${FILAMENT_DIST_DIR}/libs/filamat/include
    ${FILAMENT_DIST_DIR}/libs/utils/include
    ${FILAMENT_DIST_DIR}/libs/math/include
    ${FILAMENT_DIST_DIR}/libs/filabridge/include
    ${FILAMENT_DIST_DIR}/third_party/robin-map/include
    ${FILAMENT_DIST_DIR}/third_party/stb
    ${FILAMENT_DIST_DIR}/third_party/libz/tnt/include
)

# filament-utils-c library
add_library(filament-utils-c SHARED
    filament-utils/cpp/IBLPrefilter.cpp
    filament-utils/cpp/Manipulator.cpp
    filament-utils/cpp/HDRLoader.cpp
)

target_link_libraries(filament-utils-c
    PRIVATE filament-c
    PRIVATE iblprefilter
    PRIVATE camutils
    PRIVATE image
    PRIVATE imageio
    PRIVATE png
    PRIVATE tinyexr
    PRIVATE z
    PRIVATE zstd
    PRIVATE utils
    PRIVATE math
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
    ${FILAMENT_DIST_DIR}/filament/include
    ${FILAMENT_DIST_DIR}/filament/backend/include
    ${FILAMENT_DIST_DIR}/libs/utils/include
    ${FILAMENT_DIST_DIR}/libs/math/include
    ${FILAMENT_DIST_DIR}/libs/filabridge/include
    ${FILAMENT_DIST_DIR}/libs/filaflat/include
    ${FILAMENT_DIST_DIR}/libs/iblprefilter/include
    ${FILAMENT_DIST_DIR}/libs/camutils/include
    ${FILAMENT_DIST_DIR}/libs/image/include
    ${FILAMENT_DIST_DIR}/libs/imageio/include
    ${FILAMENT_DIST_DIR}/third_party/robin-map/include
    ${FILAMENT_DIST_DIR}/third_party/stb
    ${FILAMENT_DIST_DIR}/third_party/libz/tnt/include
    ${FILAMENT_DIST_DIR}/third_party/cgltf
)

if (FILAMENT_SUPPORTS_VULKAN)
    add_definitions(-DFILAMENT_SUPPORTS_VULKAN=1)
    # Add bluevk/smol-v if vulkan is enabled
else()
    add_definitions(-DFILAMENT_SUPPORTS_VULKAN=0)
endif()
