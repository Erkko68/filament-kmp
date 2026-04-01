# Direct test layout:
# - headers/: compile-only API surface checks
# - functionality/: linked runtime programs that exercise behavior

file(GLOB HEADER_TEST_SOURCES CONFIGURE_DEPENDS
    headers/*.c
)

set(HEADER_TEST_TARGETS)
foreach(source_file ${HEADER_TEST_SOURCES})
    get_filename_component(filename ${source_file} NAME_WE)
    set(target_name test_${filename})
    add_library(${target_name} OBJECT ${source_file})
    target_include_directories(${target_name} PRIVATE ${C_INCLUDE_DIR})
    list(APPEND HEADER_TEST_TARGETS ${target_name})
endforeach()

add_custom_target(test_headers
    DEPENDS ${HEADER_TEST_TARGETS}
)

# Linked runtime functionality tests are optional and require host Filament libs.
if (FILA_ENABLE_LINKED_TESTS)
    file(GLOB FUNCTIONALITY_TEST_SOURCES CONFIGURE_DEPENDS
        functionality/*.c
    )

    set(FUNCTIONALITY_TEST_TARGETS)
    foreach(source_file ${FUNCTIONALITY_TEST_SOURCES})
        get_filename_component(filename ${source_file} NAME_WE)
        set(target_name test_${filename})
        add_executable(${target_name} ${source_file})
        target_link_libraries(${target_name} PRIVATE filament_c_wrapper)
        add_test(NAME ${filename} COMMAND ${target_name})
        set_tests_properties(${filename} PROPERTIES LABELS functionality)
        list(APPEND FUNCTIONALITY_TEST_TARGETS ${target_name})
    endforeach()

    add_custom_target(test_functionality
        DEPENDS ${FUNCTIONALITY_TEST_TARGETS}
    )
else()
    add_custom_target(test_functionality)
endif()

add_custom_target(test_all
    DEPENDS test_headers test_functionality
)
