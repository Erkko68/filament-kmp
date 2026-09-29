# libfilament-c, the JNI image of the desktop and Android runtimes: the whole C API, the JNI runtime
# (src/main/cpp) and the forwarders :generateBindings writes, over one copy of Filament (one EntityManager).
# Added by c/CMakeLists.txt.

file(GLOB forwarders CONFIGURE_DEPENDS "${FILA_BINDINGS_DIR}/jni/*.c")
# Own C-only target: Visual Studio evaluates $<COMPILE_LANGUAGE> per target, so C next to C++ gets C++ flags.
add_library(fila-jni-forwarders OBJECT ${forwarders})
add_library(filament-c-jni SHARED
    src/main/cpp/FilaJni.cpp
    $<TARGET_OBJECTS:fila-jni-forwarders>
    $<TARGET_OBJECTS:fila-filament>
    $<TARGET_OBJECTS:fila-filamat>
    $<TARGET_OBJECTS:fila-filament-utils>
    $<TARGET_OBJECTS:fila-gltfio>
)
# $<1:…> keeps multi-config generators (Visual Studio) from appending a per-config subdir.
set_target_properties(filament-c-jni PROPERTIES
    OUTPUT_NAME filament-c
    LIBRARY_OUTPUT_DIRECTORY "$<1:${FILA_OUTPUT_DIR}>"
    RUNTIME_OUTPUT_DIRECTORY "$<1:${FILA_OUTPUT_DIR}>"
)
target_link_libraries(filament-c-jni PRIVATE ${FILAMENT_LIBRARIES} ${FILAMAT_LIBRARIES})

# Export only the JNI entry points; Filament's C++ stays local (no clashes in the host process), and
# the linker drops whatever they never reach.
set(seal "${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/filament-c-jni.map")

if (ANDROID)
    target_sources(filament-c-jni PRIVATE ../android/src/androidMain/cpp/FilaAndroid.cpp)
    target_link_libraries(filament-c-jni PRIVATE log GLESv3 EGL android jnigraphics)
    target_link_options(filament-c-jni PRIVATE
        "LINKER:--version-script=${seal}"
        "LINKER:-z,max-page-size=16384" # 16 KB pages, required from Android 15
        "LINKER:--gc-sections"
        "LINKER:--strip-all")
    set_target_properties(filament-c-jni PROPERTIES LINK_DEPENDS "${seal}")
    return()
endif()

# Desktop: jni.h from the JDK running the build.
if (APPLE)
    set(jni_os darwin)
elseif (WIN32)
    set(jni_os win32)
else()
    set(jni_os linux)
endif()
foreach(target filament-c-jni fila-jni-forwarders)
    target_include_directories(${target} PRIVATE "${JNI_HOME}/include" "${JNI_HOME}/include/${jni_os}")
endforeach()

if (APPLE)
    target_sources(filament-c-jni PRIVATE src/main/cpp/MetalHelper.mm)
    target_link_libraries(filament-c-jni PRIVATE
        "-framework Cocoa" "-framework Metal" "-framework QuartzCore" "-framework CoreVideo" "-framework OpenGL")
    target_link_options(filament-c-jni PRIVATE
        "LINKER:-exported_symbol,_Java_*"
        "LINKER:-exported_symbol,_JNI_OnLoad"
        "LINKER:-dead_strip")
elseif (WIN32)
    # A DLL exports only what's declared JNIEXPORT, which is exactly the seal.
    # PlatformWGL / BlueGL need OpenGL + GDI; utils::Path needs shlwapi.
    target_link_libraries(filament-c-jni PRIVATE opengl32 gdi32 user32 shlwapi)
else()
    target_link_libraries(filament-c-jni PRIVATE GL)
    target_link_options(filament-c-jni PRIVATE
        "LINKER:--version-script=${seal}"
        "LINKER:--gc-sections"
        "LINKER:-z,noexecstack" # one prebuilt .S object lacks the .note.GNU-stack marker
        # GNU ld scans archives once, left to right; the group rescans until Filament's cross-refs resolve.
        "LINKER:--start-group")
    target_link_libraries(filament-c-jni PRIVATE "-Wl,--end-group")
    set_target_properties(filament-c-jni PROPERTIES LINK_DEPENDS "${seal}")
endif()

# Release ships stripped (smaller runtime jars); the dynamic exports are untouched.
if (CMAKE_BUILD_TYPE STREQUAL "Release" AND NOT WIN32)
    if (APPLE)
        set(strip_args -x)
    else()
        set(strip_args --strip-unneeded)
    endif()
    add_custom_command(TARGET filament-c-jni POST_BUILD COMMAND strip ${strip_args} "$<TARGET_FILE:filament-c-jni>")
endif()
