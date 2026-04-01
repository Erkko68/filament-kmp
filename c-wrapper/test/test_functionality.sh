#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
ROOT_DIR="$(pwd -P)"

BUILD_DIR=build
mkdir -p "$BUILD_DIR"
cd "$BUILD_DIR"

MATERIAL_SOURCE="$ROOT_DIR/test/materials/mat.mat"
MATERIAL_PACKAGE="$ROOT_DIR/test/materials/mat.filamat"
LIT_MATERIAL_SOURCE="$ROOT_DIR/test/materials/lit_shadow.mat"
LIT_MATERIAL_PACKAGE="$ROOT_DIR/test/materials/lit_shadow.filamat"
MATERIAL_COMPILER="$ROOT_DIR/test/materials/matc"

# If the sample .mat exists and local matc is available, compile it and run fixture-backed checks.
if [[ -z "${FILA_TEST_MATERIAL_PACKAGE:-}" && -f "$MATERIAL_SOURCE" ]]; then
	if [[ -x "$MATERIAL_COMPILER" ]]; then
		echo "Compiling material fixture: $MATERIAL_SOURCE -> $MATERIAL_PACKAGE"
		"$MATERIAL_COMPILER" -a all -p all -o "$MATERIAL_PACKAGE" "$MATERIAL_SOURCE"
		export FILA_TEST_MATERIAL_PACKAGE="$MATERIAL_PACKAGE"
	else
		echo "Local matc not found at $MATERIAL_COMPILER; running without fixture-backed material checks"
	fi
fi

# Compile a dedicated lit fixture for first-frame lit+shadow coverage when available.
if [[ -z "${FILA_TEST_LIT_MATERIAL_PACKAGE:-}" && -f "$LIT_MATERIAL_SOURCE" ]]; then
	if [[ -x "$MATERIAL_COMPILER" ]]; then
		echo "Compiling lit material fixture: $LIT_MATERIAL_SOURCE -> $LIT_MATERIAL_PACKAGE"
		"$MATERIAL_COMPILER" -a all -p all -o "$LIT_MATERIAL_PACKAGE" "$LIT_MATERIAL_SOURCE"
		export FILA_TEST_LIT_MATERIAL_PACKAGE="$LIT_MATERIAL_PACKAGE"
	fi
fi

cmake .. -DFILA_ENABLE_LINKED_TESTS=ON
cmake --build . --target filament_c_wrapper test_functionality
ctest --output-on-failure -L functionality

echo "Functionality tests completed."

