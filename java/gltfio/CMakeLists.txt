cmake_minimum_required(VERSION 3.19)
project(gltfio-jni)

set(CMAKE_CXX_STANDARD 20)

# Paths
set(FILAMENT_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../include")
set(FILAMENT_LIB_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../prebuilts/${FILAMENT_PLATFORM}${FILAMENT_ARCH}/lib")

find_package(JNI REQUIRED)

macro(add_filament_library NAME)
    add_library(${NAME} STATIC IMPORTED)
    if (WIN32)
        set_target_properties(${NAME} PROPERTIES IMPORTED_LOCATION ${FILAMENT_LIB_DIR}/${NAME}.lib)
    else()
        set_target_properties(${NAME} PROPERTIES IMPORTED_LOCATION ${FILAMENT_LIB_DIR}/lib${NAME}.a)
    endif()
endmacro()

add_filament_library(gltfio_core)
add_filament_library(filament)
add_filament_library(backend)
add_filament_library(utils)
add_filament_library(math)
add_filament_library(dracodec)
add_filament_library(stb)
add_filament_library(ktxreader)
add_filament_library(geometry)

add_library(gltfio-jni SHARED
    src/main/cpp/AssetLoader.cpp
    src/main/cpp/FilamentAsset.cpp
    src/main/cpp/ResourceLoader.cpp
    src/main/cpp/FilamentInstance.cpp
    src/main/cpp/Animator.cpp
    src/main/cpp/MaterialProvider.cpp
)

target_include_directories(gltfio-jni PRIVATE
    src/main/cpp
    ${JNI_INCLUDE_DIRS}
    ${FILAMENT_INCLUDE_DIR}
)

target_link_libraries(gltfio-jni
    PRIVATE gltfio_core
    PRIVATE filament
    PRIVATE backend
    PRIVATE utils
    PRIVATE dracodec
    PRIVATE stb
    PRIVATE ktxreader
    PRIVATE geometry
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
endif()
