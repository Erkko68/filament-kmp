#!/usr/bin/env bash
# Regenerates Kotlin/JS externals for Filament.js from upstream filament.d.ts
# using Karakum (https://github.com/karakum-team/karakum).
#
# Workflow:
#   1. Read filaVersion from gradle.properties.
#   2. Extract web/filament-js/filament.d.ts from the shallow clone kept by
#      scripts/upgrade-diff.sh (scripts/.filament-src-cache). If that clone
#      doesn't have the tag yet, fetch it.
#   3. Run karakum (scripts/karakum/) against the .d.ts.
#   4. Mirror the generated tree into js/src/jsMain/kotlin/, replacing any
#      previous generation.
#
# Run on every filaVersion bump alongside scripts/check-js-bindings.sh.
set -euo pipefail

REPO_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
KARAKUM_DIR="$REPO_ROOT/scripts/karakum"
CACHE_REPO="$REPO_ROOT/scripts/.filament-src-cache"
OUT_DIR="$REPO_ROOT/js/src/jsMain/kotlin"

FILA_VERSION="$(grep '^filaVersion=' "$REPO_ROOT/gradle.properties" | cut -d= -f2)"
[[ -n "$FILA_VERSION" ]] || { echo "ERROR: filaVersion not found in gradle.properties" >&2; exit 1; }
TAG="v$FILA_VERSION"

echo "[karakum] target filament version: $TAG"

# 1. Ensure the upstream shallow clone has the tag we need.
if [[ ! -d "$CACHE_REPO/.git" ]]; then
    echo "[karakum] cloning google/filament (shallow)..."
    git clone --filter=blob:none --no-checkout https://github.com/google/filament "$CACHE_REPO"
fi
if ! git -C "$CACHE_REPO" rev-parse --verify "$TAG" >/dev/null 2>&1; then
    echo "[karakum] fetching tag $TAG..."
    git -C "$CACHE_REPO" fetch --depth=1 origin "tag" "$TAG" --no-tags
fi

# 2. Pull filament.d.ts at the tag into the karakum workspace.
git -C "$CACHE_REPO" show "$TAG:web/filament-js/filament.d.ts" > "$KARAKUM_DIR/filament.d.ts"
echo "[karakum] extracted filament.d.ts ($(wc -l < "$KARAKUM_DIR/filament.d.ts") lines)"

# 3. Install karakum's node deps if missing. Karakum resolves libraryName via
#    findPackageJSON, so we also stage a stub 'filament' package inside the
#    workspace's node_modules pointing at the extracted .d.ts.
if [[ ! -d "$KARAKUM_DIR/node_modules/karakum" ]]; then
    echo "[karakum] npm install..."
    (cd "$KARAKUM_DIR" && npm install --silent)
fi
STUB="$KARAKUM_DIR/node_modules/filament"
mkdir -p "$STUB"
cat > "$STUB/package.json" <<EOF
{"name":"filament","version":"$FILA_VERSION","main":"filament.d.ts","types":"filament.d.ts"}
EOF
cp "$KARAKUM_DIR/filament.d.ts" "$STUB/filament.d.ts"

# 4. Generate.
rm -rf "$KARAKUM_DIR/generated"
(cd "$KARAKUM_DIR" && npx karakum --config karakum.config.json >/dev/null)
COUNT="$(find "$KARAKUM_DIR/generated" -name '*.kt' | wc -l | tr -d ' ')"
echo "[karakum] generated $COUNT .kt files"

# 5. Mirror into js/src/jsMain/kotlin/filament/. Karakum places everything in
#    `package filament` (one file per top-level declaration). The existing
#    hand-written externals in `package io.github.erkko68.filament.js` are
#    untouched — actuals migrate to the generated ones via `import filament.X
#    as JSX` one class at a time.
DEST_PKG="$OUT_DIR/filament"
rm -rf "$DEST_PKG"
mkdir -p "$DEST_PKG"
cp "$KARAKUM_DIR/generated"/*.kt "$DEST_PKG/"
echo "[karakum] copied to ${DEST_PKG#$REPO_ROOT/}"
