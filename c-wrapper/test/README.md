# C Wrapper Test Layout

This directory is intentionally split into two categories:

- `headers/`: compile-only tests that verify public C header usability and symbol visibility.
- `functionality/`: linked runtime programs that exercise API behavior.

## Naming

- Header test files: `headers_<area>.c`
- Functionality test files: `functionality_<scenario>.c`

CMake target names are generated as `test_<filename-without-extension>`.

## Header Test Format (Required)

Each file in `test/headers/` uses this exact pattern:

```c
#include "<primary-header>.h"

void test_<filename_without_extension>(void) {
    // compile-only calls that touch the module API
}
```

Rules:

- No `main()` in `headers/*.c`.
- Entry symbol must be `void test_<filename_without_extension>(void)`.
- Keep tests compile-only (no printing/assert runtime flow required).
- Touch every public function in the target header where practical.

## Build and run

From `c-wrapper/`:

```bash
./test/test_headers.sh
./test/test_functionality.sh
```

Or with CMake directly:

```bash
cmake -S . -B build
cmake --build build --target test_headers

cmake -S . -B build -DFILA_ENABLE_LINKED_TESTS=ON
cmake --build build --target test_functionality
ctest --test-dir build --output-on-failure -L functionality
```

## Optional Material Fixture Checks (Batch D)

`functionality_color_options_material_metadata` supports an optional real material package path via:

- `FILA_TEST_MATERIAL_PACKAGE`

When set, the test loads the binary package, builds a material, and runs stricter metadata checks.

With the sample `test/materials/mat.mat` present, `test/test_functionality.sh` auto-compiles it to
`test/materials/mat.filamat` when `matc` is available and exports `FILA_TEST_MATERIAL_PACKAGE`
for the test run.

You can also provide a precompiled package explicitly:

```bash
export FILA_TEST_MATERIAL_PACKAGE="/absolute/path/to/sample.filamat"
./test/test_functionality.sh
```


