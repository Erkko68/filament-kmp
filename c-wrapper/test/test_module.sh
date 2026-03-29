#!/bin/bash
set -e

# Navigate to the c-wrapper directory (parent of the test directory)
cd "$(dirname "$0")/.."

BUILD_DIR=build

echo "Creating build directory..."
mkdir -p "$BUILD_DIR"
cd "$BUILD_DIR"

echo "Configuring CMake..."
cmake ..

echo "Building wrapper + module tests..."
make filament_c_wrapper test_compile_modules

echo "Module tests build complete."
