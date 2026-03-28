#!/bin/bash
set -e

# Navigate to the c-wrapper directory (parent of the test directory)
cd "$(dirname "$0")/.."

echo "Creating build directory..."
mkdir -p build
cd build

echo "Configuring CMake..."
cmake .. -DFILA_ENABLE_LINKED_TESTS=${FILA_ENABLE_LINKED_TESTS:-OFF}

echo "Building wrapper + progressive compile tests (engine + scene + renderer + view + camera)..."
# Build only compile-safe targets by default.
make filament_c_wrapper test_compile_all

if [ "${FILA_ENABLE_LINKED_TESTS:-OFF}" = "ON" ]; then
  echo "Building and running linked integration tests..."
  make test_program_engine_scene test_program_engine_scene_renderer test_program_engine_scene_view
  ctest --output-on-failure
fi

echo ""
echo "✅ Build successful! Progressive tests for Engine, Scene, Renderer, View, and Camera are in place."
