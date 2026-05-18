cmake_minimum_required(VERSION 3.19...3.28)

if (UNIX AND NOT APPLE)
    find_program(CLANGPP clang++)
    if (CLANGPP)
        set(CMAKE_CXX_COMPILER ${CLANGPP})
    else()
        message(FATAL_ERROR "clang++ not found. Install it with: sudo apt install clang libc++-dev libc++abi-dev")
    endif()
endif()

project(gltfio-jni)

set(CMAKE_CXX_STANDARD 20)

if (UNIX AND NOT APPLE)
    add_compile_options(-stdlib=libc++)
    add_link_options(-stdlib=libc++)
endif()

# Paths
set(FILAMENT_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../include")
set(FILAMENT_PREBUILT_PREFIX ${FILAMENT_PLATFORM})
if (WIN32)
    set(FILAMENT_PREBUILT_PREFIX "mingw")
endif()
set(FILAMENT_LIB_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../prebuilts/${FILAMENT_PREBUILT_PREFIX}${FILAMENT_ARCH}/lib")

find_package(JNI REQUIRED)

macro(add_filament_library NAME)
    add_library(${NAME} STATIC IMPORTED)
    if (WIN32)
        set_target_properties(${NAME} PROPERTIES IMPORTED_LOCATION ${FILAMENT_LIB_DIR}/${NAME}.lib)
    else()
        set_target_properties(${NAME} PROPERTIES IMPORTED_LOCATION ${FILAMENT_LIB_DIR}/lib${NAME}.a)
    endif()
endmacro()

add_filament_library(gltfio)
add_filament_library(gltfio_core)
add_filament_library(filament)
add_filament_library(backend)
add_filament_library(bluegl)
add_filament_library(bluevk)
add_filament_library(filamat)
add_filament_library(shaders)
add_filament_library(filabridge)
add_filament_library(filaflat)
add_filament_library(utils)
add_filament_library(geometry)
add_filament_library(smol-v)
add_filament_library(zstd)
add_filament_library(uberzlib)
add_filament_library(uberarchive)
add_filament_library(ktxreader)
add_filament_library(dracodec)
add_filament_library(stb)
add_filament_library(meshoptimizer)
add_filament_library(basis_transcoder)
add_filament_library(image)

add_library(gltfio-jni SHARED
    src/main/cpp/AssetLoader.cpp
    src/main/cpp/FilamentAsset.cpp
    src/main/cpp/ResourceLoader.cpp
    src/main/cpp/FilamentInstance.cpp
    src/main/cpp/Animator.cpp
    src/main/cpp/MaterialProvider.cpp
    src/main/cpp/MaterialKey.cpp
    src/main/cpp/Gltfio.cpp
    ../filament/src/main/cpp/common/NioUtils.cpp
)


target_include_directories(gltfio-jni PRIVATE
    src/main/cpp
    ../filament/src/main/cpp
    ${JNI_INCLUDE_DIRS}
    ${FILAMENT_INCLUDE_DIR}
)

target_link_libraries(gltfio-jni
    PRIVATE gltfio
    PRIVATE gltfio_core
    PRIVATE filament
    PRIVATE filamat
    PRIVATE shaders
    PRIVATE filabridge
    PRIVATE filaflat
    PRIVATE backend
    PRIVATE bluegl
    PRIVATE bluevk
    PRIVATE utils
    PRIVATE geometry
    PRIVATE smol-v
    PRIVATE zstd
    PRIVATE uberzlib
    PRIVATE uberarchive
    PRIVATE ktxreader
    PRIVATE dracodec
    PRIVATE stb
    PRIVATE meshoptimizer
    PRIVATE basis_transcoder
    PRIVATE image
    ${JNI_LIBRARIES}
)

if (APPLE)
    target_link_libraries(gltfio-jni PRIVATE
        "-framework Metal"
        "-framework Foundation"
        "-framework AppKit"
        "-framework QuartzCore"
        "-framework CoreVideo"
        "-framework OpenGL"
    )
elseif (WIN32)
    # shlwapi is needed by utils::Path (PathIsRelativeA, etc.)
    target_link_libraries(gltfio-jni PRIVATE opengl32 gdi32 user32 shlwapi)
elseif (UNIX)
    target_link_libraries(gltfio-jni PRIVATE GL)
endif()
