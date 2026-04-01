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
