cmake_minimum_required(VERSION 3.10)
project(filament-c-wrapper C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

option(FILA_ENABLE_LINKED_TESTS "Build linked C test programs (requires host Filament libs)" OFF)

# Define where our C headers are (the ones Kotlin will see)
set(C_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../include")

# Define where the original Filament C++ headers are
set(FILAMENT_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../filament-prebuilts/include")

# Define the source files for our wrapper
set(SOURCES
    src/filament/Camera.cpp
    src/filament/Engine.cpp
    src/filament/EntityManager.cpp
    src/filament/Renderer.cpp
    src/filament/Scene.cpp
    src/filament/View.cpp
)

# 1. Create our static library bridge
add_library(filament_c_wrapper STATIC ${SOURCES})

# Include the Filament C++ headers as PRIVATE (so C consumers don't see them)
# Include our C headers as PUBLIC (so consumers see them)
target_include_directories(filament_c_wrapper
    PRIVATE ${FILAMENT_INCLUDE_DIR}
    PUBLIC ${C_INCLUDE_DIR}
)

if (FILA_ENABLE_LINKED_TESTS AND APPLE)
    set(FILA_MAC_PREBUILT_LIB_DIR
        "${CMAKE_CURRENT_SOURCE_DIR}/../filament-prebuilts/mac/lib/arm64"
        CACHE PATH "Path to macOS arm64 Filament prebuilt static libraries"
    )

    file(GLOB FILA_MAC_PREBUILT_LIBS "${FILA_MAC_PREBUILT_LIB_DIR}/lib*.a")

    if (NOT FILA_MAC_PREBUILT_LIBS)
        message(FATAL_ERROR "No prebuilt static libraries found in: ${FILA_MAC_PREBUILT_LIB_DIR}")
    endif ()

    foreach(prebuilt_lib ${FILA_MAC_PREBUILT_LIBS})
        if (NOT EXISTS "${prebuilt_lib}")
            message(FATAL_ERROR "Missing required Filament prebuilt library: ${prebuilt_lib}")
        endif ()
    endforeach()

    target_link_libraries(filament_c_wrapper
        PUBLIC
            ${FILA_MAC_PREBUILT_LIBS}
            "-framework Cocoa"
            "-framework CoreVideo"
            "-framework IOKit"
            "-framework Metal"
            "-framework QuartzCore"
    )
endif ()

# 2. Setup tests
enable_testing()
add_subdirectory(test)
