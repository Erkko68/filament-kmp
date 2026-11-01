#!/usr/bin/env bash
#
# What changed upstream between two Filament tags that this repo has to react to.
# Reads the filament submodule, which is reference only: nothing builds from it.
#
# Usage:
#   scripts/upgrade-diff.sh [<old-tag>] [<new-tag>]
#
# Tags are optional:
#   no tags     — old = filaVersion from gradle.properties, new = latest upstream release.
#   one tag     — old = filaVersion from gradle.properties, new = given tag.
#   two tags    — explicit old and new.
#
# Prints the MATERIAL_VERSION and CONFIG_MAX_* changes, new or changed feature flags,
# toolchain pins that differ from gradle.properties, the new release notes, and a file
# list of the surfaces our bindings depend on. Read full diffs with plain git:
#   git -C filament diff v1.78.0..v1.79.0 -- filament/include

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FILAMENT_DIR="$REPO_ROOT/filament"

# Paths the bindings depend on, relative to the Filament repo root.
SURFACES=(
  filament/include
  filament/backend/include
  libs/filabridge/include/filament/MaterialEnums.h
  libs/filabridge/include/private/filament/EngineEnums.h
  libs/filabridge/include/private/filament/UibStructs.h
  libs/utils/include/private/utils/FeatureFlagManager.h
  libs/utils/include/utils/EntityManager.h
  libs/gltfio/include
  libs/filamat/include
  libs/ktxreader/include
  libs/iblprefilter/include
  libs/camutils/include
  libs/geometry/include
  libs/image/include/image/Ktx1Bundle.h
  web/filament-js/CMakeLists.txt
  BUILDING.md
  build/common/versions
)

fil() { git -C "$FILAMENT_DIR" "$@"; }
property() { grep -E "^$1=" "$REPO_ROOT/gradle.properties" | cut -d= -f2; }
heading() { printf '\n## %s\n' "$1"; }
# Indents stdin, or says there was nothing.
indented() { sed 's/^/  /' | grep . || echo "  (none)"; }

case "${1:-}" in -*) sed -n '2,/^$/p' "$0" | sed -E 's/^# ?//'; exit ;; esac

if [[ ! -e "$FILAMENT_DIR/.git" ]]; then
  echo "The filament submodule isn't checked out. Get it with:" >&2
  echo "  git -C $REPO_ROOT submodule update --init --filter=blob:none filament" >&2
  exit 1
fi

fil fetch --quiet --tags origin || echo "Could not fetch tags, using the local ones." >&2

latest_tag() { fil tag -l 'v*' --sort=v:refname | grep -E '^v[0-9]+\.[0-9]+\.[0-9]+$' | tail -1; }

case $# in
  0) OLD="$(property filaVersion)"; NEW="$(latest_tag)" ;;
  1) OLD="$(property filaVersion)"; NEW="$1" ;;
  2) OLD="$1"; NEW="$2" ;;
  *) echo "At most two tags (old, new)." >&2; exit 1 ;;
esac
OLD="v${OLD#v}"
NEW="v${NEW#v}"
RANGE="$OLD..$NEW"
fil rev-parse "$OLD^{commit}" "$NEW^{commit}" >/dev/null

material_version() {
  fil show "$1:libs/filabridge/include/filament/MaterialEnums.h" | sed -nE 's/.*MATERIAL_VERSION[[:space:]]*=[[:space:]]*([0-9]+).*/\1/p'
}

# Prints a mismatch between a gradle.properties pin and upstream's build/common/versions.
check_pin() {
  local ours upstream
  ours="$(property "$1")"
  upstream="$(fil show "$NEW:build/common/versions" | sed -n "s/^$2=//p")"
  if [[ "$ours" == "$upstream" ]]; then echo "$1 $ours"; else echo "!! $1: $ours here, $upstream upstream"; fi
}

echo "Filament upgrade diff: $OLD → $NEW"

heading "MATERIAL_VERSION"
old_mv="$(material_version "$OLD")"
new_mv="$(material_version "$NEW")"
if [[ "$old_mv" != "$new_mv" ]]; then
  echo "  !! $old_mv → $new_mv: recompile every shipped .filamat with the $NEW matc."
else
  echo "  Unchanged ($old_mv). Still check UibStructs.h: repacks without a bump have happened (filament#10188)."
fi

heading "CONFIG_MAX_* and engine constants"
fil diff "$RANGE" -- libs/filabridge/include/private/filament/EngineEnums.h libs/filabridge/include/filament/MaterialEnums.h \
  | awk '/^[-+][^-+]/ && /CONFIG_|MAX_/' | indented

heading "Feature flags added, removed or with a new default"
fil diff "$RANGE" -- libs/utils/include/private/utils/FeatureFlagManager.h \
  | awk '/^[-+][[:space:]]*bool /' | sed -E 's/^([-+])[[:space:]]+/\1 /' | indented

heading "Toolchain pins at $NEW (gradle.properties)"
{
  check_pin emsdkVersion GITHUB_EMSDK_VERSION
  check_pin ndkVersion GITHUB_NDK_VERSION
  check_pin androidCmakeVersion GITHUB_CMAKE_VERSION
} | indented

heading "Release notes"
fil diff "$RANGE" -- RELEASE_NOTES.md | awk '/^\+\+\+/ { next } /^\+/ { print substr($0, 2) }'

heading "Files changed on the surfaces we bind"
fil diff --stat=120 "$RANGE" -- "${SURFACES[@]}" | indented
