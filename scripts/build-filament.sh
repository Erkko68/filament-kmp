#!/bin/bash
set -e

# --- Configuration ---
VERSION=${1:-"1.70.1"}
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
PREBUILTS_DIR="$PROJECT_ROOT/prebuilts"
SOURCE_ROOT="$PROJECT_ROOT/filament-source"
TARBALL="$SOURCE_ROOT/v${VERSION}.tar.gz"
EXTRACTED_DIR="$SOURCE_ROOT/filament-${VERSION}"

FILAMENT_SOURCE_URL="https://github.com/google/filament/archive/refs/tags/v${VERSION}.tar.gz"
HOST_ARCH=$(uname -m)

usage() {
    echo "Usage: $0 [version] [--ios] [--ios-simulator] [--macos] [--cleanup]"
    exit 1
}

# --- Setup & Download ---
if [ "$#" -eq 0 ]; then usage; fi
if [[ "$1" =~ ^[0-9]+\.[0-9]+\.[0-9]+ ]]; then shift; fi

DO_IOS=false; DO_IOS_SIM=false; DO_MACOS=false; DO_CLEANUP=false
for arg in "$@"; do
    case $arg in
        --ios) DO_IOS=true ;;
        --ios-simulator) DO_IOS_SIM=true ;;
        --macos) DO_MACOS=true ;;
        --cleanup) DO_CLEANUP=true ;;
    esac
done

mkdir -p "$SOURCE_ROOT"
if [ ! -f "$TARBALL" ]; then
    echo "Downloading Filament $VERSION..."
    curl -L "$FILAMENT_SOURCE_URL" -o "$TARBALL"
fi

rm -rf "$EXTRACTED_DIR"
echo "Extracting Filament source..."
tar -xzf "$TARBALL" -C "$SOURCE_ROOT"

cd "$EXTRACTED_DIR"

# --- 1. Patch iOS Toolchain for Distinct Outputs ---
IOS_TOOLCHAIN="third_party/clang/iOS.cmake"
if ! grep -q "iphonesimulator" "$IOS_TOOLCHAIN"; then
    echo "Patching iOS toolchain for simulator output routing..."
    perl -pi -e 's/set\(DIST_ARCH \$\{IOS_ARCH\}\)/if(CMAKE_OSX_SYSROOT MATCHES "iphonesimulator")\n    set(DIST_ARCH "\${IOS_ARCH}-simulator")\nelse()\n    set(DIST_ARCH "\${IOS_ARCH}")\nendif()/g' "$IOS_TOOLCHAIN"
fi

# --- 2. Desktop Tools Build (REQUIRED for cross-compiling) ---
# We MUST build desktop tools to get 'matc', 'resgen', etc., before building iOS.
if [ "$DO_MACOS" = true ] || [ "$DO_IOS" = true ] || [ "$DO_IOS_SIM" = true ]; then
    echo "Building macOS desktop tools (Required for host compilation)..."
    
    # FIXED: Added the -p flag before desktop
    ./build.sh -p desktop -i release
    
    # Only export the Mac libraries if the user specifically asked for --macos
    if [ "$DO_MACOS" = true ]; then
        echo "Exporting macOS libraries..."
        mkdir -p "$PREBUILTS_DIR/macosArm64/lib"
        cp -Rv "out/release/filament/lib/$HOST_ARCH/"* "$PREBUILTS_DIR/macosArm64/lib/"
    fi
fi

# --- 3. iOS Device Build ---
if [ "$DO_IOS" = true ] || [ "$DO_IOS_SIM" = true ]; then
    echo "Building for iOS Device..."
    ./build.sh -p ios -i release 

    if [ "$DO_IOS" = true ]; then
        echo "Exporting iOS Device libraries..."
        mkdir -p "$PREBUILTS_DIR/iosArm64/lib"
        cp -Rv "out/ios-release/filament/lib/arm64/"* "$PREBUILTS_DIR/iosArm64/lib/"
    fi
fi

# --- 4. iOS Simulator Build (Custom CMake Invocation) ---
if [ "$DO_IOS_SIM" = true ]; then
    echo "Building for iOS Simulator ($HOST_ARCH)..."
    
    SIM_BUILD_DIR="out/cmake-ios-release-${HOST_ARCH}-simulator"
    mkdir -p "$SIM_BUILD_DIR"
    cd "$SIM_BUILD_DIR"

    # Explicitly point CMake to the desktop tools we built in Step 2
    cmake \
        -DCMAKE_BUILD_TYPE=Release \
        -DCMAKE_INSTALL_PREFIX="../ios-release/filament" \
        -DCMAKE_TOOLCHAIN_FILE="../../third_party/clang/iOS.cmake" \
        -DIOS_ARCH="$HOST_ARCH" \
        -DPLATFORM_NAME="iphonesimulator" \
        -DCMAKE_OSX_SYSROOT="iphonesimulator" \
        -DCMAKE_OSX_DEPLOYMENT_TARGET="14.0" \
        -DFILAMENT_SKIP_SAMPLES=ON \
        -DFILAMENT_SKIP_SDL2=ON \
        -DFILAMENT_SUPPORTS_OPENGL=OFF \
        -DFILAMENT_SUPPORTS_VULKAN=OFF \
        -DFILAMENT_ENABLE_LTO=ON \
        -DFILAMENT_BUILD_TESTING=OFF \
        -DIMPORT_EXECUTABLES_DIR="out" \
        ../..

    cmake --build . --target install

    cd ../.. # Go back to Filament root

    echo "Exporting iOS Simulator libraries..."
    mkdir -p "$PREBUILTS_DIR/iosSimulatorArm64/lib"
    
    if [ ! -d "out/ios-release/filament/lib/${HOST_ARCH}-simulator" ]; then
         echo "ERROR: Simulator output folder not generated! CMake failed."
         exit 1
    fi
    
    cp -Rv "out/ios-release/filament/lib/${HOST_ARCH}-simulator/"* "$PREBUILTS_DIR/iosSimulatorArm64/lib/"
fi

# --- Cleanup ---
if [ "$DO_CLEANUP" = true ]; then
    echo "Cleaning up source directory..."
    rm -rf "$SOURCE_ROOT"
fi

echo "✅ Build and export completed successfully!"