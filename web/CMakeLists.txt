# The wasm runtimes of the web targets, added by c/CMakeLists.txt. Kotlin binds their exports by name
# as globals (src/wasm/fila-globals.js); the export lists and type tables come from :generateBindings.
#   filament-kmp.wasm   filament + filament-utils + gltfio (they share Engine pointers)
#   filamat-kmp.wasm    the optional runtime material compiler: shares nothing with the engine, and large

# Flags follow upstream web/filament-js/CMakeLists.txt, minus embind (--bind).
set(common_link_options
    "-sEXPORTED_RUNTIME_METHODS=HEAP8,HEAP16,HEAP32,HEAPU8,HEAPU16,HEAPU32,HEAPF32,HEAPF64,GL,addFunction,removeFunction,stringToUTF8,lengthBytesUTF8,UTF8ToString"
    "-sALLOW_MEMORY_GROWTH=1"
    "-sALLOW_TABLE_GROWTH=1"
    "-sMODULARIZE=1"
    # C++ exceptions escaping to JS (Filament panics) become Errors carrying the message.
    "-fexceptions"
    "-sEXCEPTION_STACK_TRACES=1"
)

# RUNTIME links the C API objects into <runtime>.{js,wasm}, exporting what Kotlin binds.
function(wasm_runtime target runtime factory)
    set(bindings "${FILA_BINDINGS_DIR}/wasm")
    set(globals "${CMAKE_CURRENT_SOURCE_DIR}/src/wasm/fila-globals.js")
    set_target_properties(${target} PROPERTIES OUTPUT_NAME ${runtime} RUNTIME_OUTPUT_DIRECTORY "${FILA_OUTPUT_DIR}")
    target_link_options(${target} PRIVATE ${common_link_options}
        "-sEXPORTED_FUNCTIONS=@${bindings}/${runtime}-exports.txt"
        "-sEXPORT_NAME=${factory}"
        "--post-js=${bindings}/${runtime}-types.js"
        "--post-js=${globals}")
    set_property(TARGET ${target} APPEND PROPERTY LINK_DEPENDS
        "${bindings}/${runtime}-exports.txt;${bindings}/${runtime}-types.js;${globals}")
endfunction()

add_executable(filament-web
    $<TARGET_OBJECTS:fila-filament>
    $<TARGET_OBJECTS:fila-filament-utils>
    $<TARGET_OBJECTS:fila-gltfio>
)
target_link_libraries(filament-web PRIVATE ${FILAMENT_LIBRARIES})
wasm_runtime(filament-web filament-kmp createFilamentModule)
target_link_options(filament-web PRIVATE "-sUSE_WEBGL2=1" "-sFULL_ES3" "-sMIN_WEBGL_VERSION=2" "-sMAX_WEBGL_VERSION=2")

add_executable(filamat-web $<TARGET_OBJECTS:fila-filamat>)
target_link_libraries(filamat-web PRIVATE ${FILAMAT_LIBRARIES} filabridge smol-v utils)
wasm_runtime(filamat-web filamat-kmp createFilamatModule)
# glslang's parser recurses deeply: the 64 KB default stack overflows (surfaces as OOB traps).
target_link_options(filamat-web PRIVATE "-sSTACK_SIZE=4MB")
