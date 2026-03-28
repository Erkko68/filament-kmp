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
    src/filament/Engine.cpp
    src/filament/Scene.cpp
)

# 1. Create our static library bridge
add_library(filament_c_wrapper STATIC ${SOURCES})

# Include the Filament C++ headers as PRIVATE (so C consumers don't see them)
# Include our C headers as PUBLIC (so consumers see them)
target_include_directories(filament_c_wrapper
    PRIVATE ${FILAMENT_INCLUDE_DIR}
    PUBLIC ${C_INCLUDE_DIR}
)

# 2. Setup tests
enable_testing()
add_subdirectory(test)
