#!/usr/bin/env bash
#
# Recompile every committed .filamat with the matc of the current filaVersion, and
# refresh the web sample's vendored engine copy. Run on every filaVersion bump whose
# MATERIAL_VERSION changed — the engine rejects blobs built by another version.
#
# Usage: scripts/dev/rebuild-materials.sh
#
# matc comes from the release tarball already cached by ./gradlew downloadPrebuilts.

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT"

VERSION="$(sed -n 's/^filaVersion=//p' gradle.properties)"
case "$(uname -s)" in
    Darwin) SUFFIX=mac ;;
    Linux)  SUFFIX=linux ;;
    *)      SUFFIX=windows ;;
esac
TARBALL=".gradle/filament-prebuilts-cache/filament-v${VERSION}-${SUFFIX}.tgz"

[[ -f "$TARBALL" ]] || ./gradlew downloadPrebuilts

WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT
tar -xzf "$TARBALL" -C "$WORK" --strip-components=1 filament/bin/matc
MATC="$WORK/bin/matc"

# Every .mat in the repo is the source of a sibling .filamat. Variant filters live in the .mat.
while IFS= read -r mat; do
    echo "  matc ${mat#./}"
    "$MATC" -p all -a all -o "${mat%.mat}.filamat" "$mat"
done < <(find kotlin samples -name '*.mat' -not -path '*/build/*')

# emissive.filamat is shared with filament-compose's tests as a committed copy.
cp kotlin/filament/src/commonTest/materials/emissive.filamat \
   kotlin/filament-compose/src/commonTest/resources/emissive.filamat

# The web sample vendors the engine so it runs standalone; a new blob won't load in the old one.
for target in jsMain wasmJsMain; do
    cp prebuilts/web/filament.js prebuilts/web/filament.wasm "samples/webApp/src/$target/resources/"
done

echo "Rebuilt with matc $VERSION."
