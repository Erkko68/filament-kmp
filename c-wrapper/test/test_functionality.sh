#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

BUILD_DIR=build
mkdir -p "$BUILD_DIR"
cd "$BUILD_DIR"

cmake .. -DFILA_ENABLE_LINKED_TESTS=ON
cmake --build . --target filament_c_wrapper test_functionality
ctest --output-on-failure -L functionality

echo "Functionality tests completed."

