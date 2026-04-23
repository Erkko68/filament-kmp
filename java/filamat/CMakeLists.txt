cmake_minimum_required(VERSION 3.19)
project(filamat-jni)

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

add_filament_library(filamat)
add_filament_library(shaders)
add_filament_library(filament)
add_filament_library(backend)
add_filament_library(utils)
add_filament_library(math)
add_filament_library(filabridge)
add_filament_library(filaflat)
add_filament_library(smol-v)
add_filament_library(zstd)
add_filament_library(bluegl)
add_filament_library(bluevk)

add_library(filamat-jni SHARED
    src/main/cpp/MaterialBuilder.cpp
)

target_include_directories(filamat-jni PRIVATE
    src/main/cpp
    ${JNI_INCLUDE_DIRS}
    ${FILAMENT_INCLUDE_DIR}
)

target_link_libraries(filamat-jni
    PRIVATE filamat
    PRIVATE shaders
    PRIVATE filament
    PRIVATE backend
    PRIVATE utils
    PRIVATE filabridge
    PRIVATE filaflat
    PRIVATE smol-v
    PRIVATE zstd
    PRIVATE bluegl
    PRIVATE bluevk
    ${JNI_LIBRARIES}
)

if (APPLE)
    target_link_libraries(filamat-jni PRIVATE
        "-framework Metal"
        "-framework Foundation"
        "-framework AppKit"
        "-framework QuartzCore"
        "-framework CoreVideo"
        "-framework OpenGL"
    )
endif()
