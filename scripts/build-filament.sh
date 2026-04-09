#!/bin/bash

# Configuration
DEFAULT_VERSION="1.70.1"
VERSION=${1:-$DEFAULT_VERSION}

# Determine Project Root (parent of the scripts directory)
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

PREBUILTS_DIR="$PROJECT_ROOT/prebuilts"
SOURCE_ROOT="$PROJECT_ROOT/filament-source"
TARBALL="$SOURCE_ROOT/v${VERSION}.tar.gz"
EXTRACTED_DIR="$SOURCE_ROOT/filament-${VERSION}"

# Filament Github Source
FILAMENT_SOURCE_URL="https://github.com/google/filament/archive/refs/tags/v${VERSION}.tar.gz"

# Build Flags for optimization
# -S: Skip samples
# -L: Enable LTO (smaller/faster binaries)
# -i: Install (moves artifacts to out/target/filament)
BUILD_FLAGS="-S -L -i release"

usage() {
    echo "Usage: $0 [version] [--ios] [--ios-simulator] [--macos] [--cleanup]"
    echo "  --cleanup: Delete the source directory after the build (default is to keep it)"
    echo "  Default version: $DEFAULT_VERSION"
    exit 1
}

cleanup_source() {
    echo "Cleaning up Filament source directory..."
    rm -rf "$SOURCE_ROOT"
}

build_target() {
    local platform=$1
    local konan_target=$2
    local extra_flags=$3
    local out_subdir=$4

    echo "----------------------------------------------------"
    echo "Building Filament for $konan_target ($platform)..."
    echo "----------------------------------------------------"

    cd "$EXTRACTED_DIR"
    
    # Execute build.sh with optimized flags
    ./build.sh $BUILD_FLAGS $extra_flags $platform

    local dest_lib_dir="$PREBUILTS_DIR/$konan_target/lib"
    mkdir -p "$dest_lib_dir"

    # Find the library directory in the install output
    local src_lib_dir="$EXTRACTED_DIR/out/$out_subdir/filament/lib"
    
    # Ensure correctly found path
    if [ ! -d "$src_lib_dir" ]; then
        src_lib_dir=$(find out -name "lib" -type d | grep "$platform" | head -n 1)
    fi

    if [ -d "$src_lib_dir" ]; then
        echo "Bulk extracting all libraries from $src_lib_dir to $dest_lib_dir..."
        # On Mac, some libs might be nested or have specific names
        cp -v "$src_lib_dir"/*.a "$dest_lib_dir/" 2>/dev/null
        cp -v "$src_lib_dir"/*.lib "$dest_lib_dir/" 2>/dev/null
    else
        echo "  Error: Could not find build output library directory for $platform."
    fi
}

# START SCRIPT
if [ "$#" -eq 0 ]; then usage; fi

mkdir -p "$SOURCE_ROOT"

# Check if tarball already exists
if [ ! -f "$TARBALL" ]; then
    echo "Downloading Filament $VERSION source..."
    curl -L "$FILAMENT_SOURCE_URL" -o "$TARBALL"
else
    echo "Filament $VERSION source already downloaded at $TARBALL"
fi

# Check if already extracted
if [ ! -d "$EXTRACTED_DIR" ]; then
    echo "Extracting Filament source..."
    tar -xzf "$TARBALL" -C "$SOURCE_ROOT"
else
    echo "Filament source already extracted in $EXTRACTED_DIR"
fi

# Build desktop tools first (required by Filament for all cross-compiling)
# Even if building for iOS, we MUST build desktop tools on the host.
echo "----------------------------------------------------"
echo "Building Host Desktop Tools (Required for matc/resgen)..."
echo "----------------------------------------------------"
cd "$EXTRACTED_DIR"
./build.sh $BUILD_FLAGS desktop

# Shift past version if provided
if [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+ ]]; then
    if [[ "$1" == "$VERSION" ]]; then
        shift
    fi
fi

DO_CLEANUP=false

# Process Target Flags
for arg in "$@"; do
    case $arg in
        --ios)
            build_target "ios" "iosArm64" "" "ios-release"
            ;;
        --ios-simulator)
            build_target "ios" "iosSimulatorArm64" "-s" "ios-simulator-release"
            ;;
        --macos)
            build_target "desktop" "macosArm64" "" "release"
            ;;
        --cleanup)
            DO_CLEANUP=true
            ;;
        *)
            # Skip if it looks like a version string
            if ! [[ "$arg" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
                echo "Unknown target: $arg"
            fi
            ;;
    esac
done

if [ "$DO_CLEANUP" = true ]; then
    cleanup_source
else
    echo "Keeping Filament source at $SOURCE_ROOT for future incremental builds."
fi

echo "----------------------------------------------------"
echo "Filament build and bulk extraction completed!"
echo "Target prebuilts are in: $PREBUILTS_DIR"
echo "----------------------------------------------------"
