#!/usr/bin/env bash
#
# Build Filament's static libraries for wasm at the current filaVersion and copy them to
# prebuilts/wasm/lib (plus build-generated headers to prebuilts/wasm/include), the inputs for
# linking our C API into a wasm module (docs/design/web-c-api-bindings.md). Upstream publishes
# no wasm .a files, so we build them.
#
# Usage: scripts/dev/build-wasm-libs.sh [-f]      (-f rebuilds even if the stamp matches)
#
# Reuses upgrade-diff.sh's clone (scripts/dev/.filament-src-cache), checking out the tag in it,
# and the emsdk from setup-emsdk.sh. First run is a full Filament build (host tools + wasm).

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CACHE_DIR="$ROOT/scripts/dev/.filament-src-cache"
OUT_DIR="$ROOT/prebuilts/wasm/lib"
INCLUDE_DIR="$ROOT/prebuilts/wasm/include"
VERSION="$(sed -n 's/^filaVersion=//p' "$ROOT/gradle.properties")"
TAG="v$VERSION"
STAMP="$OUT_DIR/.prebuilt-source"

if [[ "${1:-}" != "-f" && -f "$STAMP" && "$(cat "$STAMP")" == "$VERSION|local" ]]; then
    echo "prebuilts/wasm/lib already built for $TAG (pass -f to rebuild)"
    exit 0
fi

"$ROOT/scripts/dev/setup-emsdk.sh" >/dev/null
export EMSDK="$ROOT/.emsdk"

if [[ ! -d "$CACHE_DIR/.git" ]]; then
    git clone --filter=blob:none --no-checkout https://github.com/google/filament.git "$CACHE_DIR"
fi
git -C "$CACHE_DIR" rev-parse --verify --quiet "refs/tags/$TAG" >/dev/null \
    || git -C "$CACHE_DIR" fetch --depth 1 origin "refs/tags/$TAG:refs/tags/$TAG"
git -C "$CACHE_DIR" checkout --quiet --detach "$TAG"

(cd "$CACHE_DIR" && ./build.sh -p wasm release)
# Upstream's wasm build skips filamat; turn it on in the same build dir for filamat-kmp.wasm.
(cd "$CACHE_DIR/out/cmake-wasm-release" && . "$EMSDK/emsdk_env.sh" >/dev/null 2>&1 \
    && cmake -DFILAMENT_BUILD_FILAMAT=ON . >/dev/null && ninja filamat)

rm -rf "$OUT_DIR" "$INCLUDE_DIR"
mkdir -p "$OUT_DIR" "$INCLUDE_DIR/gltfio/materials"
find "$CACHE_DIR/out/cmake-wasm-release" -name '*.a' -not -path '*/CMakeFiles/*' -exec cp {} "$OUT_DIR/" \;
# Like upstream's install step: libfilamat.a is the combined archive (glslang, SPIRV-Tools/Cross).
cp "$CACHE_DIR/out/cmake-wasm-release/libs/filamat/libfilamat_combined.a" "$OUT_DIR/libfilamat.a"
# resgen bakes the archive size into this header, so the wasm libuberarchive needs its own copy.
cp "$CACHE_DIR/out/cmake-wasm-release/libs/gltfio/materials/uberarchive.h" "$INCLUDE_DIR/gltfio/materials/"
echo "$VERSION|local" > "$STAMP"
echo "Copied $(ls "$OUT_DIR"/*.a | wc -l | tr -d ' ') wasm libraries for $TAG to prebuilts/wasm/lib"
