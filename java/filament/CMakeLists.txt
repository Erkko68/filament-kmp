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

# --- Platform specific setup (Similar to c/CMakeLists.txt) ---
set(FILAMENT_PLATFORM "macos" CACHE STRING "Target platform (ios, ios-simulator, macos, linux, windows)")
set(FILAMENT_ARCH "arm64" CACHE STRING "Target architecture (arm64, x64)")

# Paths
set(FILAMENT_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../include")
# On Windows the prebuilts are stored under mingw<Arch>/ (Kotlin/Native target name convention)
set(FILAMENT_PREBUILT_PREFIX ${FILAMENT_PLATFORM})
if (WIN32)
    set(FILAMENT_PREBUILT_PREFIX "mingw")
endif()
set(FILAMENT_LIB_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../prebuilts/${FILAMENT_PREBUILT_PREFIX}${FILAMENT_ARCH}/lib")

# JNI setup
find_package(JNI REQUIRED)

message(STATUS "Using Filament headers at: ${FILAMENT_INCLUDE_DIR}")
message(STATUS "Using Filament libraries at: ${FILAMENT_LIB_DIR}")

# Imported libraries Helper (Simplified from c/CMakeLists.txt)
macro(add_filament_library NAME)
    add_library(${NAME} STATIC IMPORTED)
    if (WIN32)
        set_target_properties(${NAME} PROPERTIES IMPORTED_LOCATION ${FILAMENT_LIB_DIR}/${NAME}.lib)
    else()
        set_target_properties(${NAME} PROPERTIES IMPORTED_LOCATION ${FILAMENT_LIB_DIR}/lib${NAME}.a)
    endif()
endmacro()

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

# The JNI shared library
add_library(filament-jni SHARED
    src/main/cpp/Engine.cpp
    src/main/cpp/Renderer.cpp
    src/main/cpp/View.cpp
    src/main/cpp/Scene.cpp
    src/main/cpp/Camera.cpp
    src/main/cpp/EntityManager.cpp
    src/main/cpp/VertexBuffer.cpp
    src/main/cpp/IndexBuffer.cpp
    src/main/cpp/Texture.cpp
    src/main/cpp/Material.cpp
    src/main/cpp/RenderableManager.cpp
    src/main/cpp/LightManager.cpp
    src/main/cpp/TransformManager.cpp
    src/main/cpp/IndirectLight.cpp
    src/main/cpp/Skybox.cpp
    src/main/cpp/ColorGrading.cpp
    src/main/cpp/ToneMapper.cpp
    src/main/cpp/TextureSampler.cpp
    src/main/cpp/Fence.cpp
    src/main/cpp/BufferObject.cpp
    src/main/cpp/RenderTarget.cpp
    src/main/cpp/Stream.cpp
    src/main/cpp/MorphTargetBuffer.cpp
    src/main/cpp/SkinningBuffer.cpp
    src/main/cpp/SurfaceOrientation.cpp
    src/main/cpp/Colors.cpp
    src/main/cpp/MathUtils.cpp
    src/main/cpp/common/NioUtils.cpp
    src/main/cpp/common/CallbackUtils.cpp
    src/main/cpp/common/VirtualMachineEnv.cpp
    src/main/cpp/MaterialInstance.cpp
    src/main/cpp/SwapChain.cpp
)

target_include_directories(filament-jni PRIVATE
    src/main/cpp
    ${JNI_INCLUDE_DIRS}
    ${FILAMENT_INCLUDE_DIR}
)

target_link_libraries(filament-jni
    PRIVATE filament
    PRIVATE backend
    PRIVATE utils
    # PRIVATE math
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
    ${JNI_LIBRARIES}
)
if (APPLE)
    target_link_libraries(filament-jni PRIVATE "-framework Cocoa" "-framework Metal"
                                               "-framework QuartzCore" "-framework CoreVideo")
elseif (UNIX)
    target_link_libraries(filament-jni PRIVATE GL)
elseif (WIN32)
endif()
