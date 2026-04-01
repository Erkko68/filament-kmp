#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

BUILD_DIR=build
mkdir -p "$BUILD_DIR"
cd "$BUILD_DIR"

cmake ..
cmake --build . --target filament_c_wrapper test_headers

echo "Headers tests compiled successfully."

