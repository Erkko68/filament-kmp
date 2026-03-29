#!/bin/bash
set -e

# Navigate to the c-wrapper directory (parent of the test directory)
cd "$(dirname "$0")/.."

# Build directory
BUILD_DIR=build

# Default: run smoke tests
MODE=smoke
if [[ "$1" == "--onscreen" ]]; then
  MODE=onscreen
fi

echo "Creating build directory..."
mkdir -p "$BUILD_DIR"
cd "$BUILD_DIR"

echo "Configuring CMake..."
cmake .. -DFILA_ENABLE_LINKED_TESTS=ON

echo "Building wrapper + integration tests..."
make filament_c_wrapper
if [[ "$MODE" == "smoke" ]]; then
  make $(cd ../test/integration/smoke && for f in *_program.c; do echo -n "test_${f%.c} "; done)
  echo "Running smoke integration tests..."
  ctest --output-on-failure
elif [[ "$MODE" == "onscreen" ]]; then
  make $(cd ../test/integration/onscreen && for f in *_program.mm; do echo -n "test_${f%.mm} "; done)
  echo "Onscreen integration tests built. Run them manually from c-wrapper/build/test/"
fi

echo "Integration tests complete."
