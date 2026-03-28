# Filament C Wrapper Tests

This test suite is designed to grow progressively as new C APIs are added.

## Folder layout

- `module/`: compile-only usage checks per API module (header usability)
- `signature/`: compile-only function signature locks per API module
- `integration/programs/`: optional linked C programs that exercise multiple APIs together

Current modules covered:

- Engine
- Scene
- Renderer
- View
- Camera

## Default workflow (no host Filament libs required)

Build wrapper + compile-only tests:

```bash
cd /Users/eric/IdeaProjects/filament-kmp-core/c-wrapper/test
./build.sh
```

This builds:

- `filament_c_wrapper`
- `test_compile_modules`
- `test_compile_signatures`
- `test_compile_all`

When linked tests are enabled, integration programs currently include:

- `test_program_engine_scene`
- `test_program_engine_scene_renderer`
- `test_program_engine_scene_view`
- `test_program_engine_scene_view_first_frame` (headless swap-chain frame smoke test)

## Optional linked integration programs

Enable only when host Filament libraries are available in your toolchain:

```bash
cd /Users/eric/IdeaProjects/filament-kmp-core/c-wrapper/test
FILA_ENABLE_LINKED_TESTS=ON ./build.sh
```

## Adding a new API module later

1. Add `test/module/<api>/<api>_module_compile.c`
2. Add `test/signature/<api>/<api>_signature_compile.c`
3. Add targets in `test/CMakeLists.txt`
4. Optionally add an integration C program in `test/integration/programs/`

