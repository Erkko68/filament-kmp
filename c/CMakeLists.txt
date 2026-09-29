cmake_minimum_required(VERSION 3.22)

# The Fila* C API over Filament, built by build-logic's CMakeBuildTask, which passes:
#   FILAMENT_PLATFORM     ios | ios-simulator | macos | linux | windows | android | wasm
#   FILAMENT_LIB_DIR      Filament's static libraries for that platform (prebuilts/<target>/lib)
#   FILAMENT_INCLUDE_DIR  Filament's public headers (include/)
#   FILA_BINDINGS_DIR     :generateBindings output (JNI forwarders, wasm export lists)
#   FILA_OUTPUT_DIR       where the built libraries go
# plus the toolchain (iOS sysroot, NDK, Emscripten, clang on Linux).
#
# Each C module compiles once into an OBJECT library; the platform's consumer links them:
#   ios / ios-simulator   one static lib per module, packed into the Kotlin/Native klibs
#   macos/linux/windows   ../jni: libfilament-c, the JNI image of the desktop runtime
#   android               ../jni: libfilament-c.so, plus the Android-only entry points
#   wasm                  ../web: filament-kmp.wasm and filamat-kmp.wasm
project(filament-c C CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_C_STANDARD 11)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

include(cmake/Platform.cmake)
include(cmake/FilamentLibraries.cmake)

set(FILA_MODULES filament filamat filament-utils gltfio)
# Modules on the generated API (<module>/generated + <module>/manual); the rest still build <module>/cpp.
set(FILA_GENERATED_MODULES filamat filament-utils gltfio)
foreach(module IN LISTS FILA_MODULES)
    if (NOT module IN_LIST FILA_GENERATED_MODULES)
        include_directories(${module}/c)
    endif()
endforeach()
# filament's hand-written C includes its conversion helpers from filament/cpp. The JNI forwarders include
# headers by their path under c/.
include_directories(filament/cpp ${CMAKE_CURRENT_SOURCE_DIR} ${FILAMENT_INCLUDE_DIR})

foreach(module IN LISTS FILA_MODULES)
    if (module IN_LIST FILA_GENERATED_MODULES)
        file(GLOB sources CONFIGURE_DEPENDS ${module}/generated/*.cpp ${module}/manual/*.cpp)
    else()
        file(GLOB sources CONFIGURE_DEPENDS ${module}/cpp/*.cpp)
    endif()
    # ponytail: filament moves record by record; drop this once it joins FILA_GENERATED_MODULES.
    if (module STREQUAL "filament")
        file(STRINGS filament/migrated.txt records)
        foreach(record IN LISTS records)
            list(APPEND sources filament/generated/${record}.cpp)
        endforeach()
        file(GLOB manual CONFIGURE_DEPENDS filament/manual/*.cpp)
        list(APPEND sources ${manual})
    endif()
    add_library(fila-${module} OBJECT ${sources})
endforeach()

if (FILAMENT_PLATFORM MATCHES "^ios")
    # Kotlin/Native links these plus the Filament archives (see build-logic KotlinNativeCLibs).
    foreach(module IN LISTS FILA_MODULES)
        add_library(${module}-c STATIC $<TARGET_OBJECTS:fila-${module}>)
        set_target_properties(${module}-c PROPERTIES ARCHIVE_OUTPUT_DIRECTORY "${FILA_OUTPUT_DIR}")
    endforeach()
elseif (FILAMENT_PLATFORM STREQUAL "wasm")
    add_subdirectory(../web web)
else()
    add_subdirectory(../jni jni)
endif()
