cmake_minimum_required(VERSION 3.19)
project(filament-utils-jni)

set(CMAKE_CXX_STANDARD 20)

# --- Platform specific setup ---
set(FILAMENT_PLATFORM "macos" CACHE STRING "Target platform (ios, ios-simulator, macos, linux, windows)")
set(FILAMENT_ARCH "arm64" CACHE STRING "Target architecture (arm64, x64)")

# Paths
set(FILAMENT_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../include")
set(FILAMENT_LIB_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../prebuilts/${FILAMENT_PLATFORM}${FILAMENT_ARCH}/lib")

# JNI setup
find_package(JNI REQUIRED)

message(STATUS "Using Filament headers at: ${FILAMENT_INCLUDE_DIR}")
message(STATUS "Using Filament libraries at: ${FILAMENT_LIB_DIR}")

# Imported libraries Helper
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
add_filament_library(image)
add_filament_library(camutils)
add_filament_library(zstd)
add_filament_library(ktxreader)
add_filament_library(basis_transcoder)
add_filament_library(imageio-lite)
add_filament_library(meshoptimizer)
add_filament_library(filabridge)
add_filament_library(filaflat)
add_filament_library(smol-v)
    add_filament_library(bluegl)
    add_filament_library(bluevk)

# The JNI shared library
add_library(filament-utils-jni SHARED
    src/main/cpp/Bookmark.cpp
    src/main/cpp/HDRLoader.cpp
    src/main/cpp/Manipulator.cpp
    src/main/cpp/KTX1Loader.cpp
    src/main/cpp/TextureLoader.cpp
    src/main/cpp/Utils.cpp
    src/main/cpp/common/NioUtils.cpp
    src/main/cpp/common/CallbackUtils.cpp
    src/main/cpp/common/VirtualMachineEnv.cpp
)

target_include_directories(filament-utils-jni PRIVATE
    src/main/cpp
    ${JNI_INCLUDE_DIRS}
    ${FILAMENT_INCLUDE_DIR}
)

# Link common JNI sources from the filament module's directory to avoid duplication
# if CMake supports it, or we point to the files in filament module.
# For now, I'll assume they will be copied or symlinked, or I'll just use the full path.

target_link_libraries(filament-utils-jni
    PRIVATE filament
    PRIVATE backend
    PRIVATE utils
    PRIVATE image
    PRIVATE camutils
    PRIVATE zstd
    PRIVATE ktxreader
    PRIVATE basis_transcoder
    PRIVATE imageio-lite
    PRIVATE meshoptimizer
    PRIVATE filabridge
    PRIVATE filaflat
    PRIVATE smol-v
    PRIVATE bluegl
    PRIVATE bluevk
    ${JNI_LIBRARIES}
)

if (APPLE)
    if (FILAMENT_PLATFORM STREQUAL "macos")
        target_link_libraries(filament-utils-jni PRIVATE
            "-framework Metal"
            "-framework Foundation"
            "-framework AppKit"
            "-framework QuartzCore"
            "-framework CoreVideo"
            "-framework OpenGL"
        )
    else()
        target_link_libraries(filament-utils-jni PRIVATE
            "-framework Metal"
            "-framework Foundation"
            "-framework UIKit"
            "-framework QuartzCore"
            "-framework CoreVideo"
        )
    endif()
endif()
