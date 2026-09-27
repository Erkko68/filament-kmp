#!/usr/bin/env bash
#
# Build Filament's static libraries at the current filaVersion for the JVM hosts upstream
# publishes no release for — macosX64 (Intel Mac) and mingwArm64 (Windows on ARM) — and copy
# them to prebuilts/<target>/lib, where c/CMakeLists.txt links them like downloaded prebuilts.
#
# Usage: scripts/dev/build-host-libs.sh macosX64|mingwArm64 [-f]   (-f rebuilds even if the stamp matches)
#
# Runs natively on the target host (matc & co. execute during the build); on Windows from Git
# Bash with MSVC. Reuses upgrade-diff.sh's clone (scripts/dev/.filament-src-cache), checking out
# the tag in it. First run is a full Filament build (host tools + libs).

set -euo pipefail

TARGET="${1:?usage: $0 macosX64|mingwArm64 [-f]}"
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CACHE_DIR="$ROOT/scripts/dev/.filament-src-cache"
OUT_DIR="$ROOT/prebuilts/$TARGET/lib"
INCLUDE_DIR="$ROOT/prebuilts/$TARGET/include"
BUILD_DIR="$CACHE_DIR/out/cmake-$TARGET-release"
INSTALL_DIR="$CACHE_DIR/out/$TARGET-release"
VERSION="$(sed -n 's/^filaVersion=//p' "$ROOT/gradle.properties")"
TAG="v$VERSION"
STAMP="$OUT_DIR/.prebuilt-source"

ARGS=(
    -DCMAKE_BUILD_TYPE=Release
    -DCMAKE_INSTALL_PREFIX="$INSTALL_DIR"
    -DFILAMENT_SKIP_SAMPLES=ON
    -DFILAMENT_SKIP_SDL2=ON
    -DFILAMENT_BUILD_TESTING=OFF
)
case "$TARGET" in
    macosX64) ARGS+=(-DCMAKE_OSX_ARCHITECTURES=x86_64) ;;
    # Mirrors upstream's build/windows/build-github.bat /MT variant (hardcoded to x64 there,
    # hence plain cmake): the JVM's own msvcp140.dll conflicts with /MD.
    mingwArm64) ARGS+=(-A ARM64 -DUSE_STATIC_CRT=ON -DFILAMENT_WINDOWS_CI_BUILD=ON -DFILAMENT_SUPPORTS_VULKAN=ON) ;;
    *) echo "unsupported target '$TARGET' (macosX64|mingwArm64)" >&2; exit 1 ;;
esac

if [[ "${2:-}" != "-f" && -f "$STAMP" && "$(cat "$STAMP")" == "$VERSION|local" ]]; then
    echo "prebuilts/$TARGET/lib already built for $TAG (pass -f to rebuild)"
    exit 0
fi

if [[ ! -d "$CACHE_DIR/.git" ]]; then
    git clone --filter=blob:none --no-checkout https://github.com/google/filament.git "$CACHE_DIR"
fi
git -C "$CACHE_DIR" rev-parse --verify --quiet "refs/tags/$TAG" >/dev/null \
    || git -C "$CACHE_DIR" fetch --depth 1 origin "refs/tags/$TAG:refs/tags/$TAG"
git -C "$CACHE_DIR" checkout --quiet --detach "$TAG"

if [[ "$TARGET" == mingwArm64 ]]; then
    # Patches are reverted on exit so the shared clone stays checkout-able for other tags.
    trap 'git -C "$CACHE_DIR" checkout --quiet -- CMakeLists.txt libs/bluegl/CMakeLists.txt' EXIT
    # BlueGL's only 64-bit Windows trampoline is x64 MASM; use the portable C++ one
    # (upstream ships it for 32-bit) on ARM64.
    sed -i 's/if(NOT IS_64_BIT)/if(NOT IS_64_BIT OR CMAKE_GENERATOR_PLATFORM STREQUAL "ARM64")/; s/if (WIN32 AND IS_64_BIT)/if (WIN32 AND IS_64_BIT AND NOT CMAKE_GENERATOR_PLATFORM STREQUAL "ARM64")/' \
        "$CACHE_DIR/libs/bluegl/CMakeLists.txt"
    # Filament rejects MSYS2 shells via $MSYSTEM, which Git Bash forwards to cmake even
    # when unset. The guard targets MSYS toolchains; this build uses MSVC, so drop it.
    sed -i 's/if(DEFINED ENV{MSYSTEM})/if(FALSE)/' "$CACHE_DIR/CMakeLists.txt"
fi

cmake -S "$CACHE_DIR" -B "$BUILD_DIR" "${ARGS[@]}"
cmake --build "$BUILD_DIR" --target install --config Release --parallel

rm -rf "$OUT_DIR" "$INCLUDE_DIR"
mkdir -p "$OUT_DIR" "$INCLUDE_DIR/gltfio/materials"
find "$INSTALL_DIR/lib" -type f \( -name '*.a' -o -name '*.lib' \) -exec cp {} "$OUT_DIR/" \;
# resgen bakes the archive size into this header, so this libuberarchive needs its own copy
# (the shared include/ one carries the release tarballs' sizes); c/CMakeLists.txt puts it first.
cp "$INSTALL_DIR/include/gltfio/materials/uberarchive.h" "$INCLUDE_DIR/gltfio/materials/"
echo "$VERSION|local" > "$STAMP"
echo "Copied $(find "$OUT_DIR" -type f \( -name '*.a' -o -name '*.lib' \) | wc -l | tr -d ' ') $TARGET libraries for $TAG to prebuilts/$TARGET/lib"
