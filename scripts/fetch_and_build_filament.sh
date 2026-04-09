#!/bin/bash

# Configuration
DEFAULT_VERSION="1.70.1"
VERSION=${1:-$DEFAULT_VERSION}

# Determine Project Root (parent of the scripts directory)
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

PREBUILTS_DIR="$PROJECT_ROOT/prebuilts"
TEMP_DIR="$PROJECT_ROOT/temp_filament_build"


# Filament Github Source
FILAMENT_SOURCE_URL="https://github.com/google/filament/archive/refs/tags/v${VERSION}.tar.gz"

usage() {
    echo "Usage: $0 [version] [--ios] [--ios-simulator] [--macos]"
    echo "Default version: $DEFAULT_VERSION"
    exit 1
}

cleanup() {
    echo "Performing deep cleanup of temporary files..."
    cd "$PROJECT_ROOT"
    rm -rf "$TEMP_DIR"
}

build_target() {
    local platform=$1
    local konan_target=$2
    local flags=$3
    local out_subdir=$4

    echo "----------------------------------------------------"
    echo "Building Filament for $konan_target ($platform)..."
    echo "----------------------------------------------------"

    cd "$TEMP_DIR/filament-$VERSION"
    # Execute build.sh with release and install flags
    ./build.sh $flags -i release

    local dest_lib_dir="$PREBUILTS_DIR/$konan_target/lib"
    mkdir -p "$dest_lib_dir"

    # Find the library directory in the install output
    # Usually in out/<target>/filament/lib
    local src_lib_dir="$TEMP_DIR/filament-$VERSION/out/$out_subdir/filament/lib"
    
    # Check if we built arm64 or x64 (Filament might use different subdir names)
    if [ ! -d "$src_lib_dir" ]; then
        # Fallback to finding the library directory if expected path differs
        src_lib_dir=$(find out -name "lib" -type d | grep "$platform" | head -n 1)
    fi

    if [ -d "$src_lib_dir" ]; then
        echo "Bulk extracting all libraries from $src_lib_dir to $dest_lib_dir..."
        cp -v "$src_lib_dir"/*.a "$dest_lib_dir/" 2>/dev/null
        cp -v "$src_lib_dir"/*.lib "$dest_lib_dir/" 2>/dev/null
    else
        echo "  Error: Could not find build output library directory for $platform."
    fi
}

# START SCRIPT
if [ "$#" -eq 0 ]; then usage; fi

# Clear any previous partial builds
rm -rf "$TEMP_DIR"
mkdir -p "$TEMP_DIR"

echo "Downloading Filament $VERSION source..."
curl -L "$FILAMENT_SOURCE_URL" -o "$TEMP_DIR/filament.tar.gz"
tar -xzf "$TEMP_DIR/filament.tar.gz" -C "$TEMP_DIR"

# Build desktop tools first (required for all cross-compilation)
echo "----------------------------------------------------"
echo "Building required Host Desktop Tools..."
echo "----------------------------------------------------"
cd "$TEMP_DIR/filament-$VERSION"
./build.sh -p desktop -i release

# Skip past version if provided
if [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+ ]]; then
    if [[ "$1" == "$VERSION" ]]; then
        shift
    fi
fi

# Process Target Flags
for arg in "$@"; do
    case $arg in
        --ios)
            # Default Metal backend for iOS
            build_target "ios" "iosArm64" "-p ios" "ios-release"
            ;;
        --ios-simulator)
            # Default Metal backend for iOS Simulator (Apple Silicon)
            build_target "ios-simulator" "iosSimulatorArm64" "-p ios -s" "ios-simulator-release"
            ;;
        --macos)
            # Desktop build on Mac is macOS build
            build_target "desktop" "macosArm64" "-p desktop" "release"
            ;;
        *)
            echo "Unknown target: $arg"
            ;;
    esac
done

# Perform deep cleanup as requested
cleanup

echo "----------------------------------------------------"
echo "Filament build and bulk extraction completed!"
echo "Target prebuilts are located in: $PREBUILTS_DIR"
echo "----------------------------------------------------"
