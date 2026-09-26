#!/usr/bin/env bash
#
# Install the Emscripten SDK into .emsdk/ (gitignored), pinned to the version upstream
# Filament builds its wasm with (BUILDING.md). Idempotent: re-running is a no-op once installed.
#
# Usage: scripts/dev/setup-emsdk.sh          then: source .emsdk/emsdk_env.sh
#        EMSDK_VERSION=x.y.z scripts/dev/setup-emsdk.sh

set -euo pipefail

# Bump alongside filaVersion when upstream BUILDING.md changes its emsdk tag.
EMSDK_VERSION="${EMSDK_VERSION:-5.0.4}"

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
DIR="$ROOT/.emsdk"
STAMP="$DIR/.installed-version"

if [[ -f "$STAMP" && "$(cat "$STAMP")" == "$EMSDK_VERSION" ]]; then
    echo "emsdk $EMSDK_VERSION already installed in .emsdk/"
else
    rm -rf "$DIR"
    mkdir -p "$DIR"
    curl -fsSL "https://github.com/emscripten-core/emsdk/archive/refs/tags/${EMSDK_VERSION}.tar.gz" \
        | tar -xz -C "$DIR" --strip-components=1
    "$DIR/emsdk" install "$EMSDK_VERSION"
    "$DIR/emsdk" activate --embedded "$EMSDK_VERSION"
    echo "$EMSDK_VERSION" > "$STAMP"
fi

echo "Activate with: source .emsdk/emsdk_env.sh"
