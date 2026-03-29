# Progressive test layout:
# - module/: compile-only checks for header usability
# - integration/: optional linked programs that use multiple APIs together

# Automatically find all *_test.c files in module/
file(GLOB MODULE_TEST_SOURCES
    module/*_test.c
)

set(MODULE_TEST_TARGETS)

foreach(source_file ${MODULE_TEST_SOURCES})
    get_filename_component(filename ${source_file} NAME_WE)
    set(target_name test_${filename})
    add_library(${target_name} OBJECT ${source_file})
    target_include_directories(${target_name} PRIVATE ${C_INCLUDE_DIR})
    list(APPEND MODULE_TEST_TARGETS ${target_name})
endforeach()

add_custom_target(test_compile_modules
    DEPENDS ${MODULE_TEST_TARGETS}
)

add_custom_target(test_compile_all
    DEPENDS test_compile_modules
)

# Integration tests: dynamic discovery for smoke and onscreen
if (APPLE)
    enable_language(OBJCXX)
endif ()

# Smoke tests
file(GLOB SMOKE_TEST_SOURCES integration/smoke/*_program.c)
foreach(source_file ${SMOKE_TEST_SOURCES})
    get_filename_component(filename ${source_file} NAME_WE)
    set(target_name test_${filename})
    add_executable(${target_name} ${source_file})
    target_link_libraries(${target_name} PRIVATE filament_c_wrapper)
    add_test(NAME ${filename} COMMAND ${target_name})
endforeach()

# Onscreen tests (macOS only)
if (APPLE)
    file(GLOB ONSCREEN_TEST_SOURCES integration/onscreen/*_program.mm)
    foreach(source_file ${ONSCREEN_TEST_SOURCES})
        get_filename_component(filename ${source_file} NAME_WE)
        set(target_name test_${filename})
        add_executable(${target_name} ${source_file})
        target_link_libraries(${target_name} PRIVATE filament_c_wrapper)
        # Optionally add_test here if you want to run onscreen tests via CTest
    endforeach()
endif ()
