#!/usr/bin/env bash
# Builds Filament's static libraries from source into prebuilts/<target>/lib, for the
# JVM hosts upstream releases don't ship: macosX64 (Intel Mac) and mingwArm64 (Windows
# on ARM). Must run natively on the target host (matc & co. run during the build).
#
#   scripts/ci/build-filament-from-source.sh macosX64|mingwArm64
#
# Windows: run from Git Bash; cmake picks the Visual Studio generator on its own.
set -euo pipefail

target="${1:?usage: $0 macosX64|mingwArm64}"
root="$(cd "$(dirname "$0")/../.." && pwd)"
version="$(grep '^filaVersion=' "$root/gradle.properties" | cut -d= -f2)"
work="${FILAMENT_SRC_WORK_DIR:-$root/.gradle/filament-src}"
src="$work/filament-$version"
out="$root/prebuilts/$target/lib"

args=(
    -DCMAKE_BUILD_TYPE=Release
    -DCMAKE_INSTALL_PREFIX="$work/install-$target"
    -DFILAMENT_SKIP_SAMPLES=ON
    -DFILAMENT_SKIP_SDL2=ON
    -DFILAMENT_BUILD_TESTING=OFF
)
case "$target" in
    macosX64)
        args+=(-DCMAKE_OSX_ARCHITECTURES=x86_64)
        ;;
    mingwArm64)
        # Mirrors upstream's build/windows/build-github.bat /MT variant (the JVM's own
        # msvcp140.dll conflicts with /MD, see FilamentDownloads.TARGETS).
        args+=(-A ARM64 -DUSE_STATIC_CRT=ON -DFILAMENT_WINDOWS_CI_BUILD=ON -DFILAMENT_SUPPORTS_VULKAN=ON)
        ;;
    *) echo "unsupported target '$target'" >&2; exit 1 ;;
esac

if [[ ! -d "$src" ]]; then
    mkdir -p "$work"
    git clone --quiet --depth 1 --branch "v$version" https://github.com/google/filament.git "$src"
fi

if [[ "$target" == mingwArm64 ]]; then
    # BlueGL's only 64-bit Windows trampoline is x64 MASM; use the portable C++ one
    # (upstream ships it for 32-bit) on ARM64. Idempotent.
    sed -i 's/if(NOT IS_64_BIT)/if(NOT IS_64_BIT OR CMAKE_GENERATOR_PLATFORM STREQUAL "ARM64")/; s/if (WIN32 AND IS_64_BIT)/if (WIN32 AND IS_64_BIT AND NOT CMAKE_GENERATOR_PLATFORM STREQUAL "ARM64")/' \
        "$src/libs/bluegl/CMakeLists.txt"
fi

# Filament rejects MSYS2 environments (Git Bash sets MSYSTEM); plain cmake+MSVC is fine.
unset MSYSTEM

cmake -S "$src" -B "$work/cmake-$target" "${args[@]}"
cmake --build "$work/cmake-$target" --target install --config Release --parallel

rm -rf "$out"
mkdir -p "$out"
find "$work/install-$target/lib" -type f \( -name '*.a' -o -name '*.lib' \) -exec cp {} "$out/" \;
echo "$version|source" > "$out/.prebuilt-source"
echo "installed $(find "$out" -type f \( -name '*.a' -o -name '*.lib' \) | wc -l) libs -> $out"
