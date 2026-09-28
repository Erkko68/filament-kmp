# Compiler settings every translation unit needs to link against the platform's Filament archives.

if (MSVC)
    # Static CRT (/MT) like the prebuilts: the JVM's own msvcp140.dll conflicts with /MD.
    set(CMAKE_MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
    # Upstream utils/algorithm.h uses memcpy in a template without including <cstring>, which MSVC's
    # two-phase lookup rejects. TODO: drop once upstream includes it.
    add_compile_options(/FIcstring)
elseif (FILAMENT_PLATFORM STREQUAL "linux")
    # The prebuilts are built with clang against libc++; mixing in libstdc++ won't link.
    add_compile_options(-stdlib=libc++)
    add_link_options(-stdlib=libc++)
endif()

if (FILAMENT_PLATFORM MATCHES "^(linux|android)$")
    # Lets the linker's gc-sections drop what the sealed exports never reach.
    add_compile_options(-ffunction-sections -fdata-sections)
endif()

# Libraries built from source (build-logic BuildFilamentFromSourceTask) carry their own
# uberarchive.h: resgen bakes the archive size in, so it must win over include/'s copy.
if (EXISTS "${FILAMENT_LIB_DIR}/../include")
    include_directories(BEFORE "${FILAMENT_LIB_DIR}/../include")
endif()
