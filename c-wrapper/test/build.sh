#!/bin/bash
set -e

# Navigate to the c-wrapper directory (parent of the test directory)
cd "$(dirname "$0")/.."

echo "Creating build directory..."
mkdir -p build
cd build

echo "Configuring CMake..."
cmake .. \
  -DFILA_ENABLE_LINKED_TESTS=${FILA_ENABLE_LINKED_TESTS:-OFF} \
  -DFILA_ENABLE_MACOS_ONSCREEN_TEST=${FILA_ENABLE_MACOS_ONSCREEN_TEST:-OFF}

echo "Building wrapper + progressive compile tests (engine + scene + renderer + view + camera + entity manager)..."
# Build only compile-safe targets by default.
make filament_c_wrapper test_compile_all

if [ "${FILA_ENABLE_LINKED_TESTS:-OFF}" = "ON" ]; then
  echo "Building and running linked integration tests..."
  make test_program_engine_scene test_program_engine_scene_renderer test_program_engine_scene_view test_program_engine_scene_view_first_frame test_program_entity_manager

  if [ "${FILA_ENABLE_MACOS_ONSCREEN_TEST:-OFF}" = "ON" ]; then
    echo "Building optional macOS on-screen smoke program target..."
    make test_program_engine_scene_view_macos_window
    echo "Run it manually from c-wrapper/build/test/test_program_engine_scene_view_macos_window"
  fi

  ctest --output-on-failure
fi

echo ""
echo "✅ Build successful! Progressive tests for Engine, Scene, Renderer, View, Camera, and EntityManager are in place."
