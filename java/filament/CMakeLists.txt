cmake_minimum_required(VERSION 3.19)
project(filament-jni LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 20)

# --- Platform specific setup (Similar to c/CMakeLists.txt) ---
set(FILAMENT_PLATFORM "macos" CACHE STRING "Target platform (ios, ios-simulator, macos, linux, windows)")
set(FILAMENT_ARCH "arm64" CACHE STRING "Target architecture (arm64, x64)")

# Paths
set(FILAMENT_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../include")
set(FILAMENT_LIB_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../prebuilts/${FILAMENT_PLATFORM}${FILAMENT_ARCH}/lib")

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

if (APPLE)
    target_sources(filament-jni PRIVATE src/main/cpp/common/AwtHelper.mm)
else()
    target_sources(filament-jni PRIVATE src/main/cpp/common/AwtHelper.cpp)
endif()

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
    target_link_libraries(filament-jni PRIVATE "-framework Cocoa")
    target_link_libraries(filament-jni PRIVATE "-framework Metal")
    target_link_libraries(filament-jni PRIVATE "-framework QuartzCore")
    target_link_libraries(filament-jni PRIVATE "-framework CoreVideo")
endif()

# JAWT is required by AwtHelper to extract native window handles.
set(JAVA_LIB_HINTS "${JAVA_HOME}/lib" "${JAVA_HOME}/jre/lib")
find_library(JAWT_LIB jawt HINTS ${JAVA_LIB_HINTS})
if (JAWT_LIB)
    message(STATUS "Found JAWT library at: ${JAWT_LIB}")
    target_link_libraries(filament-jni PRIVATE ${JAWT_LIB})
    
    if (APPLE)
        # On macOS, JAWT relies on libawt_lwawt.dylib
        find_library(AWT_LWAWT_LIB awt_lwawt HINTS ${JAVA_LIB_HINTS})
        if (AWT_LWAWT_LIB)
            message(STATUS "Found AWT LWAWT library at: ${AWT_LWAWT_LIB}")
            target_link_libraries(filament-jni PRIVATE ${AWT_LWAWT_LIB})
        endif()
    endif()
else()
    message(WARNING "JAWT library not found. AwtHelper may fail to link or work at runtime.")
endif()
