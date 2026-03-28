# Progressive test layout:
# - module/: compile-only checks for header usability
# - signature/: compile-only checks that lock function signatures
# - integration/: optional linked programs that use multiple APIs together

add_library(test_module_engine_compile OBJECT
    module/engine/engine_module_compile.c
)
add_library(test_module_scene_compile OBJECT
    module/scene/scene_module_compile.c
)

add_library(test_signature_engine_compile OBJECT
    signature/engine/engine_signature_compile.c
)
add_library(test_signature_scene_compile OBJECT
    signature/scene/scene_signature_compile.c
)

foreach(target_name
    test_module_engine_compile
    test_module_scene_compile
    test_signature_engine_compile
    test_signature_scene_compile
)
    target_include_directories(${target_name} PRIVATE ${C_INCLUDE_DIR})
endforeach()

add_custom_target(test_compile_modules
    DEPENDS
        test_module_engine_compile
        test_module_scene_compile
)

add_custom_target(test_compile_signatures
    DEPENDS
        test_signature_engine_compile
        test_signature_scene_compile
)

add_custom_target(test_compile_all
    DEPENDS
        test_compile_modules
        test_compile_signatures
)

if (FILA_ENABLE_LINKED_TESTS)
    add_executable(test_program_engine_scene
        integration/programs/engine_scene_program.c
    )
    target_link_libraries(test_program_engine_scene PRIVATE filament_c_wrapper)
    add_test(NAME engine_scene_program COMMAND test_program_engine_scene)
endif ()

