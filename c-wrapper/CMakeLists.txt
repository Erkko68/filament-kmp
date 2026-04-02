cmake_minimum_required(VERSION 3.10)
project(filament-c-wrapper C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

option(FILA_ENABLE_LINKED_TESTS "Build linked C test programs (requires host Filament libs)" OFF)

# Define where our C headers are (the ones Kotlin will see)
set(C_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/include")

# Define where the original Filament C++ headers are
set(FILAMENT_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../filament-prebuilts/include")

# Define the source files for our wrapper.
file(GLOB_RECURSE SOURCES CONFIGURE_DEPENDS
    "${CMAKE_CURRENT_SOURCE_DIR}/src/*.cpp"
)

# 1. Create our static library bridge
add_library(filament_c_wrapper STATIC ${SOURCES})

# Include the Filament C++ headers as PRIVATE (so C consumers don't see them)
# Include our C headers as PUBLIC (so consumers see them)
target_include_directories(filament_c_wrapper
    PRIVATE ${FILAMENT_INCLUDE_DIR}
    PUBLIC ${C_INCLUDE_DIR}
)

if (APPLE)
    # Try to auto-discover Filament prebuilt static libs for macOS/arm64. If found,
    # link them into the wrapper so integration tests and executables resolve
    # filament:: symbols.
    set(FILA_MAC_PREBUILT_LIB_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../filament-prebuilts/mac/lib/arm64")
    if (EXISTS ${FILA_MAC_PREBUILT_LIB_DIR})
        file(GLOB FILA_MAC_PREBUILT_LIBS "${FILA_MAC_PREBUILT_LIB_DIR}/lib*.a")
        if (FILA_MAC_PREBUILT_LIBS)
            message(STATUS "Linking Filament prebuilt static libs from: ${FILA_MAC_PREBUILT_LIB_DIR}")
            target_link_libraries(filament_c_wrapper
                PRIVATE
                    ${FILA_MAC_PREBUILT_LIBS}
                    "-framework Cocoa"
                    "-framework CoreVideo"
                    "-framework IOKit"
                    "-framework Metal"
                    "-framework QuartzCore"
            )
        else()
            message(WARNING "No Filament prebuilt libs found in ${FILA_MAC_PREBUILT_LIB_DIR}; integration executables will fail to link unless FILA_ENABLE_LINKED_TESTS is used and libs provided.")
        endif()
    else()
        message(WARNING "Filament prebuilt lib dir not found: ${FILA_MAC_PREBUILT_LIB_DIR}")
    endif()
endif()

# 2. Setup tests
enable_testing()
add_subdirectory(test)
