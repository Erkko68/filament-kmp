# Filament's static libraries (FILAMENT_LIB_DIR) as imported targets, and the set an image links.

function(filament_library name)
    set(file ${name})
    if (name STREQUAL "iblprefilter")
        set(file filament-iblprefilter)
    endif()
    add_library(${name} STATIC IMPORTED)
    set_target_properties(${name} PROPERTIES IMPORTED_LOCATION
        "${FILAMENT_LIB_DIR}/${CMAKE_STATIC_LIBRARY_PREFIX}${file}${CMAKE_STATIC_LIBRARY_SUFFIX}")
endfunction()

# What the whole C API needs, on every platform that links an image.
set(FILAMENT_LIBRARIES
    filament backend utils filaflat filabridge zstd smol-v
    ibl image geometry meshoptimizer camutils ktxreader filament-generatePrefilterMipmap
    gltfio_core uberzlib uberarchive dracodec stb basis_transcoder imageio-lite iblprefilter
)
set(FILAMAT_LIBRARIES filamat shaders)

if (FILAMENT_PLATFORM MATCHES "^(macos|linux|windows)$")
    list(APPEND FILAMENT_LIBRARIES bluegl bluevk gltfio)
elseif (FILAMENT_PLATFORM STREQUAL "android")
    list(APPEND FILAMENT_LIBRARIES bluevk perfetto abseil)
elseif (FILAMENT_PLATFORM STREQUAL "wasm")
    list(APPEND FILAMENT_LIBRARIES mikktspace)
endif()

foreach(lib IN LISTS FILAMENT_LIBRARIES FILAMAT_LIBRARIES)
    filament_library(${lib})
endforeach()
