# Progressive test layout:
# - module/: compile-only checks for header usability
# - signature/: compile-only checks that lock function signatures
# - integration/: optional linked programs that use multiple APIs together

option(FILA_ENABLE_MACOS_ONSCREEN_TEST "Build macOS on-screen window smoke program" OFF)

add_library(test_module_engine_compile OBJECT
    module/engine/engine_module_compile.c
)
add_library(test_module_scene_compile OBJECT
    module/scene/scene_module_compile.c
)
add_library(test_module_renderer_compile OBJECT
    module/renderer/renderer_module_compile.c
)
add_library(test_module_view_compile OBJECT
    module/view/view_module_compile.c
)
add_library(test_module_camera_compile OBJECT
    module/camera/camera_module_compile.c
)
add_library(test_module_entity_manager_compile OBJECT
    module/entity_manager/entity_manager_module_compile.c
)
add_library(test_module_fence_compile OBJECT
    module/fence/fence_module_compile.c
)
add_library(test_module_swapchain_compile OBJECT
    module/swapchain/swapchain_module_compile.c
)

add_library(test_signature_engine_compile OBJECT
    signature/engine/engine_signature_compile.c
)
add_library(test_signature_scene_compile OBJECT
    signature/scene/scene_signature_compile.c
)
add_library(test_signature_renderer_compile OBJECT
    signature/renderer/renderer_signature_compile.c
)
add_library(test_signature_view_compile OBJECT
    signature/view/view_signature_compile.c
)
add_library(test_signature_camera_compile OBJECT
    signature/camera/camera_signature_compile.c
)
add_library(test_signature_entity_manager_compile OBJECT
    signature/entity_manager/entity_manager_signature_compile.c
)
add_library(test_signature_fence_compile OBJECT
    signature/fence/fence_signature_compile.c
)
add_library(test_signature_swapchain_compile OBJECT
    signature/swapchain/swapchain_signature_compile.c
)

foreach(target_name
    test_module_engine_compile
    test_module_scene_compile
    test_module_renderer_compile
    test_module_view_compile
    test_module_camera_compile
    test_module_entity_manager_compile
    test_module_fence_compile
    test_module_swapchain_compile
    test_signature_engine_compile
    test_signature_scene_compile
    test_signature_renderer_compile
    test_signature_view_compile
    test_signature_camera_compile
    test_signature_entity_manager_compile
    test_signature_fence_compile
    test_signature_swapchain_compile
)
    target_include_directories(${target_name} PRIVATE ${C_INCLUDE_DIR})
endforeach()

add_custom_target(test_compile_modules
    DEPENDS
        test_module_engine_compile
        test_module_scene_compile
        test_module_renderer_compile
        test_module_view_compile
        test_module_camera_compile
        test_module_entity_manager_compile
        test_module_fence_compile
        test_module_swapchain_compile
)

add_custom_target(test_compile_signatures
    DEPENDS
        test_signature_engine_compile
        test_signature_scene_compile
        test_signature_renderer_compile
        test_signature_view_compile
        test_signature_camera_compile
        test_signature_entity_manager_compile
        test_signature_fence_compile
        test_signature_swapchain_compile
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

    add_executable(test_program_engine_scene_renderer
        integration/programs/engine_scene_renderer_program.c
    )
    target_link_libraries(test_program_engine_scene_renderer PRIVATE filament_c_wrapper)
    add_test(NAME engine_scene_renderer_program COMMAND test_program_engine_scene_renderer)

    add_executable(test_program_engine_scene_view
        integration/programs/engine_scene_view_program.c
    )
    target_link_libraries(test_program_engine_scene_view PRIVATE filament_c_wrapper)
    add_test(NAME engine_scene_view_program COMMAND test_program_engine_scene_view)

    add_executable(test_program_engine_scene_view_first_frame
        integration/programs/engine_scene_view_first_frame_program.c
    )
    target_link_libraries(test_program_engine_scene_view_first_frame PRIVATE filament_c_wrapper)
    add_test(NAME engine_scene_view_first_frame_program COMMAND test_program_engine_scene_view_first_frame)

    add_executable(test_program_entity_manager
        integration/programs/entity_manager_program.c
    )
    target_link_libraries(test_program_entity_manager PRIVATE filament_c_wrapper)
    add_test(NAME entity_manager_program COMMAND test_program_entity_manager)

    add_executable(test_program_engine_fence
        integration/programs/engine_fence_program.c
    )
    target_link_libraries(test_program_engine_fence PRIVATE filament_c_wrapper)
    add_test(NAME engine_fence_program COMMAND test_program_engine_fence)

    add_executable(test_program_engine_swapchain
        integration/programs/engine_swapchain_program.c
    )
    target_link_libraries(test_program_engine_swapchain PRIVATE filament_c_wrapper)
    add_test(NAME engine_swapchain_program COMMAND test_program_engine_swapchain)

    if (APPLE AND FILA_ENABLE_MACOS_ONSCREEN_TEST)
        enable_language(OBJCXX)
        add_executable(test_program_engine_scene_view_macos_window
            integration/programs/engine_scene_view_macos_window_program.mm
        )
        target_link_libraries(test_program_engine_scene_view_macos_window PRIVATE filament_c_wrapper)
    endif ()
endif ()

